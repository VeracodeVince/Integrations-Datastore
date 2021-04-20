package com.checkmarx.integrations.datastore.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Contains fields that can be changed by a single organization update request.
 */
@Data
@Builder
public class SCMOrgUpdateDto {
    @ToString.Exclude
    private String cxFlowConfig;

    private String team;
}
