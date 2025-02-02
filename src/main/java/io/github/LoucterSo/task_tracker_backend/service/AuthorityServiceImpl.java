package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public Optional<Authority> findByRole(Authority.Roles role) {
        return authorityRepository.findByRole(role);
    }

    @Override
    public void save(Authority authority) {
        authorityRepository.save(authority);
    }
}
