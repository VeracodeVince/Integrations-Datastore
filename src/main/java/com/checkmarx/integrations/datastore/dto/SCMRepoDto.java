package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "scm_url",
        "org_identity",
        "repoList"
})

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCMRepoDto {

    @JsonProperty("scm_url")
    private String scmUrl;
    @JsonProperty("org_identity")
    private String orgIdentity;
    private List<RepoDto> repoList;
}