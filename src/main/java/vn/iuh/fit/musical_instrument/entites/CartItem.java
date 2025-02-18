package vn.iuh.fit.musical_instrument.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    // Mỗi CartItem liên kết với 1 sản phẩm
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Mỗi CartItem thuộc về một ShoppingCart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart cart;
}

