package io.github.LoucterSo.task_tracker_scheduler.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponseForm {

    @JsonProperty("access_token")
    private String accessToken;
}
