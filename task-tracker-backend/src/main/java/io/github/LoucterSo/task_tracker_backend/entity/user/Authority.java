package io.github.LoucterSo.task_tracker_backend.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"id", "role"})
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @JsonIgnore
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles role;

    @Override
    public String getAuthority() {
        return role.name();
    }

    public Authority(Roles role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "role='" + role + "'";
    }

    public enum Roles {
        USER,
        ADMIN
    }
}
