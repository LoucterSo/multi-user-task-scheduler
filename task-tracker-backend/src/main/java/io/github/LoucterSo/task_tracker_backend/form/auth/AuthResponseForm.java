package io.github.LoucterSo.task_tracker_backend.form.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public record AuthResponseForm(@JsonProperty("access_token") String accessToken) {

}
