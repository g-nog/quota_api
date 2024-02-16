package com.nogueira.quota.interceptor;

import com.nogueira.quota.annotations.RateLimited;
import com.nogueira.quota.models.User;
import com.nogueira.quota.models.UsersQuota;
import com.nogueira.quota.repositories.QuotaRepository;
import com.nogueira.quota.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimiterInterceptorTest {

    @Mock
    private QuotaRepository quotaRepository;

    @Mock
    private UserService userService;

    private RateLimiterInterceptor rateLimiterInterceptor;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimiterInterceptor = new RateLimiterInterceptor(quotaRepository, userService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of());
    }

    @Test
    void preHandle_withQuotaAnnotationPresent_returnsTrue() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        RateLimited rateLimited = mock(RateLimited.class);
        UsersQuota usersQuota = mock(UsersQuota.class);

        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(rateLimited);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("userId", "123"));

        when(userService.getUserById(123L)).thenReturn(new User());
        when(quotaRepository.checkQuota(123L)).thenReturn(true);
        when(quotaRepository.getUsersQuotas()).thenReturn(usersQuota);

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertTrue(result);
    }

    @Test
    void preHandle_withoutQuotaAnnotationPresent_returnsTrue() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(null);

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertTrue(result);
    }

    @Test
    void preHandle_withMissingUserId_returnsFalse() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        RateLimited rateLimited = mock(RateLimited.class);
        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(rateLimited);

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertFalse(result);
    }

    @Test
    void preHandle_withInvalidUserId_returnsFalse() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        RateLimited rateLimited = mock(RateLimited.class);
        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(rateLimited);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("userId", "invalid"));

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertFalse(result);
    }

    @Test
    void preHandle_withNonExistentUser_returnsFalse() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        RateLimited rateLimited = mock(RateLimited.class);
        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(rateLimited);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("userId", "123"));
        when(userService.getUserById(123L)).thenReturn(null);

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertFalse(result);
    }

    @Test
    void preHandle_withExceededQuota_returnsFalse() throws Exception {

        HandlerMethod handler = mock(HandlerMethod.class);
        RateLimited rateLimited = mock(RateLimited.class);
        when(handler.getMethodAnnotation(RateLimited.class)).thenReturn(rateLimited);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("userId", "123"));
        when(userService.getUserById(123L)).thenReturn(new User());
        when(quotaRepository.checkQuota(123L)).thenReturn(false);

        boolean result = rateLimiterInterceptor.preHandle(request, response, handler);

        assertFalse(result);
    }
}