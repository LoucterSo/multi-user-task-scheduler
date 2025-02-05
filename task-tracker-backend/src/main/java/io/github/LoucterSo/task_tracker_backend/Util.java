package io.github.LoucterSo.task_tracker_backend;

import java.util.Optional;

public final class Util {
    private Util() {}

    public static Optional<String> getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return Optional.of(authHeader.substring(7));

        else return Optional.empty();
    }
}
