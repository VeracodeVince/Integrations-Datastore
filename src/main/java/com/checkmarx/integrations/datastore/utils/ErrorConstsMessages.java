package com.checkmarx.integrations.datastore.utils;

public class ErrorConstsMessages {

    private ErrorConstsMessages() {
    }

    public static final String ACCESS_TOKEN_NOT_FOUND = "Access token cannot be found with the combination of SCM url '%s' and org name '%s'";
    public static final String REPO_NOT_FOUND = "SCM repo '%s' was not found";
    public static final String BASE_URL_WITH_ORG_NOT_FOUND = "SCM base URL '%s' with org name '%s' cannot be found";
    public static final String SCM_NOT_FOUND = "SCM base URL '%s' cannot be found";
}