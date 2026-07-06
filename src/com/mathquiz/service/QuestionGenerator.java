package com.mathquiz.service;

import com.mathquiz.model.Question;
import java.util.Random;

/**
 * Service to dynamically generate arithmetic questions based on selected difficulty
 * (Easy, Medium, Hard) and category (Addition, Difference, Multiplication, Division, Mixed, Special).
 */
public class QuestionGenerator {
    private final Random random = new Random();

    public Question generate(String difficulty, String category) {
        String diffLower = difficulty.toLowerCase();
        String catLower = category.toLowerCase();

        // If Mixed is selected, randomly choose one of the core categories (excluding Special)
        if (catLower.equals("mixed") || catLower.equals("mix")) {
            String[] choices = {"addition", "difference", "multiplication", "division"};
            catLower = choices[random.nextInt(choices.length)];
        }

        switch (catLower) {
            case "addition":
                return generateAddition(diffLower);
            case "difference":
                return generateDifference(diffLower);
            case "multiplication":
                return generateMultiplication(diffLower);
            case "division":
                return generateDivision(diffLower);
            case "special":
                return generateSpecial(diffLower);
            default:
                return generateAddition(diffLower);
        }
    }

    private Question generateAddition(String diff) {
        int a, b, c, d;
        if (diff.equals("easy")) {
            a = random.nextInt(90) + 10;
            b = random.nextInt(90) + 10;
            return new Question(a + " + " + b, a + b);
        } else if (diff.equals("medium")) {
            a = random.nextInt(900) + 100;
            b = random.nextInt(900) + 100;
            c = random.nextInt(900) + 100;
            return new Question(a + " + " + b + " + " + c, a + b + c);
        } else {
            a = random.nextInt(9000) + 1000;
            b = random.nextInt(9000) + 1000;
            c = random.nextInt(900) + 100;
            d = random.nextInt(900) + 100;
            return new Question(a + " + " + b + " + " + c + " + " + d, a + b + c + d);
        }
    }

    private Question generateDifference(String diff) {
        int a, b, c, d;
        if (diff.equals("easy")) {
            a = random.nextInt(90) + 10;
            b = random.nextInt(a - 5) + 5; // Ensures positive result
            return new Question(a + " - " + b, a - b);
        } else if (diff.equals("medium")) {
            a = random.nextInt(900) + 100;
            b = random.nextInt(a / 2) + 10;
            c = random.nextInt(a - b - 10) + 5;
            return new Question(a + " - " + b + " - " + c, a - b - c);
        } else {
            a = random.nextInt(9000) + 1000;
            b = random.nextInt(a / 3) + 100;
            c = random.nextInt(a / 3) + 50;
            d = random.nextInt(a - b - c - 50) + 10;
            return new Question(a + " - " + b + " - " + c + " - " + d, a - b - c - d);
        }
    }

    private Question generateMultiplication(String diff) {
        int a, b, c;
        if (diff.equals("easy")) {
            a = random.nextInt(89) + 10;
            b = random.nextInt(9) + 2;
            return new Question(a + " * " + b, a * b);
        } else if (diff.equals("medium")) {
            a = random.nextInt(89) + 10;
            b = random.nextInt(8) + 2;
            c = random.nextInt(4) + 2;
            return new Question(a + " * " + b + " * " + c, a * b * c);
        } else {
            a = random.nextInt(899) + 100;
            b = random.nextInt(8) + 2;
            c = random.nextInt(8) + 2;
            return new Question(a + " * " + b + " * " + c, a * b * c);
        }
    }

    private Question generateDivision(String diff) {
        int dividend, divisor, extra;
        if (diff.equals("easy")) {
            // easy perfect division
            divisor = random.nextInt(12) + 2;
            int quotient = random.nextInt(15) + 2;
            dividend = divisor * quotient;
            return new Question(dividend + " / " + divisor, quotient);
        } else if (diff.equals("medium")) {
            // medium perfect double division: (A / B) / C
            extra = random.nextInt(8) + 2;
            int q2 = random.nextInt(10) + 2;
            int middle = extra * q2;
            divisor = random.nextInt(8) + 2;
            dividend = middle * divisor;
            return new Question("(" + dividend + " / " + divisor + ") / " + extra, q2);
        } else {
            // hard perfect division with larger scale
            divisor = random.nextInt(48) + 3;
            int quotient = random.nextInt(90) + 10;
            dividend = divisor * quotient;
            return new Question(dividend + " / " + divisor, quotient);
        }
    }

    private Question generateSpecial(String diff) {
        // Compound expressions direct from original code logic
        int type = random.nextInt(5);
        int scale = diff.equals("easy") ? 10 : (diff.equals("medium") ? 50 : 150);

        switch (type) {
            case 0: { // (A + B) * (C - D)
                int a = random.nextInt(scale) + 3;
                int b = random.nextInt(scale) + 3;
                int c = random.nextInt(scale) + 10;
                int d = random.nextInt(c - 2) + 1;
                return new Question("(" + a + " + " + b + ") * (" + c + " - " + d + ")", (a + b) * (c - d));
            }
            case 1: { // (A - B) / (C + D) - perfect division
                int denominator = random.nextInt(9) + 2;
                int quotient = random.nextInt(12) + 2;
                int numerator = denominator * quotient;
                int a = numerator + random.nextInt(scale) + 5;
                int b = a - numerator;
                int c = denominator / 2 + 1;
                int d = denominator - c;
                return new Question("(" + a + " - " + b + ") / (" + c + " + " + d + ")", quotient);
            }
            case 2: { // (A / B) + (C / D) - perfect division sum
                int b = random.nextInt(8) + 2;
                int q1 = random.nextInt(10) + 2;
                int a = b * q1;

                int d = random.nextInt(8) + 2;
                int q2 = random.nextInt(10) + 2;
                int c = d * q2;

                return new Question("(" + a + " / " + b + ") + (" + c + " / " + d + ")", q1 + q2);
            }
            case 3: { // (A * B) / (C * D) - perfect division
                int c = random.nextInt(5) + 2;
                int d = random.nextInt(5) + 2;
                int denom = c * d;
                int quotient = random.nextInt(8) + 2;
                int numer = denom * quotient;
                
                // factors of numer: a and b
                int a = c * quotient;
                int b = d;
                return new Question("(" + a + " * " + b + ") / (" + c + " * " + d + ")", quotient);
            }
            case 4:
            default: { // (A / B) - (C * D)
                int b = random.nextInt(8) + 2;
                int q1 = random.nextInt(15) + 10;
                int a = b * q1;

                int d = random.nextInt(4) + 1;
                int c = random.nextInt(q1 / d - 1) + 1;
                
                return new Question("(" + a + " / " + b + ") - (" + c + " * " + d + ")", q1 - (c * d));
            }
        }
    }
}
