package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.entity.User;

import java.util.Optional;

public interface JwtService {
    String generateAccessToken(User user);
    Optional<String> getSubjectFromToken(String token);
}
