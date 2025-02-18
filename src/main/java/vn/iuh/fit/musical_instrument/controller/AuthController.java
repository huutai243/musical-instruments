package vn.iuh.fit.musical_instrument.controller;

import vn.iuh.fit.musical_instrument.dto.UserRegistrationDto;
import vn.iuh.fit.musical_instrument.entites.User;
import vn.iuh.fit.musical_instrument.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // Injection thông qua constructor
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint đăng ký
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);
            // Nếu đăng ký thành công, thông báo kiểm tra email để xác thực
            return new ResponseEntity<>("Registration successful. Please check your email for verification link.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Nếu username hoặc email đã tồn tại
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint xác thực email
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            // Nếu xác thực thành công, thông báo kích hoạt tài khoản và có thể chuyển hướng người dùng đến trang đăng nhập
            return ResponseEntity.ok("Account verified successfully. Please login.");
        } else {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }
    }
}

