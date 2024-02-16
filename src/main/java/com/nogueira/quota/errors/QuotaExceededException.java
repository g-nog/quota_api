package com.nogueira.quota.errors;

import org.springframework.http.HttpStatus;

public class QuotaExceededException extends QuotaError {
    public QuotaExceededException(Long userIdentifier) {
        super(String.format("Quota exceeded for user %s", userIdentifier));
    }

    @Override
    public int getErrorCode() {
        return HttpStatus.TOO_MANY_REQUESTS.value();
    }
}
