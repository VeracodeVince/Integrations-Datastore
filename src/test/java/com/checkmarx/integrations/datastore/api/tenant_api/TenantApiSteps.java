package com.checkmarx.integrations.datastore.api.tenant_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.UrlFormatter;
import com.checkmarx.integrations.datastore.dto.TenantShortDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Tenant;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.repositories.TenantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
public class TenantApiSteps {

    @LocalServerPort
    private int port;

    private final ApiTestState testState;
    private final ScmRepository scmRepo;
    private final ScmOrgRepository orgRepo;
    private final TenantRepository tenantRepo;
    private final UrlFormatter urlFormatter;
    private final TestRestTemplate restTemplate;

    @Given("database contains {string} organization with scmId: {int} and tenant: {string}")
    public void dbContainsOrg(String orgIdentity, long scmId, String tenantIdentity) {
        createOrgWithTenant(orgIdentity, tenantIdentity, scmRepo.getOne(scmId));
    }

    @When("API client calls the `get tenant` API for the {string} organization and scmId: {int}")
    public void clientCallsGetTenantApi(String orgIdentity, long scmId) {
        URI url = urlFormatter.format("tenants?scmId={scmId}&orgIdentity={orgIdentity}",
                                      port, scmId, orgIdentity);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @Then("response contains `tenantIdentity` field set to {string} and `id` field set to {int}")
    public void responseContainsFields(String tenantIdentity, long id) {
        JsonNode response = testState.getResponseBodyOrThrow();
        verifyField("tenantIdentity", response, tenantIdentity);
        verifyField("id", response, Long.toString(id));
    }

    @Given("database does not contain tenants")
    public void dbDoesNotContainTokens() {
        tenantRepo.deleteAll();
    }

    @When("API client calls the `create tenant` API with tenant value: {string}")
    public void clientCallsCreateTenantApi(String tenantIdentityValue) {
        URI url = urlFormatter.format("tenants", port);
        HttpEntity<TenantShortDto> request = getTenantRequest(tenantIdentityValue);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @Then("database now/still contains a tenant with ID: {int} and {string} value")
    public void thenDbContainsTenant(long id, String value) {
        Optional<Tenant> tenantInfo = tenantRepo.findById(id);
        assertTrue(String.format("Unable to find tenant by ID: %d", id), tenantInfo.isPresent());
        assertEquals("Unexpected tenant value.", value, tenantInfo.get().getTenantIdentity());
    }

    @Then("response body is set to {word}")
    public void responseBodyIsSetTo(String body) {
        JsonNode response = testState.getResponseBodyOrThrow();
        assertEquals("Unexpected response body.", body, response.asText());
    }

    @Given("database contains a tenant with ID: {int} and {string} value")
    public void givenDbContainsTenant(long id, String tenantIdentity) {
        Tenant createdTenant = createTenant(tenantIdentity);
        assertEquals("Wrong tenant ID after creation.", id, createdTenant.getId().longValue());
    }

    @Then("database does not contain other tenants with the {string} value")
    public void dbDoesNotContainTenant(String value) {
        long tenantCount = tenantRepo.count(Example.of(Tenant.builder().tenantIdentity(value).build()));
        assertEquals(String.format("Database contains other tenants with the value: '%s'", value) , 1, tenantCount);
    }

    private HttpEntity<TenantShortDto> getTenantRequest(String tenantIdentityValue) {
        return new HttpEntity<>(TenantShortDto.builder()
                                        .tenantIdentity(tenantIdentityValue)
                                        .build());
    }

    private void createOrgWithTenant(String orgIdentity, String tenantIdentity, Scm one) {
        Tenant createdTenant = createTenant(tenantIdentity);

        ScmOrg orgToCreate = ScmOrg.builder()
                .orgIdentity(orgIdentity)
                .scm(one)
                .tenant(createdTenant)
                .build();
        orgRepo.saveAndFlush(orgToCreate);
    }

    private Tenant createTenant(String tenantIdentity) {
        return tenantRepo.saveAndFlush(Tenant.builder()
                                              .tenantIdentity(tenantIdentity)
                                              .build());
    }

    private void verifyField(String fieldName, JsonNode json, String expectedValue) {
        assertTrue(String.format("Response doesn't have the '%s' field.", fieldName),
                   json.has(fieldName));

        assertEquals(String.format("Unexpected value for the '%s' field.", fieldName),
                     expectedValue, json.get(fieldName).asText());
    }
}
