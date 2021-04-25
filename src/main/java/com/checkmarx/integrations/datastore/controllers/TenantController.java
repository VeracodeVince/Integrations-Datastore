package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.TenantDto;
import com.checkmarx.integrations.datastore.dto.TenantShortDto;
import com.checkmarx.integrations.datastore.services.StorageService;
import com.checkmarx.integrations.datastore.services.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantService tenantService;
    private final StorageService storageService;

    @Operation(summary = "Creates an Org tenant.",
            description = "Creates a tenant and returns its ID. If a tenant with exactly the same name already exists, returns the existing tenant ID.")
    @PostMapping
    @ApiResponse(responseCode = "201", description = "New or existing tenant ID.")
    @ResponseStatus(HttpStatus.CREATED)
    public long createTenant(@RequestBody TenantShortDto tenant) {
        return tenantService.createTenantIfDoesntExist(tenant);
    }

    @Operation(summary = "Gets tenant for a specific organization.")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Tenant found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Tenant was not found", content = @Content)
    public TenantDto getTenant(@RequestParam long scmId, @RequestParam String orgIdentity) {
        log.trace("getTenant: scmId={} orgIdentity={}", scmId, orgIdentity);
        TenantDto result = storageService.getTenant(scmId, orgIdentity);
        log.trace("getTenant: returning tenant with ID={}", result.getId());

        return result;
    }
}
