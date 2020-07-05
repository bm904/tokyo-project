package com.asia.tokyo.exception;

public class CustomerException extends RuntimeException {
    public CustomerException(String errorMessage) {
        super(errorMessage);
    }
}
