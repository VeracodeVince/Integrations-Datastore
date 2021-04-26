package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.TenantDto;
import com.checkmarx.integrations.datastore.dto.TenantShortDto;
import com.checkmarx.integrations.datastore.models.Tenant;
import com.checkmarx.integrations.datastore.repositories.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantDto toTenantResponse(Tenant tenantFromStorage) {
        return TenantDto.builder()
                .id(tenantFromStorage.getId())
                .tenantIdentity(tenantFromStorage.getTenantIdentity())
                .build();
    }

    public long createTenantIfDoesntExist(TenantShortDto tenant) {
        Tenant existingTenant = searchByTenantIdentity(tenant.getTenantIdentity());
        return Optional.ofNullable(existingTenant)
                .map(Tenant::getId)
                .orElseGet(createTenant(tenant));
    }

    private Tenant searchByTenantIdentity(String tenantIdentity) {
        Example<Tenant> havingSameTenantIdentity = Example.of(Tenant.builder()
                                                                  .tenantIdentity(tenantIdentity)
                                                                  .build());
        List<Tenant> existingTenants = tenantRepository.findAll(havingSameTenantIdentity, Sort.by("id"));
        return existingTenants.stream().findFirst().orElse(null);
    }

    private Supplier<Long> createTenant(TenantShortDto tenant) {
        return () -> {
            Tenant tenantInfo = Tenant.builder()
                    .tenantIdentity(tenant.getTenantIdentity())
                    .build();
            Tenant newTenant = tenantRepository.saveAndFlush(tenantInfo);
            return newTenant.getId();
        };
    }

}
