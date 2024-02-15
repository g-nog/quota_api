package com.nogueira.quota;

import com.nogueira.quota.Quota;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class QuotaAspect {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Around("@annotation(Quota)")
    public Object checkQuota(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Failed to obtain request attributes");
        }

        HttpServletRequest request = attributes.getRequest();
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        String clientIp = xForwardedForHeader != null ? xForwardedForHeader : request.getRemoteAddr();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Quota quota = signature.getMethod().getAnnotation(Quota.class);
//        if (quota == null) {
//            throw new IllegalStateException("Quota annotation not found");
//        }

        int quotaValue = quota.value();

        TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(quotaValue, 1, TimeUnit.DAYS));

        if (!bucket.tryConsume()) {
//            httpResponse.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);

        }

        // Your logic here

        return joinPoint.proceed();
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
