package io.github.LoucterSo.task_tracker_backend.entity.task;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tasks")
@AllArgsConstructor @NoArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "done", nullable = false)
    private boolean isDone = false;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false)
    private Timestamp created;

    @UpdateTimestamp
    @Column(name = "updated_time", nullable = false)
    private Timestamp updated;

    @Column(name = "completion_time")
    private Timestamp completionTime;

    public void setDone(boolean isDone) {

        this.isDone = isDone;
        if (this.isDone) {
            this.completionTime = new Timestamp(System.currentTimeMillis());
        } else {
            this.completionTime = null;
        }
    }
}
