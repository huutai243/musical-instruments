package vn.iuh.fit.musical_instrument.services.iml;


import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iuh.fit.musical_instrument.dto.UserRegistrationDto;
import vn.iuh.fit.musical_instrument.entites.SecureToken;
import vn.iuh.fit.musical_instrument.entites.User;
import vn.iuh.fit.musical_instrument.repository.SecureTokenRepository;
import vn.iuh.fit.musical_instrument.repository.UserRepository;
import vn.iuh.fit.musical_instrument.services.EmailService;
import vn.iuh.fit.musical_instrument.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecureTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    // Giả sử bạn có một EmailService để gửi email
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, SecureTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        if(userRepository.findByUserName(dto.getUserName()).isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.findByUserEmail(dto.getUserEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserEmail(dto.getUserEmail());
        user.setAccountVerified(false);  // chưa kích hoạt

        User savedUser = userRepository.save(user);
        // Tạo token xác thực cho user
        SecureToken token = new SecureToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(savedUser);
        // Đặt thời gian hết hạn token là 24 giờ sau tạo
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);

        // Gửi email chứa link kích hoạt tới user
        String link = "http://yourdomain.com/api/auth/verify?token=" + token.getToken();
        emailService.sendEmail(savedUser.getUserEmail(), "Account Verification",
                "Please click the link to verify your account: " + link);

        return savedUser;
    }

    @Override
    public boolean verifyUser(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .map(token -> {
                    if (token.isExpired()) {
                        // Có thể xóa token hoặc thông báo lỗi hết hạn
                        tokenRepository.delete(token);
                        return false;
                    } else {
                        User user = token.getUser();
                        user.setAccountVerified(true);
                        userRepository.save(user);
                        // Sau khi kích hoạt, xóa token
                        tokenRepository.delete(token);
                        return true;
                    }
                }).orElse(false);
    }
}

