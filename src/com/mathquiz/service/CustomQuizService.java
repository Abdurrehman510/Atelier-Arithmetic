package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.Question;
import com.mathquiz.util.JsonHelper;

import java.io.File;
import java.util.*;

/**
 * Service to manage custom quiz parent-authored question sets.
 * Stores quizzes under the custom_quizzes/ folder in the app home directory.
 */
public class CustomQuizService {

    private final String customQuizzesDir;

    public CustomQuizService() {
        this.customQuizzesDir = AppConfig.getAppDir() + File.separator + "custom_quizzes";
        File dir = new File(customQuizzesDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /** Saves a list of questions to a custom quiz JSON file. */
    public boolean saveQuiz(String quizName, List<Question> questions) {
        if (quizName == null || quizName.trim().isEmpty() || questions.isEmpty()) {
            return false;
        }
        String sanitized = sanitizeFilename(quizName.trim());
        File file = new File(customQuizzesDir, sanitized + ".json");

        // Format as JSON array
        List<Map<String, Object>> list = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("expression", q.getExpression());
            map.put("answer", q.getCorrectAnswer());
            list.add(map);
        }
        String json = JsonHelper.buildSessionArray(list);
        JsonHelper.writeFile(file.getAbsolutePath(), json);
        return true;
    }

    /** Loads and parses a custom quiz from a JSON file. */
    public List<Question> loadQuiz(String quizName) {
        String sanitized = sanitizeFilename(quizName);
        File file = new File(customQuizzesDir, sanitized + ".json");
        if (!file.exists()) return Collections.emptyList();

        String json = JsonHelper.readFile(file.getAbsolutePath());
        List<Question> questions = new ArrayList<>();
        List<String> blocks = JsonHelper.splitArray(json);
        for (String b : blocks) {
            String expr = JsonHelper.extractString(b, "expression");
            int ans = JsonHelper.extractInt(b, "answer");
            if (!expr.trim().isEmpty()) {
                questions.add(new Question(expr, ans));
            }
        }
        return questions;
    }

    /** Returns names of all available custom quizzes. */
    public List<String> getAvailableQuizzes() {
        File dir = new File(customQuizzesDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        List<String> list = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                list.add(name.substring(0, name.length() - 5)); // remove ".json"
            }
        }
        Collections.sort(list);
        return list;
    }

    private String sanitizeFilename(String s) {
        return s.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
