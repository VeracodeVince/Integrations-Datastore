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
        "scm_url",
        "org_name",
        "accessToken",
        "type"
})

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCMAccessTokenDto {

    @JsonProperty("scm_url")
    private String scmUrl;
    @JsonProperty("org_name")
    private String orgName;
    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("tokenType")
    private String tokenType;
}