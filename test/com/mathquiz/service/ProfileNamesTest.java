package com.mathquiz.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-08: "Spark Mascot Name" only suggests names the profile dialog accepts. */
class ProfileNamesTest {

    @Test
    void sparkedNamesAreAlwaysValid() {
        Random rng = new Random(42);
        Set<String> distinct = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            String name = ProfileNames.spark(rng);
            assertNull(ProfileNames.validate(name), () -> "rejected suggestion: " + name);
            distinct.add(name);
        }
        assertTrue(distinct.size() >= 100, "suggestions should stay varied, got " + distinct.size());
    }

    @Test
    void validationKeepsTheExistingRulesAndMessages() {
        assertEquals("Profile name cannot be empty!", ProfileNames.validate(""));
        assertEquals("Profile name contains invalid characters!", ProfileNames.validate("Bad!"));
        assertEquals("Profile name must be 15 characters or less!", ProfileNames.validate("Sixteen Letters!".replace("!", "s")));
        assertNull(ProfileNames.validate("Happy Owl"));
        assertNull(ProfileNames.validate("Fifteen_Chars_1"));
    }
}
