package com.mathquiz.service;

import java.util.List;
import java.util.Map;

/**
 * Manual-check helper (test-only): prints the newest session's question rows as
 * {@code index skillId difficulty hintsUsed}. Run it with the same {@code -Duser.home} the app used.
 */
public final class DbPeek {

    private DbPeek() {
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<Map<String, Object>> sessions = new SessionRepository().loadRaw();
        if (sessions.isEmpty()) {
            System.out.println("no sessions saved for the active profile");
            return;
        }
        Map<String, Object> newest = sessions.get(0);
        System.out.println("session " + newest.get("timestamp") + " " + newest.get("category"));
        List<Map<String, Object>> questions = (List<Map<String, Object>>) newest.get("questions");
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            System.out.println((i + 1) + " " + q.get("skillId") + " " + q.get("difficulty") + " " + q.get("hintsUsed"));
        }
    }
}
