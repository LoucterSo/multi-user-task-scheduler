package io.github.LoucterSo.task_tracker_backend.repository;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByRole(Authority.Roles role);
}
