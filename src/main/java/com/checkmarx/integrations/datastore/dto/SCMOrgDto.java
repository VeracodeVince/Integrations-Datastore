package com.checkmarx.integrations.datastore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Full organization info.
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SCMOrgDto extends SCMOrgShortDto {
    private long id;

    private long scmId;

    private String cxFlowUrl;

    private String cxFlowConfig;

    private String team;
}