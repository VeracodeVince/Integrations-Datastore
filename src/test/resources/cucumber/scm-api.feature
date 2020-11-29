Feature: SCM CRUD APIs

    Scenario Outline: Getting SCM details by a valid SCM base url
        Given data base contains "<scm base url>", "<Client Id>" and "<Client Secret>"
        When API client calls the `get scm` API for "<scm base url>"
        Then response status is 200
        And response contains the "baseUrl" field set to "<scm base url>"
        And response contains the "Client Id" field set to "<Client Id>"
        And response contains the "Client Secret" field set to "<Client Secret>"
        Examples:
            | scm base url    | Client Id             | Client Secret                            |
            | github.com      | ff718111a48803ba73566 | 7a260229fc9adf2c34f5e06e2b665f0f06094666 |