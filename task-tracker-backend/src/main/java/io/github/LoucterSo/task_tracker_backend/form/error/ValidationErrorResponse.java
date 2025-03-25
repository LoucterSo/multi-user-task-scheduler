package io.github.LoucterSo.task_tracker_backend.form.error;

import java.util.Map;

public record ValidationErrorResponse(Map<String, String> errorFields, long timeStamp) {

}
