package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmOrgRepository extends JpaRepository<ScmOrg, Long> {

    @Query(value = "SELECT s FROM ScmOrg s WHERE s.name = ?1 AND s.scm.name = ?2")
    ScmOrg getScmOrg(String orgName, String scmName);
}