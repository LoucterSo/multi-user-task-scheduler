package io.github.LoucterSo.task_tracker_backend.service;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;

import java.util.Optional;

public interface AuthorityService {
    Optional<Authority> findByRole(Authority.Roles role);
    void save(Authority authority);
}
