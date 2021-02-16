Feature: Scan details API

  Scenario Outline: creating scan details
    When API client calls the `create scan details` API with scan ID: <scanId> and body: "<bodyInRequest>"
    Then response status is <status>
    And database "<containsOrNot>" scan details with scan ID: <scanId> and body: "<bodyInDB>"

    Examples:
      | scanId    | bodyInRequest | status | containsOrNot    | bodyInDB    |
      | mc3glw    | sample-body   | 201    | contains         | sample-body |
      | <empty>   | sample-body   | 400    | does not contain | <n/a>       |
      | <missing> | sample-body   | 400    | does not contain | <n/a>       |
      | mc3glw    | <empty>       | 201    | contains         | <empty>     |
      | mc3glw    | <missing>     | 400    | does not contain | <n/a>       |

  Scenario: trying to create scan details twice for the same scan
    Given database initially contains scan details with scan ID: imw92pqz and body: "{abc:123}"
    When API client calls the `create scan details` API with scan ID: imw92pqz and body: "{def:987}"
    Then response status is 400
    And database "contains" scan details with scan ID: imw92pqz and body: "{abc:123}"

  Scenario: getting existing scan details
    Given database initially contains scan details with scan ID: ozpd3jan4 and body: "{abc:123}"
    When API client calls the `get scan details` API with scan ID: ozpd3jan4
    Then response status is 200
    And response contains "body" field set to "{abc:123}"

  Scenario: trying to get details for a non-existent scan
    Given database initially contains scan details with scan ID: scan-id-1 and body: "{abc:123}"
    When API client calls the `get scan details` API with scan ID: scan-id-2
    Then response status is 404

