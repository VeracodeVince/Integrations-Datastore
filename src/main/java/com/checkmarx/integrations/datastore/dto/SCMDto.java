package com.checkmarx.integrations.datastore.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents an SCM in 'get' operations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SCMDto extends SCMCreateDto {
    private long id;

    private String scope;
}