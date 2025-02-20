package vn.iuh.fit.musical_instrument.services.iml;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iuh.fit.musical_instrument.dto.UserRegistrationDto;
import vn.iuh.fit.musical_instrument.entities.SecureToken;
import vn.iuh.fit.musical_instrument.entities.User;
import vn.iuh.fit.musical_instrument.repository.SecureTokenRepository;
import vn.iuh.fit.musical_instrument.repository.UserRepository;
import vn.iuh.fit.musical_instrument.services.EmailService;
import vn.iuh.fit.musical_instrument.services.UserService;

/**
 * Triển khai UserService
 * - Đăng ký user
 * - Gửi email xác thực
 * - Xác thực token
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecureTokenRepository secureTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           SecureTokenRepository secureTokenRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.secureTokenRepository = secureTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUserName(dto.getUserName()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 3. Tạo user mới, emailVerified = false
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmailVerified(false);  // Tài khoản chưa xác thực
        user.setStatus(1);             // Ví dụ: 1 = active, 0 = locked
        // Nếu DTO có trường phoneNumber, fullName, v.v. => set vào đây

        // 4. Lưu user vào DB
        User savedUser = userRepository.save(user);

        // 5. Tạo token xác thực bằng UUID
        SecureToken token = new SecureToken();
        String tokenValue = UUID.randomUUID().toString();
        token.setToken(tokenValue);
        token.setUser(savedUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(24)); // hết hạn sau 24h
        secureTokenRepository.save(token);

        // 6. Gửi email xác thực kèm link chứa token
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + tokenValue;
        String emailBody = "Please click the link to verify your account: "
                + "<a href=\"" + verificationLink + "\">Click here</a>";

        emailService.sendEmail(
                savedUser.getEmail(),
                "Account Verification",
                emailBody
        );

        return savedUser;
    }

    @Override
    public boolean verifyUser(String tokenValue) {
        // 1. Tìm SecureToken trong DB
        Optional<SecureToken> optToken = secureTokenRepository.findByToken(tokenValue);
        if (optToken.isEmpty()) {
            return false; // Không tìm thấy token => xác thực thất bại
        }

        SecureToken token = optToken.get();

        // 2. Kiểm tra nếu token đã được sử dụng
        if (token.isUsed()) {
            return false;
        }

        // 3. Kiểm tra token đã hết hạn chưa
        if (token.isExpired()) {
            // Nếu hết hạn, bạn có thể xóa token hoặc cập nhật trạng thái
            token.setUsed(true);
            secureTokenRepository.save(token);
            return false;
        }

        // 4. Kích hoạt emailVerified cho user
        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // 5. Đánh dấu token là đã sử dụng (không xóa, để lưu lịch sử)
        token.setUsed(true);
        secureTokenRepository.save(token);

        return true;
    }

}
