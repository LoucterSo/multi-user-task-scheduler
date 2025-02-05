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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, unique = true)
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
