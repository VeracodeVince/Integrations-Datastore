package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmTokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "SELECT t FROM Token t WHERE t.scmOrg.orgIdentity = ?1 AND t.type = ?2")
    Token getToken(String orgIdentity, String type);

    @Query(value = "SELECT t FROM Token t WHERE t.scmOrg.orgIdentity = ?1")
    Token getTokenByOrgIdentity(String orgIdentity);
}