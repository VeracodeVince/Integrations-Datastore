package com.checkmarx.integrations.datastore.controllers;

import java.util.List;

import javax.websocket.server.PathParam;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scms")
public class ScmController {

    private final ScmService scmService;

    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @GetMapping(path = "/{id}")
    public Scm getScm(@PathVariable String id) {
        return scmService.getScm(Long.valueOf(id));
    }

    @PostMapping
    public Scm createScm(@RequestBody final Scm scm) {
        return scmService.createScm(scm);
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@RequestBody final Scm scm) {
        scmService.deleteScm(scm);
    }

}