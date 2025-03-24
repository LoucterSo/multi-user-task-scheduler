package io.github.LoucterSo.task_tracker_backend.form.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupForm(
        @JsonProperty("first_name")
        @NotBlank(message = "First name cannot be empty")
        @Size(min = 3, max = 64, message = "First name must be between 3 and 64 characters")
        @Pattern(regexp = "^[A-Za-z]+$",
                message = "First name must include only letters")
        String firstName,

        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be empty")
        @Size(min = 3, max = 64, message = "Last name must be between 3 and 64 characters")
        @Pattern(regexp = "^[A-Za-z]+$",
                message = "Last name must include only letters")
        String lastName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be 8 characters or more")
        @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "Password must contain at least one uppercase letter and one lowercase letter")
        String password
) {

}
