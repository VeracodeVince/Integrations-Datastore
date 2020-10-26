package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Scm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScmRepository extends JpaRepository<Scm, Long> {
}