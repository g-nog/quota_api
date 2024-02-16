package com.nogueira.quota.aspect;

import com.nogueira.quota.exceptions.QuotaExceededException;
import com.nogueira.quota.repositories.QuotaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Aspect
@Component
public class UserIdQuotaAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserIdQuotaAspect.class);
    private final QuotaRepository quotaTokenBucket;

    public UserIdQuotaAspect(QuotaRepository quotaTokenBucket) {
        this.quotaTokenBucket = quotaTokenBucket;
    }

    @Around("@annotation(com.nogueira.quota.annotations.UserIdQuota)")
    public Object checkQuota(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Could not get request attributes");
        }

        HttpServletRequest request = attributes.getRequest();
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userId = pathVariables.get("userId");

        if (userId == null) {
            LOGGER.debug("Invalid userId: {} - Not checking quota.", userId);
        } else {
            try {
                var userIdLong = Long.parseLong(userId);
                if (!quotaTokenBucket.checkQuota(userIdLong)) {
                    throw new QuotaExceededException(userIdLong);
                }
            } catch (NumberFormatException e) {
                LOGGER.debug("Invalid userId: {} - Not checking quota.", userId);
            }
        }

        return joinPoint.proceed();
    }
}
