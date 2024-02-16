package com.nogueira.quota.controllers;

import com.nogueira.quota.models.User;
import com.nogueira.quota.models.UsersQuota;
import com.nogueira.quota.repositories.QuotaRepository;
import com.nogueira.quota.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class QuotaControllerSpringTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private QuotaRepository quotaRepository;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void consumeQuota_whenQuotaLeft_returnsOk() throws Exception {
        Long userId = 1L;
        User user = new User();
        UsersQuota usersQuota = new UsersQuota(10); // assuming 10 is the quota left

        when(userService.getUserById(userId)).thenReturn(user);
        when(quotaRepository.checkQuota(userId)).thenReturn(true);
        when(quotaRepository.getUsersQuotas()).thenReturn(usersQuota);

        mockMvc.perform(post("/quota/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void consumeQuota_whenNoQuotaLeft_returnsBadRequest() throws Exception {
        Long userId = 2L;
        User user = new User();

        when(userService.getUserById(userId)).thenReturn(user);
        when(quotaRepository.checkQuota(userId)).thenReturn(false);

        mockMvc.perform(post("/quota/users/" + userId))
                .andExpect(status().isTooManyRequests());
    }
}