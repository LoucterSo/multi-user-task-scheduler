package io.github.LoucterSo.task_tracker_backend.form.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ValidationErrorResponse {

    private final String message = "Wrong values for fields";
    private long timeStamp;
    private List<String> errorFields;
}
