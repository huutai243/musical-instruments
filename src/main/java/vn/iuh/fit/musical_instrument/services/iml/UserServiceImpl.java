package vn.iuh.fit.musical_instrument.services.iml;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iuh.fit.musical_instrument.dto.request.RegistrationDto;
import vn.iuh.fit.musical_instrument.entities.RefreshToken;
import vn.iuh.fit.musical_instrument.entities.SecureToken;
import vn.iuh.fit.musical_instrument.entities.User;
import vn.iuh.fit.musical_instrument.repository.SecureTokenRepository;
import vn.iuh.fit.musical_instrument.repository.UserRepository;
import vn.iuh.fit.musical_instrument.services.EmailService;
import vn.iuh.fit.musical_instrument.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final SecureTokenRepository secureTokenRepository; // Dùng cho email verification
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository,
                           SecureTokenRepository secureTokenRepository,

                           PasswordEncoder passwordEncoder,
                           EmailService emailService,

                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.secureTokenRepository = secureTokenRepository;

        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;

        this.authenticationManager = authenticationManager;
    }

    @Override
    public User registerUser(RegistrationDto dto) {
        logger.info("Registering user: {}", dto.getUserName());
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUserName(dto.getUserName()).isPresent()) {
            logger.warn("Username {} already exists", dto.getUserName());
            throw new IllegalArgumentException("Username already exists");
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Email {} already exists", dto.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // 3. Tạo user mới, emailVerified = false
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmailVerified(false);
        user.setStatus(1);

        // 4. Lưu user vào DB
        User savedUser = userRepository.save(user);
        logger.info("User {} registered successfully with id {}", savedUser.getUserName(), savedUser.getId());

        // 5. Tạo token xác thực bằng UUID
        SecureToken token = new SecureToken();
        String tokenValue = UUID.randomUUID().toString();
        token.setToken(tokenValue);
        token.setUser(savedUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        secureTokenRepository.save(token);
        logger.info("Verification token generated for user {}: {}", savedUser.getUserName(), tokenValue);

        // 6. Gửi email xác thực
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + tokenValue;
        String emailBody = "Please click the link to verify your account: "
                + "<a href=\"" + verificationLink + "\">Click here</a>";
        emailService.sendEmail(savedUser.getEmail(), "Account Verification", emailBody);
        logger.info("Verification email sent to {}", savedUser.getEmail());

        return savedUser;
    }

    @Override
    public boolean verifyUser(String tokenValue) {
        logger.info("Verifying token: {}", tokenValue);
        Optional<SecureToken> optToken = secureTokenRepository.findByToken(tokenValue);
        if (optToken.isEmpty()) {
            logger.warn("Verification token {} not found", tokenValue);
            return false;
        }
        SecureToken token = optToken.get();

        if (token.isUsed()) {
            logger.warn("Token {} has already been used", tokenValue);
            return false;
        }

        if (token.isExpired()) {
            logger.warn("Token {} is expired", tokenValue);
            token.setUsed(true);
            secureTokenRepository.save(token);
            return false;
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        logger.info("User {} email verified", user.getUserName());

        token.setUsed(true);
        secureTokenRepository.save(token);
        return true;
    }

}
