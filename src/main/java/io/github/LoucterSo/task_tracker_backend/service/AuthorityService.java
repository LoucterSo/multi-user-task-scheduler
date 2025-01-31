package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.repository.AuthorityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorityService {

    private AuthorityRepository authorityRepository;
}
