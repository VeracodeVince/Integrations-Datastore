Feature: Scan details API

    Scenario: creating scan details
        When API client calls the `create scan details` API with scan ID: mc3glw and body: '{"foo":"bar","list":[1,2,3]}'
        Then response status is 201
        And database contains scan details with scan ID: mc3glw and body: '{"foo":"bar","list":[1,2,3]}'

    Scenario Outline: trying to create scan details with invalid request fields
        When API client calls the `create scan details` API with scan ID: <scanId> and body: '<bodyInRequest>'
        Then response status is 400
        And database does not contain scan details for scan ID: <scanId>
        Examples:
            | scanId    | bodyInRequest                |
            | <empty>   | {"foo":"bar","list":[1,2,3]} |
            | <missing> | {"foo":"bar","list":[1,2,3]} |
            | mc3glw    | <empty>                      |
            | mc3glw    | <missing>                    |

    Scenario: trying to create scan details twice for the same scan
        Given database initially contains scan details with scan ID: imw92pqz and body: '{"abc":123}'
        When API client calls the `create scan details` API with scan ID: imw92pqz and body: '{"def":987}'
        Then response status is 400
        And database contains scan details with scan ID: imw92pqz and body: '{"abc":123}'

    Scenario: getting existing scan details
        Given database initially contains scan details with scan ID: ozpd3jan4 and body: '{"abc":123}'
        When API client calls the `get scan details` API with scan ID: ozpd3jan4
        Then response status is 200
        And response content is '{"abc":123}'

    Scenario: trying to get details for a non-existent scan
        Given database initially contains scan details with scan ID: scan-id-1 and body: '{"abc":123}'
        When API client calls the `get scan details` API with scan ID: scan-id-2
        Then response status is 404

