package com.nogueira.quota.exceptions;

import org.springframework.http.HttpStatus;

public class QuotaExceededException extends QuotaException {
    public QuotaExceededException(Long userIdentifier) {
        super(String.format("Quota exceeded for user %s", userIdentifier));
    }

    @Override
    public int getErrorCode() {
        return HttpStatus.TOO_MANY_REQUESTS.value();
    }
}
