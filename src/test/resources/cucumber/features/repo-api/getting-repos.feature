Feature: APIs for retrieving SCM Repositories
    Background:
        Given database contains SCMs with IDs:
            | 1 |
            | 2 |

    Scenario: Getting organization repos
        Given database contains an organization with id: 1, scmId: 1, orgIdentity: existingOrg1
        And database contains the following repos:
            | id | repoIdentity  | webhookId     | isWebhookConfigured | orgId |
            | 1  | existingRepo1 | existingHook1 | true                | 1     |
            | 2  | existingRepo2 | <null>        | false               | 1     |
            | 3  | existingRepo3 | existingHook3 | true                | 2     |
        When API client calls the `get organization repos` API with scmId: 1 and orgIdentity: existingOrg1
        Then response status is 200
        And the response is an array of 2 objects with the following fields:
            | repo_identity | webhook_id    | is_webhook_configured |
            | existingRepo1 | existingHook1 | true                  |
            | existingRepo2 | <null>        | false                 |

    Scenario: Getting repos for an organization that doesn't have any
        Given database contains an organization with scmId: 1, orgIdentity: orgWithoutRepos, with no related repos
        When API client calls the `get organization repos` API with scmId: 1 and orgIdentity: orgWithoutRepos
        Then response status is 200
        And the response is an empty array

    Scenario Outline: Getting organization repos: wrong combination of parameters
        Given database contains an organization with scmId: 1, orgIdentity: existingOrg1
        When API client calls the `get organization repos` API with scmId: <scmId> and orgIdentity: <orgIdentity>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | scmId | orgIdentity  |
            | 1     | invalidOrg   |
            | 2     | existingOrg1 |
            | 99    | existingOrg1 |
            | 99    | invalidOrg   |

    Scenario: Getting a single repo
        Given database contains an organization with id: 1, scmId: 1, orgIdentity: existingOrg1
        And database contains the following repo:
            | repoIdentity  | webhookId     | isWebhookConfigured | orgId |
            | existingRepo1 | existingHook1 | true                | 1     |
        When API client calls the `get repo` API with scmId: 1, orgIdentity: existingOrg1, repoIdentity: existingRepo1
        Then response status is 200
        And response contains the following fields:
            | repo_identity | webhook_id    | is_webhook_configured |
            | existingRepo1 | existingHook1 | true                  |

    Scenario Outline: Getting a single repo: wrong combination of parameters
        Given database contains the following organizations:
            | id | scmId | orgIdentity  |
            | 1  | 1     | existingOrg1 |
            | 2  | 1     | existingOrg2 |
        And database contains the following repos:
            | id | repoIdentity  | orgId |
            | 1  | existingRepo1 | 1     |
            | 2  | existingRepo2 | 2     |
        When API client calls the `get repo` API with scmId: <scmId>, orgIdentity: <orgIdentity>, repoIdentity: <repoIdentity>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | scmId | orgIdentity  | repoIdentity  |
            | 99    | existingOrg1 | existingRepo1 |
            | 1     | invalidOrg   | existingRepo1 |
            | 1     | existingOrg1 | invalidRepo   |
            | 2     | existingOrg1 | existingRepo1 |
            | 1     | existingOrg1 | existingRepo2 |