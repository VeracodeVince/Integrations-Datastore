package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.ScmNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
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
import java.util.Optional;

import static com.checkmarx.integrations.datastore.utils.ErrorConstsMessages.SCM_NOT_FOUND;

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

    @Operation(summary = "Gets a SCM")
    @GetMapping(value = "{baseUrl}")
    @ApiResponse(responseCode = "200", description = "SCM found", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM not found", content = @Content)
    public SCMDto getScmByBaseUrl(@PathVariable String baseUrl) {
        log.trace("getScmByBaseUrl: baseUrl:{}", baseUrl);
        Scm scmByBaseUrl = Optional.ofNullable(scmService.getScmByBaseUrl(baseUrl))
                .orElseThrow(() -> new ScmNotFoundException(String.format(SCM_NOT_FOUND, baseUrl)));
        SCMDto scmDto = ObjectMapperUtil.map(scmByBaseUrl, SCMDto.class);
        log.trace("getScmByBaseUrl: scmDto={}", scmDto);

        return scmDto;
    }

    @Operation(summary = "Stores a new SCM")
    @PostMapping(value = "/storeScm")
    public ResponseEntity storeScm(@RequestBody SCMDto scmDto) {
        log.trace("storeScm: scmDto={}", scmDto);
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