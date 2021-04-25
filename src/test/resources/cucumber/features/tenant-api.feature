Feature: Tenant API

  Scenario: Getting a Tenant
    Given database contains an SCM with ID 1
    And database contains "myAwesomeOrg" organization with scmId: 1 and tenant: "tenant1"
    When API client calls the `get tenant` API for the "myAwesomeOrg" organization and scmId: 1
    Then response status is 200
    And response contains `tenantIdentity` field set to "tenant1" and `id` field set to 1

  Scenario: Creating a Tenant
    Given database does not contain tenants
    And API client calls the `create tenant` API with tenant value: "tenant1"
    Then database now contains a tenant with ID: 1 and "tenant1" value
    And response status is 201
    And response body is set to 1

  Scenario: Calling the "create tenant" API when tenant with the same value already exists
  Here we don't create a new tenant, but rather reuse an existing one and return its ID.
    Given database contains a tenant with ID: 1 and "tenant1" value
    When API client calls the `create tenant` API with tenant value: "tenant1"
    Then database still contains a tenant with ID: 1 and "tenant1" value
    And database does not contain other tenants with the "tenant1" value
    And response status is 201
    And response body is set to 1