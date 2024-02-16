package com.nogueira.quota.errors;

public abstract class QuotaError extends RuntimeException {

    public QuotaError(String message) {
        super(message);
    }
    public abstract int getErrorCode();
}
