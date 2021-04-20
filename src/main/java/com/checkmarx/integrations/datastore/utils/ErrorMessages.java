package com.checkmarx.integrations.datastore.utils;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String ACCESS_TOKEN_NOT_FOUND = "Access token cannot be found with the combination of SCM ID: %d and org name: '%s'";
    public static final String ACCESS_TOKEN_NOT_FOUND_BY_ID = "Access token with the %s ID was not found.";
    public static final String REPO_NOT_FOUND = "SCM repo '%s' was not found";
    public static final String ORG_NOT_FOUND_BY_IDENTITY = "The '%s' org for the SCM ID %d cannot be found.";
    public static final String ORG_NOT_FOUND_BY_REPO = "The '%s' org with the '%s' repo base URL cannot be found.";
    public static final String SCM_NOT_FOUND = "SCM with ID %d cannot be found";
    public static final String INVALID_SCM_ID = "Invalid SCM ID.";
    public static final String INVALID_TOKEN_ID = "Invalid access token ID.";
    public static final String SCM_NOT_FOUND_BY_REPO_BASE_URL = "SCM with cannot be found for repo base URL: '%s'";
    public static final String INVALID_SCM_TYPE = "Invalid SCM type: '%s'.";
}