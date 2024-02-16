package com.nogueira.quota.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryQuotaRepositoryTest {
    private InMemoryQuotaRepository quotaRepository;

    @BeforeEach
    void setUp() {
        quotaRepository = new InMemoryQuotaRepository();
    }

    @Test
    void checkQuota_whenQuotaLeft_returnsTrue() {
        Long userId = 1L;
        assertTrue(quotaRepository.checkQuota(userId));
    }

    @Test
    void checkQuota_whenNoQuotaLeft_returnsFalse() {
        Long userId = 2L;
        for (int i = 0; i < 6; i++) {
            quotaRepository.checkQuota(userId);
        }
        assertFalse(quotaRepository.checkQuota(userId));
    }

    @Test
    void getUsersQuotas_returnsCorrectQuotas() {
        Long userId1 = 3L;
        Long userId2 = 4L;
        quotaRepository.checkQuota(userId1);
        quotaRepository.checkQuota(userId2);
        quotaRepository.checkQuota(userId2);

        var quotas = quotaRepository.getUsersQuotas();

        assertEquals(4, quotas.get(userId1));
        assertEquals(3, quotas.get(userId2));
    }

    @Test
    void tryConsume_whenTokensLeft_returnsTrue() {
        var bucket = new InMemoryQuotaRepository.TokenBucket(5, 1, java.util.concurrent.TimeUnit.DAYS);
        assertTrue(bucket.tryConsume());
    }

    @Test
    void tryConsume_whenNoTokensLeft_returnsFalse() {
        var bucket = new InMemoryQuotaRepository.TokenBucket(1, 1, java.util.concurrent.TimeUnit.DAYS);
        bucket.tryConsume();
        assertFalse(bucket.tryConsume());
    }
}