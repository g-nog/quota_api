package com.nogueira.quota.controllers;

import com.nogueira.quota.models.User;
import com.nogueira.quota.models.UsersQuota;
import com.nogueira.quota.repositories.QuotaRepository;
import com.nogueira.quota.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class QuotaControllerTest {
    private QuotaController quotaController;
    private QuotaRepository quotaRepository;
    private UserService userService;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        quotaRepository = mock(QuotaRepository.class);
        userService = mock(UserService.class);
        quotaController = new QuotaController(quotaRepository, userService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void getQuotas_whenUsersExist_returnsOk() {
        User user = new User();
        user.setId(1L);
        UsersQuota usersQuota = mock(UsersQuota.class);
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(quotaRepository.getUsersQuotas()).thenReturn(usersQuota);
        when(usersQuota.get(user.getId())).thenReturn(5);

        ResponseEntity<Object> result = quotaController.getQuotas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getQuotas_whenNoUsers_returnsOkWithEmptyList() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> result = quotaController.getQuotas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List) result.getBody()).isEmpty());
    }

    @Test
    void getQuotas_whenExceptionOccurs_returnsInternalServerError() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException());

        ResponseEntity<Object> result = quotaController.getQuotas();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void getQuotas_whenOneUser_returnsOkWithOneQuota() {
        User user = new User();
        user.setId(1L);
        UsersQuota usersQuota = mock(UsersQuota.class);
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(quotaRepository.getUsersQuotas()).thenReturn(usersQuota);
        when(usersQuota.get(user.getId())).thenReturn(5);

        ResponseEntity<Object> result = quotaController.getQuotas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, ((List) result.getBody()).size());
        assertEquals(5, ((List<QuotaController.UserQuotaResponse>) result.getBody()).get(0).getQuota());
    }

    @Test
    void getQuotas_whenMultipleUsers_returnsOkWithMultipleQuotas() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        UsersQuota usersQuota = mock(UsersQuota.class);
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
        when(quotaRepository.getUsersQuotas()).thenReturn(usersQuota);
        when(usersQuota.get(user1.getId())).thenReturn(5);
        when(usersQuota.get(user2.getId())).thenReturn(3);

        ResponseEntity<Object> result = quotaController.getQuotas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, ((List) result.getBody()).size());
        assertEquals(5, ((List<QuotaController.UserQuotaResponse>) result.getBody()).get(0).getQuota());
        assertEquals(3, ((List<QuotaController.UserQuotaResponse>) result.getBody()).get(1).getQuota());
    }
}