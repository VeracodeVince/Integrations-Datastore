Feature: Test Repo Controller endpoints

  Background: database contains SCM organizations and multiple SCM repos entities

  Scenario Outline: Validate a list of org repos are getting back successfully from
  database when passing SCM base-url and SCM organization details

    When getCxFlowProperties endpoint is getting called with "<scm-base-url>" and "<org-identity>"
    Then "getScmReposByOrgIdentity" response status is 200
    And <number-of-repos> repos are expected to be retrieved back
    Examples:
      | scm-base-url   | org-identity       | number-of-repos |
      | githubTest.com | orgNameTest        | 2               |
      | githubTest.com | invalidOrgNameTest | 0               |


  Scenario: Validate a list of Repo DTOs are getting back successfully from database
    When getCxFlowProperties endpoint is getting called with "githubTest.com" and "orgNameTest"
    Then "getScmReposByOrgIdentity" response status is 200
    And a list of Repo DTOs details are fully retrieved

  Scenario: Validate a single Repo DTO is getting back successfully from database
    When getScmRepo endpoint is getting called with "githubTest.com" "orgNameTest" and "repo-1"
    Then "getScmRepo" response status is 200
    And a Repo DTO details are fully retrieved

  Scenario: Set invalid repo identity and validate returned response status is 404
    When getScmRepo endpoint is getting called with "githubTest.com" "orgNameTest" and "invalid-repo-name"
    Then "getScmRepo" response status is 404



