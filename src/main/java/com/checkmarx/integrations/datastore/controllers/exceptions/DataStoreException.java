package com.checkmarx.integrations.datastore.controllers.exceptions;

public class DataStoreException extends RuntimeException {
    public DataStoreException(String message) {
        super(message);
    }
}
