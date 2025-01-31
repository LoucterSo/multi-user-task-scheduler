package io.github.LoucterSo.task_tracker_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    public enum Roles {
        USER, ADMIN
    }
}
