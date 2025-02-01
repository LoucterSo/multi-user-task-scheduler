package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final int expireTimeForAccess;
    private final SecretKey signingKey;

    public JwtServiceImpl(
            @Value("${jwt.access.secret}") SecretKey signingKey,
            @Value("${jwt.access.lifetime}") int expireTimeForAccess) {
        this.expireTimeForAccess = expireTimeForAccess;
        this.signingKey = signingKey;
    }

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(createExpireTimeForAccess())
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Optional<String> getSubjectFromToken(String token) {
        return Optional.ofNullable(getClaim(token, Claims::getSubject));
    }

    private Claims getAllClaims(String jwt) {
        return Jwts
                .parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsFunction) {
        final Claims claims = getAllClaims(token);
        return claimsFunction.apply(claims);
    }


    private Date createExpireTimeForAccess() {
        return new Date(System.currentTimeMillis() + expireTimeForAccess);
    }
}
