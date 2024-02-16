package com.nogueira.quota.exceptions;

public abstract class QuotaException extends RuntimeException {

    public QuotaException(String message) {
        super(message);
    }
    public abstract int getErrorCode();
}
