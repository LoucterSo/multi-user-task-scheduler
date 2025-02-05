package io.github.LoucterSo.task_tracker_backend.repository;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
