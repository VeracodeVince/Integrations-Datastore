Feature: Source Control Manager APIs

    Background:
        Given database contains the following SCM types:
            | name   | displayName | scope  |
            | github | GitHub      | scope1 |
            | gitlab | GitLab      | scope2 |
        And database contains the following SCMs:
            | id | type   | authBaseUrl      | apiBaseUrl      | repoBaseUrl      | clientId  | clientSecret  |
            | 1  | github | http://auth1.url | http://api1.url | http://repo1.url | clientId1 | clientSecret1 |
            | 2  | github | http://auth2.url | http://api2.url | http://repo2.url | clientId2 | clientSecret2 |
            | 3  | gitlab | http://auth3.url | http://api3.url | http://repo3.url | clientId3 | clientSecret3 |


    Scenario: Getting SCM list
        When API client calls the `get SCMs` API
        Then response status is 200
        And the response is an array of 3 objects with the following fields:
            | id | type   | authBaseUrl      | apiBaseUrl      | repoBaseUrl      | clientId  | clientSecret  | displayName | scope  |
            | 1  | github | http://auth1.url | http://api1.url | http://repo1.url | clientId1 | clientSecret1 | GitHub      | scope1 |
            | 2  | github | http://auth2.url | http://api2.url | http://repo2.url | clientId2 | clientSecret2 | GitHub      | scope1 |
            | 3  | gitlab | http://auth3.url | http://api3.url | http://repo3.url | clientId3 | clientSecret3 | GitLab      | scope2 |

    Scenario: Getting SCM details by a valid SCM ID
        When API client calls the `get scm` API for SCM ID: 2
        Then response status is 200
        And response contains the following fields:
            | id | type   | authBaseUrl      | apiBaseUrl      | repoBaseUrl      | clientId  | clientSecret  | displayName | scope  |
            | 2  | github | http://auth2.url | http://api2.url | http://repo2.url | clientId2 | clientSecret2 | GitHub      | scope1 |

    Scenario: Getting SCM details by an invalid SCM ID
        When API client calls the `get scm` API for SCM ID: 93
        Then response status is 404
        And response has a non-empty "message" field