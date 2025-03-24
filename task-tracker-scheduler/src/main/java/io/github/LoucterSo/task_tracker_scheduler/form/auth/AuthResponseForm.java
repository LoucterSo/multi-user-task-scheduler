package io.github.LoucterSo.task_tracker_scheduler.form.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseForm(@JsonProperty("access_token") String accessToken) {

}
