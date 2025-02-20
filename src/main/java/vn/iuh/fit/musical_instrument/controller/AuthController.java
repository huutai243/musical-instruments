package vn.iuh.fit.musical_instrument.controller;

import org.springframework.web.servlet.view.RedirectView;
import vn.iuh.fit.musical_instrument.dto.UserRegistrationDto;
import vn.iuh.fit.musical_instrument.entities.User;
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
    // Endpoint xác thực email và chuyển hướng về trang đăng nhập
    @GetMapping("/verify")
    public RedirectView verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyUser(token);
        RedirectView redirectView = new RedirectView();
        if (verified) {
            // Nếu xác thực thành công, chuyển hướng đến trang login (có thể là http://localhost:8080/login)
            redirectView.setUrl("http://localhost:8080/login");
        } else {
            // Nếu không thành công, chuyển hướng đến trang login với thông báo lỗi
            redirectView.setUrl("http://localhost:8080/login?error=invalidOrExpiredToken");
        }
        return redirectView;
    }
}

