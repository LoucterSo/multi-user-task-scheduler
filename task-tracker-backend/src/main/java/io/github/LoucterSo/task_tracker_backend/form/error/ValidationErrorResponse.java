package io.github.LoucterSo.task_tracker_backend.form.error;

import java.util.List;

public record ValidationErrorResponse(List<String> errorFields, long timeStamp) {

}
