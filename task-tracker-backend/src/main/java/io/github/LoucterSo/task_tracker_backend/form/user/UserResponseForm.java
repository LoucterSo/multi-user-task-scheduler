package io.github.LoucterSo.task_tracker_backend.form.user;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class UserResponseForm {

    private Long id;

    private String email;

}
