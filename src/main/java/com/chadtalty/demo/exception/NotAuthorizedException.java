package com.chadtalty.demo.exception;

public abstract class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
