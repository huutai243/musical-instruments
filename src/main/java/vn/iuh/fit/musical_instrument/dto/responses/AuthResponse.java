package vn.iuh.fit.musical_instrument.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}
