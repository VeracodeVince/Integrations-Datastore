package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RepoUpdateDto {
    @JsonProperty("webhook_id")
    private String webhookId;

    @JsonProperty("is_webhook_configured")
    private boolean isWebhookConfigured;
}
