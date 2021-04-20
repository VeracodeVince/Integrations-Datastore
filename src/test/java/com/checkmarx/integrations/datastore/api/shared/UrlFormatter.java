package com.checkmarx.integrations.datastore.api.shared;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

@Service
public class UrlFormatter {
    public URI format(String template, int port, Object... urlVariableValues) {
        String fullTemplate = String.format("http://localhost:%d/%s", port, template);

        return new UriTemplate(fullTemplate)
                .expand(urlVariableValues);
    }
}
