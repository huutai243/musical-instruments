package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // Ví dụ: 0 - chưa giao, 1 - đã giao
    @Column(name = "is_shipped")
    private int isShipped;

    // Trạng thái đơn hàng (có thể dùng enum)
    @Column(name = "status")
    private int status;

    // Đơn hàng thuộc về 1 user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Một order có nhiều orderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
