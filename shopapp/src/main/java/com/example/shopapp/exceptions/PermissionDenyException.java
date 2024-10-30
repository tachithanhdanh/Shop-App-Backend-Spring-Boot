package com.example.shopapp.exceptions;

public class PermissionDenyException extends Exception {
    public PermissionDenyException(String message) {
        super(message);
    }

    public PermissionDenyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDenyException(Throwable cause) {
        super(cause);
    }
}
