package com.mathquiz.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** BUG-10: the custom-quiz writer and reader must be symmetrical (rules.md R-DAT-09). */
class JsonHelperTest {

    private static String arrayOf(String expression, int answer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("expression", expression);
        map.put("answer", answer);
        return JsonHelper.buildSessionArray(List.of(map));
    }

    @Test
    void quotesAndBackslashesRoundTrip() {
        String expr = "say \"hi\" \\ then 2 + 2";
        List<String> items = JsonHelper.splitArray(arrayOf(expr, 4));
        assertEquals(1, items.size());
        assertEquals(expr, JsonHelper.extractString(items.get(0), "expression"));
        assertEquals(4, JsonHelper.extractInt(items.get(0), "answer"));
    }

    @Test
    void bracesInsideStringsDoNotSplitObjects() {
        String expr = "{3 + 4} * 2 }{";
        List<String> items = JsonHelper.splitArray(arrayOf(expr, 14));
        assertEquals(1, items.size());
        assertEquals(expr, JsonHelper.extractString(items.get(0), "expression"));
    }

    @Test
    void escapedControlCharactersRoundTrip() {
        String expr = "line1\nline2\ttab";
        assertEquals(expr, JsonHelper.extractString(JsonHelper.splitArray(arrayOf(expr, 1)).get(0), "expression"));
    }
}
