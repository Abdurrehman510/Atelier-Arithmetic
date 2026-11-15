package com.mathquiz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Profile-name rules and the "Spark Mascot Name" suggestion (PRD FR-PRF-05). One source for the
 * length limit, so a suggestion can never be rejected by the dialog (BUG-08).
 */
public final class ProfileNames {

    /** Longest profile name the dialog accepts. */
    public static final int MAX_LENGTH = 15;

    private static final String[] ADJECTIVES = {"Happy", "Clever", "Swift", "Bright", "Merry", "Kind",
            "Calm", "Brave", "Shiny", "Friendly", "Loyal", "Sparkly"};
    private static final String[] ANIMALS = {"Owl", "Panda", "Cheetah", "Squirrel", "Dolphin", "Penguin",
            "Tiger", "Koala", "Otter", "Badger", "Fox", "Falcon"};

    /** Every "Adjective Animal" pair that fits within {@link #MAX_LENGTH}. */
    private static final List<String> SUGGESTIONS;

    static {
        List<String> fitting = new ArrayList<>();
        for (String adjective : ADJECTIVES) {
            for (String animal : ANIMALS) {
                String name = adjective + " " + animal;
                if (name.length() <= MAX_LENGTH) {
                    fitting.add(name);
                }
            }
        }
        SUGGESTIONS = Collections.unmodifiableList(fitting);
    }

    private ProfileNames() {
    }

    /** A random friendly "Adjective Animal" name that always passes {@link #validate(String)}. */
    public static String spark(Random rng) {
        return SUGGESTIONS.get(rng.nextInt(SUGGESTIONS.size()));
    }

    /**
     * Checks a (trimmed) profile name.
     *
     * @return {@code null} when the name is acceptable, otherwise the message to show
     */
    public static String validate(String name) {
        if (name == null || name.isEmpty()) {
            return "Profile name cannot be empty!";
        }
        if (!name.matches("[a-zA-Z0-9_ ]+")) {
            return "Profile name contains invalid characters!";
        }
        if (name.length() > MAX_LENGTH) {
            return "Profile name must be 15 characters or less!";
        }
        return null;
    }
}
