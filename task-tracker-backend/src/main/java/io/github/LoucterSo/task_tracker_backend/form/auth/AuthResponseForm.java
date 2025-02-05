package io.github.LoucterSo.task_tracker_backend.form.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class AuthResponseForm {

    @JsonProperty("access_token")
    private String accessToken;
}
