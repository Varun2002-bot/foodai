package com.foodai.user.exception;

import com.foodai.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String email) {
        super("Email is already registered: " + email, HttpStatus.CONFLICT);
    }
}
