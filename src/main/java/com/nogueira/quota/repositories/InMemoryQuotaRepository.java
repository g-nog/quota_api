package com.nogueira.quota.repositories;

import com.nogueira.quota.models.UsersQuota;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class InMemoryQuotaRepository extends ConcurrentHashMap<Long, InMemoryQuotaRepository.TokenBucket> implements QuotaRepository {

    private final int defaultQuotaValue = 5;

    public InMemoryQuotaRepository() {
        super();
    }


    @Override
    public boolean checkQuota(Long userId) {
        TokenBucket bucket = this.computeIfAbsent(userId, k -> new TokenBucket(defaultQuotaValue, 1, TimeUnit.DAYS));
        return bucket.tryConsume();
    }

    @Override
    public UsersQuota getUsersQuotas() {
        UsersQuota usersQuota = new UsersQuota(defaultQuotaValue);
        this.forEach((k, v) -> usersQuota.put(k, v.getQuotaValue()));
        return usersQuota;
    }

    //https://en.wikipedia.org/wiki/Token_bucket
    public static class TokenBucket {
        private final int capacity;
        private final long refillIntervalInMilliseconds;
        private int tokens;
        private long lastRefillTimestamp;

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

        public int getQuotaValue() {
            return tokens;
        }
    }
}
