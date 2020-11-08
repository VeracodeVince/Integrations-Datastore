package com.checkmarx.integrations.datastore.controllers.exceptions;

public class RepoNotFoundException extends RuntimeException {

    public RepoNotFoundException(String message) {
        super(message);
    }

    public RepoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}