package com.checkmarx.integrations.datastore.controllers.exceptions;

public class ScmOrgNotFoundException extends RuntimeException {

    public ScmOrgNotFoundException(String message) {
        super(message);
    }

    public ScmOrgNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}