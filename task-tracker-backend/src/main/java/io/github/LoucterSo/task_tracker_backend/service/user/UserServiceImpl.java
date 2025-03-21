package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;
import io.github.LoucterSo.task_tracker_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
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
