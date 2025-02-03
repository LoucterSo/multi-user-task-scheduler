package io.github.LoucterSo.task_tracker_backend.service.jwt;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final int expireTimeForAccess;
    private final long expireTimeForRefresh;
    private final SecretKey signingKey;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.lifetime}") int expireTimeForAccess,
            @Value("${jwt.refresh.lifetime}") long expireTimeForRefresh) {
        this.expireTimeForRefresh = expireTimeForRefresh;
        this.expireTimeForAccess = expireTimeForAccess;
        this.signingKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(createExpireTimeForAccess())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(createExpireTimeForRefresh())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<String> getSubjectFromToken(String token) {
        return Optional.ofNullable(getClaim(token, Claims::getSubject));
    }

    @Override
    public Date getExpFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
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

    private Date createExpireTimeForRefresh() {
        return new Date(System.currentTimeMillis() + expireTimeForRefresh);
    }
}
