package com.checkmarx.integrations.datastore.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Contains fields that can be changed by a single organization update request.
 */
@Data
public class SCMOrgUpdateDto {
    @ToString.Exclude
    private String cxGoToken;

    private String team;
}
