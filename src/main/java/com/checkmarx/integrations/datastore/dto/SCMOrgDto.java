package com.checkmarx.integrations.datastore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SCMOrgDto {
    private String orgIdentity;
    private long tokenId;
}
