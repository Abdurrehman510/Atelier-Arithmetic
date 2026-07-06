package com.mathquiz.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Lightweight JSON read/write helper that operates without any external libraries.
 * Handles the fixed schema used by SessionRepository — not a general JSON parser.
 *
 * Output format is valid, human-readable JSON that can be opened in any text editor.
 */
public class JsonHelper {

    private JsonHelper() {}

    // -------------------------------------------------------------------------
    // File I/O
    // -------------------------------------------------------------------------

    public static void writeFile(String path, String json) {
        try {
            Files.write(Paths.get(path), json.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {}
    }

    public static String readFile(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "[]";
        }
    }

    // -------------------------------------------------------------------------
    // Building JSON strings (write path)
    // -------------------------------------------------------------------------

    public static String buildSessionArray(List<Map<String, Object>> sessions) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < sessions.size(); i++) {
            sb.append(buildObject(sessions.get(i), "  "));
            if (i < sessions.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String buildObject(Map<String, Object> map, String indent) {
        StringBuilder sb = new StringBuilder(indent).append("{\n");
        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object val = map.get(key);
            sb.append(indent).append("  ").append("\"").append(key).append("\": ");
            sb.append(valueToJson(val, indent + "  "));
            if (i < keys.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(indent).append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String valueToJson(Object val, String indent) {
        if (val == null)                    return "null";
        if (val instanceof String)          return "\"" + escapeString((String) val) + "\"";
        if (val instanceof Boolean)         return val.toString();
        if (val instanceof Number)          return val.toString();
        if (val instanceof List) {
            List<?> list = (List<?>) val;
            if (list.isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Map) {
                    sb.append(buildObject((Map<String, Object>) item, indent + "  "));
                } else {
                    sb.append(indent).append("  ").append(valueToJson(item, indent + "  "));
                }
                if (i < list.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(indent).append("]");
            return sb.toString();
        }
        return "\"" + escapeString(val.toString()) + "\"";
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // -------------------------------------------------------------------------
    // Parsing JSON strings (read path)
    // -------------------------------------------------------------------------

    /**
     * Extracts the string value of a JSON key from a JSON object fragment.
     * e.g. extractString("{\"name\": \"Alice\"}", "name") → "Alice"
     */
    public static String extractString(String json, String key) {
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start < 0) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end < 0 ? "" : json.substring(start, end);
    }

    /**
     * Extracts an integer value of a JSON key.
     */
    public static int extractInt(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start < 0) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        try { return Integer.parseInt(json.substring(start, end)); } catch (NumberFormatException e) { return 0; }
    }

    /**
     * Extracts a long value of a JSON key.
     */
    public static long extractLong(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start < 0) return 0L;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        try { return Long.parseLong(json.substring(start, end)); } catch (NumberFormatException e) { return 0L; }
    }

    /**
     * Extracts a double value of a JSON key.
     */
    public static double extractDouble(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start < 0) return 0.0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.')) end++;
        try { return Double.parseDouble(json.substring(start, end)); } catch (NumberFormatException e) { return 0.0; }
    }

    /**
     * Extracts a boolean value of a JSON key.
     */
    public static boolean extractBoolean(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start < 0) return false;
        start += search.length();
        return json.startsWith("true", start);
    }

    /**
     * Splits a JSON array string into a list of individual object strings.
     * Handles nested braces correctly.
     */
    public static List<String> splitArray(String json) {
        List<String> items = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    items.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return items;
    }
}
