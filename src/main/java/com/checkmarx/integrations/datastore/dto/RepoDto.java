package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "webhook_id",
        "is_webhook_configured"
})

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("webhook_id")
    private long webhookId;
    @JsonProperty("is_webhook_configured")
    private boolean isWebhookConfigured;
}