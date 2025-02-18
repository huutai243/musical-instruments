package vn.iuh.fit.musical_instrument.entites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @NotBlank
    @Email
    @Column(name = "user_email", nullable = false, unique = true, length = 50)
    private String userEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Quan hệ OneToOne với ShoppingCart (mappedBy ở ShoppingCart)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private ShoppingCart shoppingCart;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<SecureToken> tokens;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "account_verified", nullable = false)
    private boolean accountVerified;
}
