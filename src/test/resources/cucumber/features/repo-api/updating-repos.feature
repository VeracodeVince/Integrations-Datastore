Feature: APIs for updating SCM repositories
    Background:
        Given database contains SCMs with IDs:
            | 1 |
            | 2 |
        And database contains the following organizations:
            | id | scmId | orgIdentity         |
            | 1  | 1     | existingOrg1        |
            | 2  | 1     | existingOrg2NoRepos |
            | 3  | 2     | existingOrg3        |
        And database contains the following repos:
            | id | repoIdentity  | webhookId     | isWebhookConfigured | orgId |
            | 1  | existingRepo1 | existingHook1 | true                | 1     |
            | 2  | existingRepo2 | <null>        | false               | 1     |
            | 3  | existingRepo3 | existingHook3 | true                | 2     |

    Scenario: Importing repos for an existing organization
        When API client calls the `import repos` API for scmId: 1, orgIdentity: existingOrg1, with the following repos:
            | repo_identity | webhook_id  | is_webhook_configured |
            | existingRepo1 | <null>      | false                 |
            | existingRepo2 | newWebhook1 | true                  |
            | newRepo       | newWebhook2 | true                  |
        Then response status is 204
        And database contains, among others, the following repos:
            | id | repoIdentity  | webhookId   | isWebhookConfigured | orgId |
            | 1  | existingRepo1 | <null>      | false               | 1     |
            | 2  | existingRepo2 | newWebhook1 | true                | 1     |
            | 4  | newRepo       | newWebhook2 | true                | 1     |

    Scenario: Importing repos for a new organization
        When API client calls the `import repos` API for scmId: 1, orgIdentity: newOrg, with the following repos:
            | repo_identity | webhook_id | is_webhook_configured |
            | newRepo1      | <null>     | false                 |
            | newRepo2      | newWebhook | true                  |
        Then response status is 204
        And database now contains an organization with id: 4, scmId: 1, orgIdentity: newOrg
        And database contains, among others, the following repos:
            | id | repoIdentity | webhookId  | isWebhookConfigured | orgId |
            | 4  | newRepo1     | <null>     | false               | 4     |
            | 5  | newRepo2     | newWebhook | true                | 4     |

    Scenario: Importing repos: invalid SCM
        When API client calls the `import repos` API for scmId: 99, orgIdentity: anyOrg
        Then response status is 404
        And response has a non-empty "message" field

    Scenario Outline: Updating an existing repo
        When API client calls the `update repo` API with scmId: 1, orgIdentity: existingOrg1, repoIdentity: <repoIdentity>, webhook_id: <webhook_id>, is_webhook_configured: <is_webhook_configured>
        Then response status is 204
        And database contains a repo with id: <id>, repoIdentity: <repoIdentity>, webhookId: <webhook_id>, isWebhookConfigured: <is_webhook_configured>, orgId: 1
        Examples:
            | id | repoIdentity  | webhook_id | is_webhook_configured |
            | 1  | existingRepo1 | <null>     | false                 |
            | 2  | existingRepo2 | newHook    | true                  |

    Scenario Outline: Trying to update a non-existent repo
        When API client calls the `update repo` API with scmId: <scmId>, orgIdentity: <orgIdentity>, repoIdentity: <repoIdentity>
        Then response status is 404
        And response has a non-empty "message" field
        Examples:
            | scmId | orgIdentity  | repoIdentity  |
            | 99    | existingOrg1 | existingRepo1 |
            | 1     | invalidOrg   | existingRepo1 |
            | 1     | existingOrg1 | invalidRepo   |
            | 2     | existingOrg1 | existingRepo1 |
            | 1     | existingOrg1 | existingRepo3 |
