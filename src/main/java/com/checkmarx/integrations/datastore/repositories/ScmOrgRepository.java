package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScmOrgRepository extends JpaRepository<ScmOrg, Long> {
}