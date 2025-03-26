package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithTasksDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findByEmail(String email);
    List<UserWithTasksDto> getAllUsersWithTasks();
    Optional<User> findById(Long userId);
}
