package com.mathquiz.service;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * A math-based challenge dialog that acts as a parental lock gate to block
 * children from resetting database history, modifying developer configuration,
 * or entering custom builder screens.
 */
public class ParentalGate {

    private static final String[] WORDS = {
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"
    };

    /**
     * Shows a modal input challenge to verify parental presence.
     * Returns true if verified, false otherwise.
     */
    public static boolean verifyParent(Component parentComponent) {
        Random rand = new Random();
        int a = 3 + rand.nextInt(10); // Factor between 3 and 12
        int b = 3 + rand.nextInt(10); // Factor between 3 and 12
        int expected = a * b;

        String phraseA = numberToWord(a);
        String phraseB = numberToWord(b);

        String challenge = String.format(
            "This screen is for parents or teachers only.\n\n" +
            "Please solve this math puzzle to proceed:\n" +
            "\u2605 What is %s times %s? \u2605",
            phraseA, phraseB
        );

        String input = JOptionPane.showInputDialog(
            parentComponent,
            challenge,
            "Parent / Teacher Lock 🦉",
            JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return false; // Cancelled
        }

        try {
            int answer = Integer.parseInt(input.trim());
            if (answer == expected) {
                return true;
            }
        } catch (NumberFormatException ignored) {}

        JOptionPane.showMessageDialog(
            parentComponent,
            "Incorrect answer. Verification failed! Access restricted.",
            "Lock Active",
            JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    private static String numberToWord(int n) {
        if (n >= 0 && n < WORDS.length) {
            return WORDS[n];
        }
        return String.valueOf(n);
    }
}
