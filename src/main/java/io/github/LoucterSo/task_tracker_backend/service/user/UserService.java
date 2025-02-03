package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    Optional<User> findById(Long userId);
}
