package com.checkmarx.integrations.datastore.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AccessTokenDto extends AccessTokenShortDto {
    private long id;
}
