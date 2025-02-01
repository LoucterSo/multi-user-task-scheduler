package io.github.LoucterSo.task_tracker_backend.security.filter;

import static io.github.LoucterSo.task_tracker_backend.Util.getTokenFromAuthHeader;
import io.github.LoucterSo.task_tracker_backend.service.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        getTokenFromAuthHeader(request.getHeader("Authorization"))
                .flatMap(jwtService::getSubjectFromToken)
                .ifPresent(
                        email -> {
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                userService
                                        .findByEmail(email)
                                        .ifPresent(
                                                user -> {
                                                    UsernamePasswordAuthenticationToken authorization =
                                                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                                                    authorization.setDetails(
                                                            new WebAuthenticationDetailsSource().buildDetails(request));
                                                    System.out.println("Success with token");
                                                    SecurityContextHolder.getContext().setAuthentication(authorization);
                                                });
                            }
                        });

        filterChain.doFilter(request, response);
    }
}
