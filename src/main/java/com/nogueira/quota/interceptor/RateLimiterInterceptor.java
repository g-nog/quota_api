package com.nogueira.quota.interceptor;

import com.nogueira.quota.annotations.RateLimited;
import com.nogueira.quota.repositories.QuotaRepository;
import com.nogueira.quota.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterInterceptor.class);
    private final QuotaRepository quotaTokenBucket;
    private final UserService userService;

    public RateLimiterInterceptor(QuotaRepository quotaTokenBucket, UserService userService) {
        this.quotaTokenBucket = quotaTokenBucket;
        this.userService = userService;
    }

    private static boolean isQuotaAnnotationPresent(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimited rateLimited = handlerMethod.getMethodAnnotation(RateLimited.class);

        return rateLimited == null;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (isQuotaAnnotationPresent(handler)) return true;

        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userId = pathVariables.get("userId");

        if (!StringUtils.hasText(userId)) {
            response.sendError(HttpStatus.BAD_REQUEST.value(),
                    "Missing path variable userId");
            return false;
        }
        try {
            var userIdLong = Long.parseLong(userId);
            LOGGER.info("checking quota for user [ {} ].", userId);

            if (userService.getUserById(userIdLong) == null) {
                LOGGER.error("user [ {} ] not found.", userId);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.sendError(HttpStatus.BAD_REQUEST.value(), "user_not_found");
                return false;
            }

            if (!quotaTokenBucket.checkQuota(userIdLong)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "quota_exceeded");
                return false;
            }

            String leftQuotas = String.valueOf(quotaTokenBucket.getUsersQuotas().get(userIdLong));
            LOGGER.info("left quotas for user [ {} ] -> {}.", userId, leftQuotas);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid userId: {}", userId);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.sendError(HttpStatus.BAD_REQUEST.value(), "invalid_user_id");
            return false;
        }
        return true;
    }
}
