package io.github.LoucterSo.task_tracker_backend.security.filter;

import static io.github.LoucterSo.task_tracker_backend.Util.getTokenFromAuthHeader;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        LOGGER.info("Processing request with path {}.", path);
        if (path.startsWith("/api/v1/auth")) {
            LOGGER.info("Skipping request with path {}.", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
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
                                                        SecurityContextHolder.getContext().setAuthentication(authorization);
                                                        LOGGER.info("User {} authenticated successfully", user.getEmail() + user.getAuthorities());
                                                    });
                                }
                            });

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException ex) {
            LOGGER.error("Exception {} occurred. Message: {}", ex.getClass().getName(), ex.getMessage());
            handleJwtException(response, ex);
        }
    }

    private void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        String jsonResponse =  "{\"message\": \"" + e.getMessage() + "\", \"timeStamp\": \"" + System.currentTimeMillis() + "\"}";
        writer.write(jsonResponse);
        writer.flush();
        LOGGER.error("Exception {} handled. Response {} with status {} send.", e.getClass().getName(), jsonResponse, HttpStatus.UNAUTHORIZED.value());
    }
}
