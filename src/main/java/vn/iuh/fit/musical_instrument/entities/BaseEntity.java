package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lớp cơ bản chứa khóa chính (id).
 * Các entity khác kế thừa từ đây để có sẵn trường id.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Bạn có thể thêm các trường chung khác (nếu cần),
    // nhưng ở đây chúng ta chỉ cần duy nhất id.
}
