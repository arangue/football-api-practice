package com.football.api.exception;

public class NetworkException extends Exception {
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
