package vn.iuh.fit.musical_instrument.entites;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "status", nullable = false)
    private int statusSell;

    // Một sản phẩm có thể xuất hiện trong nhiều giỏ hàng (CartItem)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // Mỗi sản phẩm thuộc một danh mục
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    // Một sản phẩm có thể có nhiều OrderDetail
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}

