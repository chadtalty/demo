package com.chadtalty.demo.exception;

public class MissingJwtTokenException extends NotAuthenticatedException {

    public MissingJwtTokenException(String message) {
        super(message);
    }
}
