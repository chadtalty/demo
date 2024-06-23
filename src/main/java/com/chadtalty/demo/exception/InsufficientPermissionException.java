package com.chadtalty.demo.exception;

public class InsufficientPermissionException extends NotAuthorizedException {
    public InsufficientPermissionException(String message) {
        super(message);
    }
}
