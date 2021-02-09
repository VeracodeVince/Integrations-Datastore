package com.checkmarx.integrations.datastore.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenShortDto {
    private String accessToken;
}
