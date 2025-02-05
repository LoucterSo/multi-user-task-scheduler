package io.github.LoucterSo.task_tracker_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class LoggingRequestCache extends HttpSessionRequestCache {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestCache.class);

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        super.saveRequest(request, response);
        logger.info("Request saved to cache: {}", request.getRequestURI());
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = super.getRequest(request, response);
        if (savedRequest != null) {
            logger.info("Request retrieved from cache: {}", savedRequest.getRedirectUrl());
        } else {
            logger.info("No request found in cache.");
        }
        return savedRequest;
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        super.removeRequest(request, response);
        logger.info("Request removed from cache.");
    }
}