package com.nogueira.quota.models;

import java.util.HashMap;

public class UsersQuota extends HashMap<Long, Integer> {
    private final int maxQuota;

    public UsersQuota(int maxQuota) {
        super();
        this.maxQuota = maxQuota;
    }

    public int getMaxQuota() {
        return maxQuota;
    }

    @Override
    public Integer get(Object key) {
        return super.getOrDefault(key, maxQuota);
    }
}