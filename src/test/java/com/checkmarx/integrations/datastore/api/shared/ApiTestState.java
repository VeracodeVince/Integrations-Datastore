package com.checkmarx.integrations.datastore.api.shared;

import com.checkmarx.integrations.datastore.controllers.exceptions.DataStoreException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Getter
@Setter
@Slf4j
@Component
public class ApiTestState {
    private ResponseEntity<JsonNode> lastResponse;

    public void clear() {
        log.info("Clearing test state.");
        lastResponse = null;
    }

    public JsonNode getResponseBodyOrThrow() {
        return Optional.ofNullable(lastResponse)
                .map(ResponseEntity::getBody)
                .orElseThrow(() -> new DataStoreException("Response body is missing!"));
    }
}
