package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scms")
@Slf4j
public class ScmController {

    private final ScmService scmService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Gets details of all SCMs")
    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @Operation(summary = "Gets a SCM by baseUrl")
    @GetMapping(value = "{baseUrl}")
    public SCMDto getScmByBaseUrl(@PathVariable String baseUrl) {
        log.trace("getScmByBaseUrl: baseUrl:{}", baseUrl);
        Scm scmByBaseUrl = scmService.getScmByBaseUrl(baseUrl);
        return modelMapper.map(scmByBaseUrl, SCMDto.class);
    }

    @Operation(summary = "Stores a new SCM")
    @PostMapping(value = "/storeScm")
    public ResponseEntity createScm(@RequestBody SCMDto scmDto) {
        log.trace("createScm: scmDto={}", scmDto);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Scm scm = modelMapper.map(scmDto, Scm.class);
        scmService.createOrUpdateScm(scm);

        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Deletes a SCM by id")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@PathVariable Long id) {
        log.trace("deleteScm: id={}", id);
        scmService.deleteScm(id);
    }

}