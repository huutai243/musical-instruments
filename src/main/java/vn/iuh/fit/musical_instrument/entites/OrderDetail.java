package vn.iuh.fit.musical_instrument.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetail extends BaseEntity {

    @Min(1)
    @Column(name = "detail_quantity", nullable = false)
    private int detailQuantity;

    // Mỗi OrderDetail thuộc về 1 Order
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // Mỗi OrderDetail liên kết với 1 sản phẩm
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    // Không đặt logic nghiệp vụ trong setter; kiểm tra số lượng tồn kho nên được xử lý ở tầng Service
}

