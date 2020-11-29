package com.checkmarx.integrations.datastore.controllers.exceptions;

public class ScmNotFoundException extends RuntimeException {

    public ScmNotFoundException(String message) {
        super(message);
    }

    public ScmNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}