package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScmService {

    private final ScmRepository scmRepository;

    public Scm createScm(Scm scm) {
        return scmRepository.saveAndFlush(scm);
    }
}