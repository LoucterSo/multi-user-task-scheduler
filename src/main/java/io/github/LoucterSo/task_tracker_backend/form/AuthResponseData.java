package io.github.LoucterSo.task_tracker_backend.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter @Setter
public class AuthResponseData {
    private String message;

    @JsonProperty("access_token")
    private String accessToken;
}
