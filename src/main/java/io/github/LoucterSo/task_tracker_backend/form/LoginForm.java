package io.github.LoucterSo.task_tracker_backend.form;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class LoginForm {
    @NotBlank(message = "Email cannot be empty")
    private final String email;
    @NotBlank(message = "Password cannot be empty")
    private final String password;
}
