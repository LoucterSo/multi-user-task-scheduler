package io.github.LoucterSo.task_tracker_backend.api.controller;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/auth/login")
    public String loginPage() {

        return "login-page";
    }
}
