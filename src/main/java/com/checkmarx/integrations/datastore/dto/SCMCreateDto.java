package com.checkmarx.integrations.datastore.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SCMCreateDto {
    private String type;
    private String authBaseUrl;
    private String apiBaseUrl;
    private String displayName;
    private String repoBaseUrl;
    private String clientId;

    @ToString.Exclude
    private String clientSecret;
}