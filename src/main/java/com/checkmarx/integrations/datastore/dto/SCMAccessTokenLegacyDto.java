package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: remove this class when the tests are migrated to the new entity structure.

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "scm_url",
        "org_identity",
        "accessToken",
        "tokenType"
})

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCMAccessTokenLegacyDto {

    @JsonProperty("scm_url")
    private String scmUrl;
    @JsonProperty("org_identity")
    private String orgIdentity;
    private String accessToken;
    private String tokenType;
}