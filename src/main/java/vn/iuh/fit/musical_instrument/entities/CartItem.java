package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Min(0)
    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    // Nhiều CartItem liên kết với 1 Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Nhiều CartItem thuộc về 1 ShoppingCart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart cart;
}
