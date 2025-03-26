package io.github.LoucterSo.task_tracker_backend.service.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithTasksDto;
import io.github.LoucterSo.task_tracker_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {return userRepository.findByEmail(email);}

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<UserWithTasksDto> getAllUsersWithTasks() {return userRepository.getAllUsersWithTasks().stream().map(UserWithTasksDto::fromEntity).toList();}

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

}
