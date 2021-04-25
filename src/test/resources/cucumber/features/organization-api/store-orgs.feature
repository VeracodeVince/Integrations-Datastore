Feature: APIs for working with SCM organizations

    Background:
        Given database contains the following SCMs:
            | id | repoBaseUrl                    |
            | 1  | https://scm1.example.com/repos |
            | 2  | https://scm2.example.com/test  |
        And database contains SCM tokens with IDs:
            | 1 |
            | 2 |
            | 3 |
        And database contains tenants with IDs:
            | 1 |
            | 2 |
            | 3 |
        And database contains the following organizations:
            | id | scmId | orgIdentity | cxFlowUrl            | cxFlowConfig   | tokenId | team         | tenantId |
            | 1  | 1     | myOrg1      | http://example.com/1 | initialConfig1 | 1       | initialTeam1 | 1        |
            | 2  | 2     | myOrg1      | http://example.com/2 | initialConfig2 | 2       | initialTeam2 | 2        |
            | 3  | 2     | myOrg2      | http://example.com/3 | initialConfig3 | 3       | initialTeam3 | 3        |

    Scenario Outline: Storing an organization
        When API client calls the `store organization` API with scmId: <scmId>, orgIdentity: <orgIdentity>, team: <teamInRequest>, cxFlowConfig: <cxFlowConfigInRequest>
        Then response status is 204
        And database contains an organization with scmId: <scmId>, orgIdentity: <orgIdentity>, team: <teamInDB>, cxFlowUrl: <cxFlowUrl>, cxFlowConfig: <cxFlowConfigInDB>, tokenId: <tokenId>, tenantId: <tenantId>
        Examples:
            | scmId | orgIdentity | teamInRequest | cxFlowConfigInRequest | teamInDB     | cxFlowConfigInDB | tokenId | cxFlowUrl            | tenantId |
            # Updating an existing org. Null request fields are ignored during the update.
            | 1     | myOrg1      | newTeam       | newConfig             | newTeam      | newConfig        | 1       | http://example.com/1 | 1        |
            | 1     | myOrg1      | <null>        | newConfig             | initialTeam1 | newConfig        | 1       | http://example.com/1 | 1        |
            | 1     | myOrg1      | newTeam       | <null>                | newTeam      | initialConfig1   | 1       | http://example.com/1 | 1        |
            | 1     | myOrg1      | <null>        | <null>                | initialTeam1 | initialConfig1   | 1       | http://example.com/1 | 1        |
            # Creating a new org.
            | 1     | newOrg      | newTeam       | newConfig             | newTeam      | newConfig        | <null>  | <null>               | <null>   |
            | 1     | newOrg      | <null>        | newConfig             | <null>       | newConfig        | <null>  | <null>               | <null>   |
            | 1     | newOrg      | newTeam       | <null>                | newTeam      | <null>           | <null>  | <null>               | <null>   |
            | 1     | newOrg      | <null>        | <null>                | <null>       | <null>           | <null>  | <null>               | <null>   |

    Scenario: Storing an organization: invalid SCM
        When API client calls the `store organization` API with scmId: 111, orgIdentity: myOrg1, team: newTeam, cxFlowConfig: newConfig
        Then response status is 404
        And response has a non-empty "message" field

    Scenario: Importing multiple organizations
    Checking for both an existing and new organization.
        When API client calls the `import organizations` API request with scmId: 1 and the following items:
            | orgIdentity | tokenId |
            | myOrg1      | 2       |
            | newOrg      | 1       |
        Then response status is 204
        And database contains, among others, organizations with the following fields:
            | scmId | orgIdentity | team         | cxFlowUrl            | cxFlowConfig   | tokenId | tenantId |
            | 1     | myOrg1      | initialTeam1 | http://example.com/1 | initialConfig1 | 2       | 1        |
            | 1     | newOrg      | <null>       | <null>               | <null>         | 1       | <null>   |

    Scenario: Importing multiple organizations: invalid SCM
        When API client calls the `import organizations` API request with scmId: 111 and the following items:
            | orgIdentity | tokenId |
            | myOrg1      | 2       |
            | newOrg      | 1       |
        Then response status is 404
        And response has a non-empty "message" field

    Scenario: Importing multiple organizations: invalid token ID
        When API client calls the `import organizations` API request with scmId: 1 and the following items:
            | orgIdentity | tokenId |
            | myOrg1      | 2       |
            | newOrg      | 1111    |
        Then response status is 404
        And response has a non-empty "message" field