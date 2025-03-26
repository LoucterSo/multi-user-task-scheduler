package io.github.LoucterSo.task_tracker_backend.form.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


interface NotBlankGroup {}
interface SizeGroup {}
interface PatternGroup {}

@GroupSequence({NotBlankGroup.class, SizeGroup.class, PatternGroup.class, SignupForm.class})
public record SignupForm(
        @JsonProperty("first_name")
        @NotBlank(message = "First name cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 3, max = 64, message = "First name must be between 3 and 64 characters", groups = SizeGroup.class)
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must include only letters", groups = PatternGroup.class)
        String firstName,

        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 3, max = 64, message = "Last name must be between 3 and 64 characters", groups = SizeGroup.class)
        @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must include only letters", groups = PatternGroup.class)
        String lastName,

        @NotBlank(message = "Email cannot be empty", groups = NotBlankGroup.class)
        @Email(message = "Invalid email", groups = PatternGroup.class)
        String email,

        @NotBlank(message = "Password cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 8, message = "Password must be 8 characters or more", groups = SizeGroup.class)
        @Pattern(
                regexp = "(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "Password must contain at least one uppercase and one lowercase letter",
                groups = PatternGroup.class
        )
        String password
) {}
