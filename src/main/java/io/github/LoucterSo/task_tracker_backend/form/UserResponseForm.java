package io.github.LoucterSo.task_tracker_backend.form;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class UserResponseForm {
    private String message;

    private Long id;

    private String email;

}
