package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserResponseForm> getAllUsers();
    Optional<User> findById(Long userId);
    UserResponseForm createUserResponseForm(User user);
}
