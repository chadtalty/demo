package com.chadtalty.demo.exception;

public class WebClientException extends RuntimeException {

    public WebClientException(String message) {
        super(message);
    }

    public WebClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
