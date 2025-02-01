package io.github.LoucterSo.task_tracker_backend.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter @Setter
public class AuthResponseData {
    private String message;
}
