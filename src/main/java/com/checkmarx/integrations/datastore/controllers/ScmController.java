package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scms")
@Slf4j
public class ScmController {

    private final ScmService scmService;

    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @PostMapping
    public Scm createScm(@RequestBody final Scm scm) {
        log.trace("createScm: scm={}", scm);
        return scmService.createScm(scm);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@PathVariable Long id) {
        log.trace("deleteScm: id={}", id);
        scmService.deleteScm(id);
    }

}