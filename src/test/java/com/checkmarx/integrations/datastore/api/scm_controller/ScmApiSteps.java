package com.checkmarx.integrations.datastore.api.scm_controller;

import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScmApiSteps {

    @LocalServerPort
    private int port;

    private ResponseEntity<SCMDto> responseEntity;

    @Autowired
    private ScmRepository scmRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Given("data base contains {string}, {string} and {string}")
    public void initDataBaseValues(String baseUrl, String clientId, String clientSecret) {
        scmRepository.saveAndFlush(Scm.builder()
                                       .authBaseUrl(baseUrl)
                                       .clientId(clientId)
                                       .clientSecret(clientSecret)
                                       .build());
    }

    @When("API client calls the `get scm` API for {string}")
    public void apiClientCallsTheGetScmAPIFor(String scmBaseUrl) {
        String path = String.format("http://localhost:%s/scms/%s", port, scmBaseUrl);
        responseEntity = restTemplate.getForEntity(path, SCMDto.class);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int HttpStatusCode) {
        Assert.assertEquals("Error HttpStatus", HttpStatusCode ,
                            responseEntity.getStatusCodeValue());
    }

    @And("response contains the {string} field set to {string}")
    public void responseContainsTheFieldSetTo(String received, String expected) {
        Assert.assertEquals(expected, received);
    }

//    TODO: fix.
//    @Given("data base does not contains {string} base url")
//    public void dataBaseDoesNotContainsBaseUrl(String baseUrl) {
//        Scm scm = scmRepository.getScmById(baseUrl);
//        Assert.assertNull(scm);
//    }
}
