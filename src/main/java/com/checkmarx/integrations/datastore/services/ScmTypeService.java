package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.models.ScmType;
import com.checkmarx.integrations.datastore.repositories.ScmTypeRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ScmTypeService {
    private final ScmTypeRepository repo;

    public ScmType getByName(String name) {
        return Optional.ofNullable(repo.getByName(name)).orElseThrow(() -> {
            String message = String.format(ErrorMessages.INVALID_SCM_TYPE, name);
            return new EntityNotFoundException(message);
        });
    }
}
