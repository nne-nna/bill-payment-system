package com.billpayments.billpaymentsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private final Map<String, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return !(
                "/api/v1/auth/login".equals(path) ||
                "/api/v1/auth/register".equals(path) ||
                "/api/v1/auth/forgot-password".equals(path)
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String key = path + ":" + resolveClientIdentifier(request);
        int maxRequests = "/api/v1/auth/register".equals(path) ? 3 : 5;

        if (!allowRequest(key, maxRequests)) {
            log.warn("Rate limit exceeded for path {} and client {}", path, key);
            writeRateLimitResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean allowRequest(String key, int maxRequests) {
        long now = System.currentTimeMillis();
        RequestWindow window = requestWindows.computeIfAbsent(key, ignored -> new RequestWindow(now, 0));

        synchronized (window) {
            if (now - window.windowStartMillis >= WINDOW_MILLIS) {
                window.windowStartMillis = now;
                window.requestCount = 0;
            }

            if (window.requestCount >= maxRequests) {
                return false;
            }

            window.requestCount++;
            return true;
        }
    }

    private String resolveClientIdentifier(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {
                  "timestamp":"%s",
                  "status":429,
                  "error":"Too Many Requests",
                  "message":"Too many requests. Please try again in a minute.",
                  "path":"%s",
                  "errors":null
                }
                """.formatted(java.time.LocalDateTime.now(), request.getRequestURI()));
    }

    private static final class RequestWindow {
        private long windowStartMillis;
        private int requestCount;

        private RequestWindow(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
