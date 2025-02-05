package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;
import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<UserResponseForm> getAllUsers() {

        return userRepository.findAll().stream()
                .map(user -> UserResponseForm.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .build()).toList();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserResponseForm createUserResponseForm(User user) {
        return UserResponseForm.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

}
