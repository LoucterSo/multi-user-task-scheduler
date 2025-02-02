package io.github.LoucterSo.task_tracker_backend.form;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class SignupForm {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
}
