Feature: Test Scm Org Controller endpoints

  Scenario: Store new Scm org details and validate response status
    When storeScmOrg POST endpoint is getting called with "githubTest.com" as scm-url and with "orgNameTest" as org identity
    Then "storeScmOrg" response status is 200

  Scenario: Validate new scm org entity details are getting back successfully from database
    Given Scm org with "githubTest.com" as scm-url and "orgNameTest" as org identity is stored in database
    When getCxFlowProperties endpoint is getting called with "githubTest.com" as scm-url and "orgNameTest" as org identity
    Then "getCxFlowProperties" response status is 200
    And response contains scmUrl field set to "githubTest.com"
    And response contains orgIdentity field set to "orgNameTest"

  Scenario: Validate CxFlowProperties DTO is getting back successfully from database
    Given cx-flow details are stored into database
    When getCxFlowProperties endpoint is getting called with "githubTest.com" as scm-url and "orgNameTest" as org identity
    Then "getCxFlowProperties" response status is 200
    And CxFlowProperties DTO details are fully retrieved


  Scenario: Get invalid scm org entity and validate returned response status is 404
    When getCxFlowProperties endpoint is getting called with invalid scm org parameter
    Then "getCxFlowProperties" response status is 404

