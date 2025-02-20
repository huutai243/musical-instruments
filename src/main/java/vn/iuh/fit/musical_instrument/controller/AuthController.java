package vn.iuh.fit.musical_instrument.controller;

import vn.iuh.fit.musical_instrument.dto.request.LoginRequest;
import vn.iuh.fit.musical_instrument.dto.request.RegistrationDto;
import vn.iuh.fit.musical_instrument.dto.responses.AuthResponse;
import vn.iuh.fit.musical_instrument.entities.RefreshToken;
import vn.iuh.fit.musical_instrument.entities.User;
import vn.iuh.fit.musical_instrument.repository.RefreshTokenRepository;
import vn.iuh.fit.musical_instrument.repository.UserRepository;
import vn.iuh.fit.musical_instrument.services.JwtService;
import vn.iuh.fit.musical_instrument.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // Dependency Injection qua Constructor
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          RefreshTokenRepository refreshTokenRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Endpoint Đăng ký User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);
            return new ResponseEntity<>("Registration successful. Please check your email for verification link.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint Xác thực Email qua Token
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            return ResponseEntity.ok("Email verified successfully. Please login.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    // Endpoint Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("User {} is attempting to log in.", loginRequest.getUserName());

        // Kiểm tra User tồn tại không
        Optional<User> userOpt = userRepository.findByUserName(loginRequest.getUserName());
        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid username or password", null, null));
        }

        // Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );

        // Tạo Access Token và Refresh Token
        String accessToken = jwtService.generateAccessToken(loginRequest.getUserName());
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getUserName());

        // Lưu Refresh Token vào DB
        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken(refreshToken);
        savedToken.setUser(userOpt.get());
        savedToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(savedToken);

        return ResponseEntity.ok(new AuthResponse("Login successful", accessToken, refreshToken));
    }

    // Endpoint Làm mới Access Token bằng Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid or expired refresh token", null, null));
        }

        // Tạo Access Token mới từ Refresh Token hợp lệ
        User user = tokenOpt.get().getUser();
        String newAccessToken = jwtService.generateAccessToken(user.getUserName());

        return ResponseEntity.ok(new AuthResponse("Token refreshed", newAccessToken, refreshToken));
    }
}
