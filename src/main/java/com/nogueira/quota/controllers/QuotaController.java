package com.nogueira.quota.controllers;

import com.nogueira.quota.annotations.UserIdQuota;
import com.nogueira.quota.models.User;
import com.nogueira.quota.repositories.QuotaRepository;
import com.nogueira.quota.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quota")
public class QuotaController {
    private final QuotaRepository quotaTokenBucket;
    private final UserService userService;

    public QuotaController(QuotaRepository quotaTokenBucket, UserService userService) {
        this.quotaTokenBucket = quotaTokenBucket;
        this.userService = userService;
    }

    @PostMapping("/users/{userId}")
    @UserIdQuota
    public ResponseEntity<Object> consumeQuota(@PathVariable String userId) {
        return new ResponseEntity<>("quota_set", HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getQuotas() {
        var quotas = this.quotaTokenBucket.getUsersQuotas();
        var users = this.userService.getAllUsers();

        List<UserQuotaResponse> resp = users.stream()
                .map(user -> new UserQuotaResponse(user, quotas.get(user.getId())))
                .toList();

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public static class UserQuotaResponse {
        private User user;
        private int quota;

        public UserQuotaResponse(User user, int quota) {
            this.user = user;
            this.quota = quota;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public int getQuota() {
            return quota;
        }

        public void setQuota(int quota) {
            this.quota = quota;
        }
    }

}
