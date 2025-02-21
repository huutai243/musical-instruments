package vn.iuh.fit.musical_instrument.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.iuh.fit.musical_instrument.entities.User;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.accessToken.expiration}") long accessTokenExpiration,
                      @Value("${jwt.refreshToken.expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Tạo JWT cho user thông thường (dùng username và role)
     */
    public String generateToken(User user, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUserName());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()));

        return createToken(claims, user.getUserName(), expirationTime);
    }

    /**
     *  Tạo JWT cho user đăng nhập qua Google/Facebook
     */
    public String generateOAuth2Token(String email, String name, String provider, String pictureUrl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("fullName", name);
        claims.put("provider", provider);
        claims.put("picture", pictureUrl);

        return createToken(claims, email, accessTokenExpiration);
    }

    /**
     * Tạo Access Token từ User
     */
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }

    /**
     * Tạo Refresh Token từ User
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration);
    }

    /**
     * Kiểm tra và xác thực JWT
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Lấy Username từ Token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất các Claims cụ thể từ Token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     *  Tạo Token với các claims
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     *  Trích xuất toàn bộ Claims từ Token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
