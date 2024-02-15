package com.nogueira.quota;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@WebFilter("/*")
public class RateLimitingFilter implements Filter {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(5, 1, TimeUnit.MINUTES));

        if (!bucket.tryConsume()) {
//            httpResponse.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private static class TokenBucket {
        private int tokens;
        private final int capacity;
        private long lastRefillTimestamp;
        private final long refillIntervalInMilliseconds;

        public TokenBucket(int capacity, long refillInterval, TimeUnit timeUnit) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.currentTimeMillis();
            this.refillIntervalInMilliseconds = timeUnit.toMillis(refillInterval);
        }

        public synchronized boolean tryConsume() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }

            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsedTime = now - lastRefillTimestamp;

            if (elapsedTime > refillIntervalInMilliseconds) {
                tokens = capacity;
                lastRefillTimestamp = now;
            }
        }
    }
}

