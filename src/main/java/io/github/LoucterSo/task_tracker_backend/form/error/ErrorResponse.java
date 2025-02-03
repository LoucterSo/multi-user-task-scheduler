package io.github.LoucterSo.task_tracker_backend.form.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ErrorResponse {

    private String message;
    private long timeStamp;

}
