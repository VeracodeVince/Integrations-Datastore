package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Scm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface ScmRepository extends JpaRepository<Scm, Long> {
    @Query(value = "SELECT s FROM Scm s WHERE s.repoBaseUrl = ?1")
    Scm getScmByRepoBaseUrl(String repoBaseUrl);

    @Modifying
    @Transactional
    @Query("UPDATE Scm s SET s.clientSecret = ?1 WHERE s.repoBaseUrl = ?2")
    void updateScmClientSecret(String clientSecret, String repoBaseUrl);
}