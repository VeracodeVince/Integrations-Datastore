package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScmTokenRepository extends JpaRepository<Token, Long> {
}