package com.checkmarx.integrations.datastore.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ScanDetailsDto {
    @NotBlank(message = "Scan ID cannot be empty.")
    private String scanId;

    @NotNull(message = "Scan details body cannot be null.")
    private String body;
}
