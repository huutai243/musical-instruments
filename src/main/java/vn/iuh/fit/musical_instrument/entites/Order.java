package vn.iuh.fit.musical_instrument.entites;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @NotBlank
    @Column(name = "ship_address", nullable = false)
    private String shipAddress;

    @Column(name = "note")
    private String note;

    @NotNull
    @Column(name = "order_date", nullable = false)
    private Date orderDate;

    // Mỗi đơn hàng thuộc về một User
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // Ví dụ: 0 - chưa giao, 1 - đã giao
    @Column(name = "is_shipped")
    private int isShipped;

    // Trạng thái đơn hàng (có thể mở rộng thành enum nếu cần)
    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}

