package com.checkmarx.integrations.datastore.api.repo_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class RepoResponseValidationSteps {
    private final ApiTestState testState;
    private final ObjectMapper objectMapper;

    @Then("the response is an empty array")
    public void responseIsEmptyArray() {
        validateResponseAsArray(0);
    }

    @Then("the response is an array of {int} objects with the following fields:")
    public void responseIsArrayWithFields(int expectedItemCount, List<Map<String, String>> expectedResponseItems) throws JsonProcessingException {
        ArrayNode responseJson = validateResponseAsArray(expectedItemCount);
        List<JsonNode> sortedResponseRepos = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(responseJson.elements(), Spliterator.ORDERED), false)
                .sorted(Comparator.comparing(repo -> repo.get("repo_identity").textValue()))
                .collect(Collectors.toList());

        List<Map<String, String>> sortedExpectedRepos = expectedResponseItems.stream()
                .sorted(Comparator.comparing(repo -> repo.get("repo_identity")))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedExpectedRepos.size(); i++) {
            Map<String, String> expected = sortedExpectedRepos.get(i);

            JsonNode actualJson = sortedResponseRepos.get(i);
            validateFieldsMatch(expected, actualJson);
        }
    }

    @Then("response contains the following fields:")
    public void responseContainsFields(List<Map<String, String>> expectedFieldValues) throws JsonProcessingException {
        JsonNode responseJson = testState.getResponseBodyOrThrow();
        validateFieldsMatch(expectedFieldValues.get(0), responseJson);
    }

    private static Map<String, Object> toTypedRepoMap(Map<String, String> rawRepo) {
        final String BOOLEAN_FIELD = "is_webhook_configured";

        // We need to convert a field value from string to boolean.
        // Otherwise comparing rawRepo to response JSON will fail.
        Map<String, Object> result = new HashMap<>(rawRepo);
        boolean boolValue = Boolean.parseBoolean(rawRepo.get(BOOLEAN_FIELD));
        result.put(BOOLEAN_FIELD, boolValue);
        return result;
    }

    private void validateFieldsMatch(Map<String, String> rawExpectedRepo, JsonNode actualRepoJson)
            throws JsonProcessingException {
        Map<String, Object> expected = toTypedRepoMap(rawExpectedRepo);
        Map<?, ?> actual = objectMapper.treeToValue(actualRepoJson, Map.class);
        assertEquals("Repo from response has unexpected fields", expected, actual);
    }

    private ArrayNode validateResponseAsArray(int expectedItemCount) {
        JsonNode responseJson = testState.getResponseBodyOrThrow();
        assertEquals("Response is not a JSON array.", JsonNodeType.ARRAY, responseJson.getNodeType());
        assertEquals("Wrong item count in response.", expectedItemCount, responseJson.size());
        return (ArrayNode) responseJson;
    }
}