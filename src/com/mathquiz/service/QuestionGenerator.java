package com.mathquiz.service;

import com.mathquiz.model.Question;
import java.util.Random;

/**
 * Service to dynamically generate arithmetic questions based on selected difficulty
 * (Easy, Medium, Hard) and category (Addition, Difference, Multiplication, Division, Mixed, Special).
 */
public class QuestionGenerator {
    private Random random = new Random();

    public Question generateSeeded(String difficulty, String category, long dateSeed, int questionIndex) {
        Random oldRandom = this.random;
        this.random = new Random(dateSeed * 100 + questionIndex);
        try {
            return generate(difficulty, category);
        } finally {
            this.random = oldRandom;
        }
    }

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
            case "fractions":
                return generateFractions(diffLower);
            case "patterns":
                return generatePatterns(diffLower);
            case "algebra":
                return generateAlgebra(diffLower);
            case "measurement":
                return generateMeasurement(diffLower);
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

    private Question generateFractions(String diff) {
        if (diff.equals("easy")) {
            // Find 1/2 of an even integer
            int val = (random.nextInt(30) + 5) * 2;
            return new Question("1/2 of " + val, val / 2);
        } else if (diff.equals("medium")) {
            // Find 3/4 of a multiple of 4
            int val = (random.nextInt(20) + 3) * 4;
            return new Question("3/4 of " + val, (val / 4) * 3);
        } else {
            // Find 20% or 30% of a multiple of 10
            int pctChoice = random.nextBoolean() ? 20 : 30;
            int val = (random.nextInt(25) + 4) * 10;
            int answer = (val * pctChoice) / 100;
            return new Question(pctChoice + "% of " + val, answer);
        }
    }

    private Question generatePatterns(String diff) {
        if (diff.equals("easy")) {
            // Arithmetic sequence
            int start = random.nextInt(15) + 2;
            int diffVal = random.nextInt(8) + 2;
            int s1 = start;
            int s2 = start + diffVal;
            int s3 = start + 2 * diffVal;
            int s4 = start + 3 * diffVal;
            return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 + diffVal);
        } else if (diff.equals("medium")) {
            // Geometric sequence
            int start = random.nextInt(5) + 2;
            int ratio = random.nextInt(3) + 2;
            int s1 = start;
            int s2 = start * ratio;
            int s3 = start * ratio * ratio;
            return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", _", s3 * ratio);
        } else {
            // Square numbers pattern
            int offset = random.nextInt(5) + 1;
            int s1 = (offset) * (offset);
            int s2 = (offset + 1) * (offset + 1);
            int s3 = (offset + 2) * (offset + 2);
            int s4 = (offset + 3) * (offset + 3);
            int next = (offset + 4) * (offset + 4);
            return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", next);
        }
    }

    private Question generateAlgebra(String diff) {
        if (diff.equals("easy")) {
            // x + A = B
            int x = random.nextInt(15) + 2;
            int a = random.nextInt(25) + 2;
            return new Question("Solve for x: x + " + a + " = " + (x + a), x);
        } else if (diff.equals("medium")) {
            // Ax - B = C
            int x = random.nextInt(10) + 2;
            int a = random.nextInt(6) + 2;
            int b = random.nextInt(12) + 1;
            int c = a * x - b;
            return new Question("Solve for x: " + a + "x - " + b + " = " + c, x);
        } else {
            // Ax + B = Cx + D
            int x = random.nextInt(8) + 2;
            int c = random.nextInt(5) + 2;
            int a = c + random.nextInt(4) + 1; // a > c
            int d = random.nextInt(20) + 15;
            int b = d - (a - c) * x;
            if (b <= 0) {
                // fallback to simpler hard equation
                return new Question("Solve for x: 5x + 3 = 3x + 15", 6);
            }
            return new Question("Solve for x: " + a + "x + " + b + " = " + c + "x + " + d, x);
        }
    }

    private Question generateMeasurement(String diff) {
        if (diff.equals("easy")) {
            // m to cm
            int meters = random.nextInt(12) + 2;
            return new Question("Convert " + meters + " meters to centimeters", meters * 100);
        } else if (diff.equals("medium")) {
            // hours to minutes
            int hours = random.nextInt(5) + 2;
            return new Question("Convert " + hours + " hours to minutes", hours * 60);
        } else {
            // liters to milliliters
            int liters = random.nextInt(9) + 2;
            return new Question("Convert " + liters + " liters to milliliters", liters * 1000);
        }
    }
}
