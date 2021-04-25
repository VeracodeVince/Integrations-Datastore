package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}