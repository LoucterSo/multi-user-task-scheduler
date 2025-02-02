package io.github.LoucterSo.task_tracker_backend.form;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class LoginForm {

    private final String email;
    private final String password;
}
