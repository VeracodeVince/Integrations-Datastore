Feature: APIs for getting an SCM organization

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

    Scenario Outline: Getting organization by identity and SCM
    Includes the case when organizations with the same identity exist in several SCMs.
        When API client calls the `get organization` API with scmId: <scmId> and orgIdentity: <orgIdentity>
        Then response status is 200
        And response fields correspond to an organization with the ID: <orgId> in the database
        Examples:
            | scmId | orgIdentity | orgId |
            | 1     | myOrg1      | 1     |
            | 2     | myOrg1      | 2     |
            | 2     | myOrg2      | 3     |

    Scenario Outline: Getting organization by identity and SCM: wrong combination of parameters
        When API client calls the `get organization` API with scmId: <scmId> and orgIdentity: <orgIdentity>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | scmId | orgIdentity |
            | 1     | myOrg2      |
            | 1     | anotherOrg  |
            | 10    | myOrg1      |
            | 10    | anotherOrg  |

    Scenario Outline: Getting organization by identity and repository base URL
        When API client calls the `get organization` API with repo base URL: <repoBaseUrl> and orgIdentity: <orgIdentity>
        Then response status is 200
        And response fields correspond to an organization with the ID: <orgId> in the database
        Examples:
            | repoBaseUrl                    | orgIdentity | orgId |
            | https://scm1.example.com/repos | myOrg1      | 1     |
            | https://scm2.example.com/test  | myOrg1      | 2     |
            | https://scm2.example.com/test  | myOrg2      | 3     |

    Scenario Outline: Getting organization by identity and repository base URL: wrong combination of parameters
        When API client calls the `get organization` API with repo base URL: <repoBaseUrl> and orgIdentity: <orgIdentity>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | repoBaseUrl                    | orgIdentity |
            # URL exists, org doesn't
            | https://scm1.example.com/repos | anotherOrg  |
            # URL doesn't exist, org does
            | https://wrong.example.com      | myOrg1      |
            # Neither URL nor org exist
            | https://wrong.example.com      | anotherOrg  |
            # Both URL and org exist, but the org belongs to another SCM
            | https://scm1.example.com/repos | myOrg2      |
            # Case sensitivity check - URL
            | https://scm1.example.com/Repos | myOrg1      |
            # Case sensitivity check - org identity
            | https://scm1.example.com/repos | MyOrg1      |
