package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.publishing.CxProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CxProjectRepository extends JpaRepository<CxProject, Long> {
    boolean existsByIdentity(String identity);
}
