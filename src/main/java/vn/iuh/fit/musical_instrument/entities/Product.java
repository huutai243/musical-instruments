package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotBlank
    @Column(name = "product_img", nullable = false)
    private String productImage;

    @NotNull
    @Min(0)
    @Column(name = "product_price", nullable = false)
    private Long productPrice;

    @NotNull
    @Min(0)
    @Column(name = "product_stock", nullable = false)
    private int quantity;

    @NotBlank
    @Column(name = "product_desc", nullable = false, length = 700)
    private String productDesc;

    @NotNull
    @Column(name = "status_sell", nullable = false)
    private int statusSell; // 0: hết hàng, 1: đang bán, ...

    // Một sản phẩm thuộc một Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Quan hệ với CartItem
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // Quan hệ với OrderDetail
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
