Feature: SCM access token API

    Scenario: Getting access token by organization and SCM
        Given database contains "my-awesome-org" organization belonging to the "github" SCM
        And the organization's access token is "access-token-524"
        When API client calls the "get access token" API for the "my-awesome-org" organization of "github" SCM
        Then response status is 200
        And response contains accessToken field set to "access-token-524"
        And response contains id field set to a positive numeric value

    Scenario: Updating access token using a valid id
        Given database contains access token with the N id and "old-token" value
        When API client creates a request with accessToken field set to "new-token"
        And API client calls the "update access token" API with access token id set to N, using the request above
        Then response status is 204
        And database contains access token with the N id and "new-token" value

    Scenario: Creating an access token
        Given database does not contain access tokens
        When API client creates a request with accessToken field set to "token-936"
        And API client calls the "create access token" API, using the request above
        Then database contains access token with the N id and "token-936" value
        And response status is 200
        And response body is set to N

    Scenario: Calling the "create token" API when token with the same value already exists
    Here we don't create a new token, but rather reuse an existing one.
        Given database contains access token with the N id and "token-857" value
        When API client creates a request with accessToken field set to "token-857"
        And API client calls the "create access token" API, using the request above
        Then database still contains an access token with the N id and "token-857" value
        And database does not contain other access tokens with the "token-857" value
        And response status is 200
        And response body is set to N

    Scenario Outline: Trying to get access token for an invalid combination of organization and SCM
        Given my-hub-org is the only organization belonging to the github SCM
        And my-lab-org is the only organization belonging to the gitlab SCM
        When API client calls the "get access token" API for the <org> organization of <scm> SCM
        Then response status is 404
        Examples:
            | scm             | org             |
            | github          | nonexistent-org |
            | nonexistent-scm | my-hub-org      |
            | github          | my-lab-org      |
            | nonexistent-scm | nonexistent-org |

  Scenario: Trying to update access token using an invalid id
    TODO

