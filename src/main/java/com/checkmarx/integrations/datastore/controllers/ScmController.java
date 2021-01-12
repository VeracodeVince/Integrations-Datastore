package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.StorageService;
import com.checkmarx.integrations.datastore.utils.ObjectMapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private final StorageService storageService;

    @Operation(summary = "Gets details of all SCMs")
    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @Operation(summary = "Gets a SCM")
    @GetMapping(value = "{baseUrl}")
    @ApiResponse(responseCode = "200", description = "SCM base URL found", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM base URL wan not found", content = @Content)
    public SCMDto getScmByBaseUrl(@PathVariable String baseUrl) {
        log.trace("getScmByBaseUrl: baseUrl:{}", baseUrl);
        Scm scmByBaseUrl = scmService.getScmByScmUrl(baseUrl);
        return ObjectMapperUtil.map(scmByBaseUrl, SCMDto.class);
    }

    @Operation(summary = "Stores a new SCM")
    @PostMapping(value = "/storeScm")
    public ResponseEntity<Object> storeScm(@RequestBody SCMDto scmDto) {
        log.trace("storeScm: scmDto={}", scmDto);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        scmService.createOrUpdateScm(modelMapper.map(scmDto, Scm.class));

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deletes a SCM by id")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@PathVariable Long id) {
        log.trace("deleteScm: id={}", id);
        scmService.deleteScm(id);
    }

    @PutMapping(value = "{scmBaseUrl}/orgs")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void storeOrgList(@PathVariable String scmBaseUrl, @RequestBody List<SCMOrgDto> orgs) {
        log.trace("storeOrgList: baseUrl: {}, organization count: {}", scmBaseUrl, orgs.size());
        storageService.mergeOrgsIntoStorage(orgs, scmBaseUrl);
    }
}