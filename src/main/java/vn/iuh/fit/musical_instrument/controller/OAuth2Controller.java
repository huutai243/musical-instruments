package vn.iuh.fit.musical_instrument.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import vn.iuh.fit.musical_instrument.dto.responses.AuthResponse;
import vn.iuh.fit.musical_instrument.entities.RefreshToken;
import vn.iuh.fit.musical_instrument.entities.User;
import vn.iuh.fit.musical_instrument.repository.RefreshTokenRepository;
import vn.iuh.fit.musical_instrument.repository.UserRepository;
import vn.iuh.fit.musical_instrument.services.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public OAuth2Controller(UserRepository userRepository, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping("/success")
    public ResponseEntity<AuthResponse> oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");
        String provider = oauthUser.getAttribute("sub") != null ? "google" : "facebook";

        // Kiểm tra xem user đã tồn tại chưa
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isEmpty()) {
            // Tạo user mới nếu chưa có
            user = new User();
            user.setUserName(email);  // Dùng email làm username
            user.setFullName(name);
            user.setEmail(email);
            user.setProfilePicture(picture);
            user.setProvider(provider);
            userRepository.save(user);
        } else {
            user = userOpt.get();
        }

        //  Tạo Access Token & Refresh Token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        //  Lưu Refresh Token vào DB
        saveRefreshToken(user, refreshToken);

        //  Trả về phản hồi giống API login thông thường
        return ResponseEntity.ok(new AuthResponse("OAuth2 Login Successful", accessToken, refreshToken));
    }

    /**
     *  Lưu Refresh Token vào database
     */
    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken(refreshToken);
        savedToken.setUser(user);
        savedToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(savedToken);
    }
}
