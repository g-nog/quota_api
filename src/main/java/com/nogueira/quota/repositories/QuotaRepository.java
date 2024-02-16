package com.nogueira.quota.repositories;

import com.nogueira.quota.models.UsersQuota;

public interface QuotaRepository {
    boolean checkQuota(Long userId);

    UsersQuota getUsersQuotas();
}
