Feature: Test Scm token Controller endpoints

  Scenario Outline: Store new Scm access token details and validate response status
    Given data base contains "<scm base url>", "<Client Id>" and "<Client Secret>"
    When storeScmAccessToken endpoint is getting called with "<scm base url>"
    Then "storeScmAccessToken" response status is 200
    Examples:
      | scm base url | Client Id             | Client Secret                            |
      | github.com   | ff718111a48803ba73566 | 7a260229fc9adf2c34f5e06e2b665f0f06094666 |
      | gitlab.com   | ff718111a48803ba73562 | 7a260229fc9adf2c34f5e06e2b665f0f06094662 |

  Scenario: Validate scm org access token DTO details are getting back successfully from data store
    When getScmAccessToken endpoint is getting called
    Then "getScmAccessToken" response status is 200
    And response contains scmUrl field set to "github.com"
    And response contains orgIdentity field set to "orgIdentityTest"
    And response contains accessToken field set to "access-token-ff718111a48803ba"
    And response contains tokenType field set to "access-token"

  Scenario: Getting scm org access token Dto by invalid scm url and validate returned response status is 404
    When getScmAccessToken endpoint is getting called with invalid scm "uknown-scm.com"
    Then "getScmAccessToken" response status is 404

  Scenario: Store scm org access token with invalid scm url and validate returned response status is 404
    When storeScmAccessToken endpoint is getting called with invalid scm "uknown-scm.com"
    Then "storeScmAccessToken" response status is 404