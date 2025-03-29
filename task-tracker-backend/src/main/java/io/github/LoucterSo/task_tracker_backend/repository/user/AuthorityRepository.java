package io.github.LoucterSo.task_tracker_backend.repository.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
