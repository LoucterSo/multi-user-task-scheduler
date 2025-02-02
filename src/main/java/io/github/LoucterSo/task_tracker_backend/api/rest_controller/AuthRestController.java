package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthorityService authorityService;

    @PostMapping(value = "/signup")
    public ResponseEntity<AuthResponseForm> signup(@RequestBody SignupForm signupForm) {
        final String email = signupForm.getEmail();

        if (userServiceImpl.existsByEmail(email))
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(AuthResponseForm.builder()
                            .message("This email is already taken").build());

        final String password = passwordEncoder.encode(signupForm.getPassword());

        User user = User.builder()
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();

        Authority authority = authorityService.findByRole(Authority.Roles.USER).orElseThrow();

        user.addRole(authority);

        userServiceImpl.saveUser(user);

        String jwtAccess = jwtService.generateAccessToken(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthResponseForm.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponseForm> login(@RequestBody SignupForm signupForm) {
        final String email = signupForm.getEmail();
        final String password = signupForm.getPassword();

        Optional<User> user = userServiceImpl.findByEmail(email);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.orElseThrow().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseForm.builder()
                            .message("User with wrong email or password")
                            .build()); //ERROR
        }

        String jwtAccess = jwtService.generateAccessToken(user.orElseThrow());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseForm.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@Valid @RequestBody LoginForm loginForm) {
//
//        return null;
//    }
//
//    @PostMapping("/refresh-token")
//    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
//        final String header = request.getHeader("Authorization");
//        final String email;
//        String jwtRefresh = null;
//        String jwtAccess = null;
//
//        if (header != null && header.startsWith("Bearer ")) {
//            jwtRefresh = header.substring(7);
//
//            email = jwtService.getNameFromJwtRefresh(jwtRefresh);
//
//            if (email != null) {
//                User user = userRepository.findByEmail(email).get();
//
//                UserDetails userDetails = new UserDetailsImpl(user);
//
//                if (jwtService.isRefreshTokenValid(jwtRefresh, userDetails))
//                    jwtAccess = jwtService.generateAccessToken(userDetails);
//
//            } else {
//                throw new UsernameNotFoundException(String.format("User with email %s not found", email));
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(AuthResponseData.builder()
//                .accessToken(jwtAccess)
//                .refreshToken(jwtRefresh)
//                .message("Success").build());
//    }
}
