package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
}
