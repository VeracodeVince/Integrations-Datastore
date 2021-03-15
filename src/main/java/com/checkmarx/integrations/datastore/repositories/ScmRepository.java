package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Scm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmRepository extends JpaRepository<Scm, Long> {
    @Query(value = "SELECT s FROM Scm s WHERE s.repoBaseUrl = ?1")
    Scm getScmByRepoBaseUrl(String repoBaseUrl);
}