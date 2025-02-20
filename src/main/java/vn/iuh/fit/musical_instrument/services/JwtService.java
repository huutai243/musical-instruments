package vn.iuh.fit.musical_instrument.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

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

    public String generateToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpiration);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}
