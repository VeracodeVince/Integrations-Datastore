package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class RepoDto {

    @JsonProperty("repo_identity")
    private String repoIdentity;
    @JsonProperty("webhook_id")
    private String webhookId;
    @JsonProperty("is_webhook_configured")
    private boolean isWebhookConfigured;
}