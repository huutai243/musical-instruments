package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "password") // Có thể null nếu đăng nhập bằng OAuth2
    private String password;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Ví dụ: trạng thái active, locked,...
    @Column(name = "status", nullable = false)
    private int status = 1; // Mặc định là active (1)

    // Đã xác thực tài khoản qua email hay chưa
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    // OAuth2 Provider (Google, Facebook)
    @Column(name = "provider")
    private String provider; // "google" hoặc "facebook"

    // Ảnh đại diện từ OAuth2
    @Column(name = "profile_picture")
    private String profilePicture;

    // OneToOne với ShoppingCart
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private ShoppingCart shoppingCart;

    // Nhiều Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    // Một người dùng có nhiều đơn hàng
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // Một người dùng có nhiều token (để xác thực email, reset password, etc.)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SecureToken> tokens;
}
