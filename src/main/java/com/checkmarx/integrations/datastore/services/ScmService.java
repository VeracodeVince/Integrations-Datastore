package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMCreateDto;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmType;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.checkmarx.integrations.datastore.utils.ErrorMessages.SCM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScmService {
    private final ScmRepository scmRepository;
    private final ModelMapper modelMapper;

    public void deleteScm(long id) {
        try {
            scmRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format(SCM_NOT_FOUND, id), e);
        }
    }

    public List<SCMDto> getAllScms() {
        return scmRepository.findAll()
                .stream()
                .map(this::toScmDto)
                .collect(Collectors.toList());
    }

    public SCMDto getScmById(long id) {
        return scmRepository.findById(id)
                .map(this::toScmDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(SCM_NOT_FOUND, id)));
    }

    public long createScm(SCMCreateDto scm, ScmType type) {
        Scm scmToCreate = modelMapper.map(scm, Scm.class);
        scmToCreate.setType(type);

        Scm newScm = scmRepository.saveAndFlush(scmToCreate);
        return newScm.getId();
    }

    private SCMDto toScmDto(Scm scm) {
        SCMDto result = modelMapper.map(scm, SCMDto.class);
        ScmType type = scm.getType();
        result.setType(type.getName());
        result.setDisplayName(type.getDisplayName());
        result.setScope(type.getScope());
        return result;
    }
}