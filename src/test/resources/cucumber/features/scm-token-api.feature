Feature: SCM access token API

    Scenario: Getting an access token
        Given database contains an SCM with ID 1
        And database contains "myAwesomeOrg" organization with scmId: 1 and token: "token1"
        When API client calls the `get access token` API for the "myAwesomeOrg" organization and scmId: 1
        Then response status is 200
        And response contains `accessToken` field set to "token1" and `id` field set to 1

    Scenario: Updating an access token
        Given database contains an access token with ID: 1 and "currentToken" value
        When API client calls the `update access token` API with token ID: 1 and token value: "newToken"
        Then response status is 204
        And database now contains an access token with ID: 1 and "newToken" value

    Scenario: Creating an access token
        Given database does not contain access tokens
        And API client calls the `create access token` API with token value: "token1"
        Then database now contains an access token with ID: 1 and "token1" value
        And response status is 201
        And response body is set to 1

    Scenario: Calling the "create token" API when token with the same value already exists
    Here we don't create a new token, but rather reuse an existing one and return its ID.
        Given database contains an access token with ID: 1 and "token1" value
        When API client calls the `create access token` API with token value: "token1"
        Then database still contains an access token with ID: 1 and "token1" value
        And database does not contain other access tokens with the "token1" value
        And response status is 201
        And response body is set to 1

    Scenario Outline: Trying to get access token for an invalid combination of organization and SCM
        Given database contains SCMs with IDs:
            | 1 |
            | 2 |
        And myOrg1 is the only organization belonging to the SCM with ID 1
        And myOrg2 is the only organization belonging to the SCM with ID 2
        When API client calls the `get access token` API for the "<orgIdentity>" organization and scmId: <scmId>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | scmId | orgIdentity |
            | 1     | invalidOrg  |
            | 1     | myOrg2      |
            | 99    | myOrg1      |
            | 99    | invalidOrg  |

    Scenario: Trying to update access token using an invalid id
        Given database does not contain an access token with ID: 99
        When API client calls the `update access token` API with token ID: 99 and token value: "newToken"
        Then response status is 404
        And response has a non-empty "message" field

