package vn.iuh.fit.musical_instrument.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

    // Refresh token, có thể tạo bằng secure random, Base64 encoded.
    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    // Thời gian hết hạn của refresh token
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Trạng thái đánh dấu token đã sử dụng (nếu cần lưu lịch sử)
    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Liên kết với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
