package vn.iuh.fit.musical_instrument.entites;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCart extends BaseEntity {

    @Column(name = "total_items")
    private int totalItems;

    @Column(name = "total_prices")
    private double totalPrices;

    // Quan hệ OneToOne với User; sử dụng JoinColumn với tên user_id cho rõ ràng
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
}

