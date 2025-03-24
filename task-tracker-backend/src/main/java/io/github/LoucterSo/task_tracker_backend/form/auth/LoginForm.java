package io.github.LoucterSo.task_tracker_backend.form.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public record LoginForm(
        @NotBlank(message = "Email cannot be empty") String email,

        @NotBlank(message = "Password cannot be empty") String password
) {

}
