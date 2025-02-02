package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.entity.User;

import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
