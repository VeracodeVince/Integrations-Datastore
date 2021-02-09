package com.checkmarx.integrations.datastore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Contains organization fields that can be changed during import.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SCMOrgShortDto {
    private String orgIdentity;
    private long tokenId;
}
