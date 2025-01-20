package com.xxx.test.api.nrt.apinrt.campaignExecutor.checker;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class JsonChecker {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> errors = new ArrayList<>();
    private final TextChecker textChecker;
    private boolean ignoreMessages = false;

    public JsonChecker(TextChecker textChecker) {
        this.textChecker = textChecker;
    }

    public boolean matches(String expectedJson, String actualJson) {

        final var expectedMap = toMap(expectedJson);
        final var actuelMap = toMap(actualJson);

        var result =  compareNodes(expectedMap, actuelMap, ".");
        System.out.println(String.join("\n   ", errors));
        return  result;
    }

    private Map<String, Object> toMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean compareNodes(Object expectedNode, Object actualNode, String path) {

        if (isObject(expectedNode)) {
            if (!isObject(actualNode)) {
                addError(path, "The expected value is a JSON object, but not the current one.");
                return false;
            }
            return compareObjects(expectedNode, actualNode, path);
        } else if (isArray(expectedNode)) {
            if (!isArray(actualNode)) {
                addError(path, "The expected value is a JSON array, but not the current one.");
                return false;
            }
            return compareArrays(expectedNode, actualNode, path);
        }else if (isSimpleValue(expectedNode)) {
            if (!isSimpleValue(actualNode)) {
                return false;
            }
            return compareSimpleValues(expectedNode, actualNode, path);
        }
        throw new RuntimeException("not implemented");
    }

    private boolean compareObjects(Object expected, Object current, String path) {
        Map<String, Object> expectedMap = asMap(expected);
        Map<String, Object> currentMap = asMap(current);

        TreeSet<String> keys = new TreeSet<>(expectedMap.keySet());
        keys.addAll(currentMap.keySet());

        for (String key : keys) {
            if (!compareKeyConsistancy(expectedMap, currentMap, key, path)) {
                return false;
            }
            if (isUnexpectedKeyDetected(expectedMap, currentMap, path)) {
                return false;
            }
            if (!compareNodes(expectedMap.get(key), currentMap.get(key), path+"/"+key)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareArrays(Object expected, Object current, String path) {
        List<Object> expectedList = asList(expected);
        List<Object> currentListCopy = new ArrayList<>(asList(current));
        List<Integer> currentPositionsFound = new ArrayList<>();

        for (int i = 0; i < expectedList.size(); i++) {
            Object expectedItem = expectedList.get(i);
            boolean matchFound = false;
            matchFound = isMatchFound(path, currentListCopy, currentPositionsFound, expectedItem, i, matchFound);
            if (!matchFound) {
                addError(path+"["+i+"]", "Unable to find a matching element in the current array for the element (i starts at position 0, it is the positions in the expected array).");
                return false;
            }
        }
        return checkNoUnexpectedElements(currentPositionsFound, asList(current), path);
    }

    private boolean isMatchFound(String path, List<Object> currentListCopy, List<Integer> currentPositionsFound, Object expectedItem, int i, boolean matchFound) {
        this.ignoreMessages=true;
        for (int j = 0; j < currentListCopy.size(); j++) {
            Object currentItem = currentListCopy.get(j);
            if (!currentPositionsFound.contains(j) && compareNodes(expectedItem, currentItem, path +"["+ i +"]")) {
                currentPositionsFound.add(j);
                matchFound = true;
                break;
            }
        }
        this.ignoreMessages=false;
        return matchFound;
    }

    private boolean checkNoUnexpectedElements(List<Integer> currentPositionsFound, List<Object> currentList, String path) {
        if (currentPositionsFound.size() != currentList.size()) {
            ArrayList<String> missingPositions = new ArrayList<>();
            for (int i = 0; i < currentList.size(); i++) {
                if (!currentPositionsFound.contains(i)) {
                    missingPositions.add(i+"");
                }
            }
            addError(path, "Some elements of the current array doesn't match with any expected one (positions, starting at 0 :"+String.join(",", missingPositions)+")");
            return false;
        }
        return true;
    }

    private boolean isUnexpectedKeyDetected(Map<String, Object> expectedMap, Map<String, Object> currentMap, String path) {
        List<String> unexpectedKeys = new ArrayList<>();
        for (String key: currentMap.keySet()) {
           if (currentMap.get(key) != null && (!expectedMap.containsKey(key) || expectedMap.get(key) == null)) {
               unexpectedKeys.add(key);
           }
        }
        if (!unexpectedKeys.isEmpty()) {
            addError(path, "Unexpected field(s) detected in the current object: " + String.join(", ", unexpectedKeys));
        }
        return !unexpectedKeys.isEmpty();
    }

    private boolean compareSimpleValues(Object expected, Object current, String path) {
        if (expected == null) {
            if (current != null) {
                addError(path, "unexpected value: "+ expected+". Null/absent was expected");
            }
            return current == null;
        } else {
            boolean isMatching = compareValues(expected, current);
            if (!isMatching) {
                addError(path, "the found value: "+ current+" doesn't match with the expected one: "+expected+".");
            }
            return isMatching;
        }
    }

    private boolean compareValues(Object expectedValue, Object currentValue) {
        if (isString(expectedValue) && isString(currentValue)) {
            return textChecker.matches((String)expectedValue, (String)currentValue);
        } else {
            return expectedValue.equals(currentValue);
        }
    }

    private boolean isString(Object object) {
        return object.getClass().equals(String.class);
    }


    private Map<String, Object> asMap(Object expected) {
        if (isObject(expected)) {
            return (Map<String, Object>) expected;
        } else {
            throw new RuntimeException("Not a JSON object: unable to convert to a map.");
        }
    }

    private List<Object> asList(Object expected) {
        if (isArray(expected)) {
            return (List<Object>) expected;
        } else {
            throw new RuntimeException("Not a JSON array: unable to convert to a list.");
        }
    }

    private boolean isObject(Object map) {
        if (null == map) {
            return false;
        }
        return Map.class.isAssignableFrom(map.getClass());
    }

    private boolean isArray(Object map) {
        if (null == map) {
            return false;
        }
        return List.class.isAssignableFrom(map.getClass());
    }

    private boolean isSimpleValue(Object map) {
        return !isObject(map) && !isArray(map);
    }

    private boolean compareKeyConsistancy(Map<String, Object> expectedMap, Map<String, Object> actualMap, String key, String path) {

        if (isConsistantKey(expectedMap, key) && !isConsistantKey(actualMap, key)) {
            addError(path, "The expected field (with not null value) named "+ key + " doesn't exist or is null in the current object.");
        }
        if (!isConsistantKey(expectedMap, key) && isConsistantKey(actualMap, key)) {
            addError(path, "An unexpected field (with not null value) named "+ key + " is found in the current object.");
        }
        return isConsistantKey(expectedMap, key) == isConsistantKey(actualMap, key);
    }

    private boolean isConsistantKey(Map<String, Object> map, String key) {
        return map.get(key)!=null;
    }

    private void addError(String path, String message) {
        if (!ignoreMessages) {
            errors.add(path + ": " + message);
        }
    }
}