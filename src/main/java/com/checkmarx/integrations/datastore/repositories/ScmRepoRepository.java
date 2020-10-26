package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScmRepoRepository extends JpaRepository<ScmRepo, Long> {
}