package io.github.LoucterSo.task_tracker_backend.service.jwt;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;

import java.util.Date;
import java.util.Optional;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Optional<String> getSubjectFromToken(String token);
    Date getExpFromToken(String token);
}
