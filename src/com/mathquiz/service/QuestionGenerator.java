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
            // Find 1/2, 1/3, 1/4, or 1/5 of a matching integer
            int type = random.nextInt(4);
            int denom, answer;
            if (type == 0) {
                denom = 2;
                answer = random.nextInt(25) + 3;
            } else if (type == 1) {
                denom = 3;
                answer = random.nextInt(15) + 2;
            } else if (type == 2) {
                denom = 4;
                answer = random.nextInt(12) + 2;
            } else {
                denom = 5;
                answer = random.nextInt(10) + 2;
            }
            int val = denom * answer;
            return new Question("1/" + denom + " of " + val, answer);
        } else if (diff.equals("medium")) {
            // Find 2/3, 3/4, 2/5, 3/5, or 4/5 of X
            int type = random.nextInt(5);
            int numer, denom, multiplier;
            if (type == 0) {
                numer = 2; denom = 3; multiplier = random.nextInt(12) + 2;
            } else if (type == 1) {
                numer = 3; denom = 4; multiplier = random.nextInt(10) + 2;
            } else if (type == 2) {
                numer = 2; denom = 5; multiplier = random.nextInt(8) + 2;
            } else if (type == 3) {
                numer = 3; denom = 5; multiplier = random.nextInt(8) + 2;
            } else {
                numer = 4; denom = 5; multiplier = random.nextInt(8) + 2;
            }
            int val = denom * multiplier;
            return new Question(numer + "/" + denom + " of " + val, numer * multiplier);
        } else {
            // Percentages or Ratios
            boolean isPercent = random.nextBoolean();
            if (isPercent) {
                int[] pcts = {10, 20, 25, 30, 40, 50, 60, 75, 80, 90};
                int pct = pcts[random.nextInt(pcts.length)];
                int multiplier;
                if (pct == 25 || pct == 75) {
                    multiplier = (random.nextInt(10) + 1) * 4; // Multiple of 4
                } else if (pct == 50) {
                    multiplier = (random.nextInt(20) + 1) * 2; // Multiple of 2
                } else {
                    multiplier = (random.nextInt(10) + 1) * 10; // Multiple of 10
                }
                int val = multiplier * 10;
                int answer = (val * pct) / 100;
                return new Question(pct + "% of " + val, answer);
            } else {
                // Ratios: A:B ratio. If A is X, what is B?
                int[][] ratios = {{1, 2}, {1, 3}, {2, 3}, {3, 4}, {3, 5}};
                int[] selected = ratios[random.nextInt(ratios.length)];
                int rA = selected[0];
                int rB = selected[1];
                int factor = random.nextInt(10) + 2;
                int valA = rA * factor;
                int valB = rB * factor;
                return new Question("Ratio is " + rA + ":" + rB + ". If left is " + valA + ", what is right?", valB);
            }
        }
    }

    private Question generatePatterns(String diff) {
        if (diff.equals("easy")) {
            // Arithmetic sequence: positive or negative step
            int start = random.nextInt(20) + 5;
            int step = random.nextInt(6) + 2;
            boolean isPositive = random.nextBoolean();
            if (!isPositive && start - 4 * step > 0) {
                int s1 = start;
                int s2 = start - step;
                int s3 = start - 2 * step;
                int s4 = start - 3 * step;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 - step);
            } else {
                int s1 = start;
                int s2 = start + step;
                int s3 = start + 2 * step;
                int s4 = start + 3 * step;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 + step);
            }
        } else if (diff.equals("medium")) {
            // Geometric or progressive increment sequence
            boolean isGeometric = random.nextBoolean();
            if (isGeometric) {
                int start = random.nextInt(4) + 1;
                int ratio = random.nextInt(3) + 2;
                int s1 = start;
                int s2 = start * ratio;
                int s3 = start * ratio * ratio;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", _", s3 * ratio);
            } else {
                // +1, +2, +3, +4...
                int start = random.nextInt(10) + 1;
                int s1 = start;
                int s2 = start + 1;
                int s3 = start + 1 + 2;
                int s4 = start + 1 + 2 + 3;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 + 4);
            }
        } else {
            // Squares or cubes or Fibonacci sequence
            int type = random.nextInt(3);
            if (type == 0) {
                // Squares
                int offset = random.nextInt(6) + 1;
                int s1 = offset * offset;
                int s2 = (offset + 1) * (offset + 1);
                int s3 = (offset + 2) * (offset + 2);
                int s4 = (offset + 3) * (offset + 3);
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", (offset + 4) * (offset + 4));
            } else if (type == 1) {
                // Cubes
                int offset = random.nextInt(4) + 1;
                int s1 = offset * offset * offset;
                int s2 = (offset + 1) * (offset + 1) * (offset + 1);
                int s3 = (offset + 2) * (offset + 2) * (offset + 2);
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", _", (offset + 3) * (offset + 3) * (offset + 3));
            } else {
                // Fibonacci sequence
                int start = random.nextInt(5) + 1;
                int f1 = start;
                int f2 = start;
                int f3 = f1 + f2;
                int f4 = f2 + f3;
                int f5 = f3 + f4;
                int f6 = f4 + f5;
                return new Question("Pattern: " + f1 + ", " + f2 + ", " + f3 + ", " + f4 + ", " + f5 + ", " + f6 + ", _", f5 + f6);
            }
        }
    }

    private Question generateAlgebra(String diff) {
        if (diff.equals("easy")) {
            // x + A = B, x - A = B, or A - x = B
            int x = random.nextInt(15) + 2;
            int a = random.nextInt(20) + 2;
            int type = random.nextInt(3);
            if (type == 0) {
                return new Question("Solve for x: x + " + a + " = " + (x + a), x);
            } else if (type == 1) {
                return new Question("Solve for x: x - " + a + " = " + (x - a), x);
            } else {
                int sum = x + a;
                return new Question("Solve for x: " + sum + " - x = " + a, x);
            }
        } else if (diff.equals("medium")) {
            // Ax + B = C, Ax - B = C, or B - Ax = C
            int x = random.nextInt(10) + 2;
            int a = random.nextInt(6) + 2;
            int b = random.nextInt(15) + 1;
            int type = random.nextInt(3);
            if (type == 0) {
                return new Question("Solve for x: " + a + "x + " + b + " = " + (a * x + b), x);
            } else if (type == 1) {
                return new Question("Solve for x: " + a + "x - " + b + " = " + (a * x - b), x);
            } else {
                int start = a * x + b;
                return new Question("Solve for x: " + start + " - " + a + "x = " + b, x);
            }
        } else {
            // Ax + B = Cx + D, Ax - B = Cx + D, Ax + B = Cx - D, Ax - B = Cx - D
            int x = random.nextInt(6) + 2;
            int c = random.nextInt(4) + 2;
            int a = c + random.nextInt(4) + 1; // a > c
            int d = random.nextInt(20) + 15;
            int type = random.nextInt(4);
            if (type == 0) { // Ax + B = Cx + D
                int b = (c - a) * x + d;
                if (b > 0) return new Question("Solve for x: " + a + "x + " + b + " = " + c + "x + " + d, x);
            } else if (type == 1) { // Ax - B = Cx + D -> Ax - (Cx+D) = B -> (a-c)x - d = B
                int b = (a - c) * x - d;
                if (b > 0) return new Question("Solve for x: " + a + "x - " + b + " = " + c + "x + " + d, x);
            } else if (type == 2) { // Ax + B = Cx - D -> (c-a)x - d = B -> no, B = (c-a)x - d
                int b = (c - a) * x - d;
                if (b > 0) return new Question("Solve for x: " + a + "x + " + b + " = " + c + "x - " + d, x);
            } else { // Ax - B = Cx - D -> (a-c)x + d = B
                int b = (a - c) * x + d;
                if (b > 0) return new Question("Solve for x: " + a + "x - " + b + " = " + c + "x - " + d, x);
            }
            // fallback
            return new Question("Solve for x: 5x + 3 = 3x + 15", 6);
        }
    }

    private Question generateMeasurement(String diff) {
        if (diff.equals("easy")) {
            // Length conversions: m to cm, cm to mm, km to m
            int type = random.nextInt(3);
            int val = random.nextInt(15) + 2;
            if (type == 0) {
                return new Question("Convert " + val + " meters to centimeters", val * 100);
            } else if (type == 1) {
                return new Question("Convert " + val + " centimeters to millimeters", val * 10);
            } else {
                return new Question("Convert " + val + " kilometers to meters", val * 1000);
            }
        } else if (diff.equals("medium")) {
            // Time conversions: hours to mins, mins to secs, days to hours, weeks to days
            int type = random.nextInt(4);
            int val = random.nextInt(6) + 2;
            if (type == 0) {
                return new Question("Convert " + val + " hours to minutes", val * 60);
            } else if (type == 1) {
                return new Question("Convert " + val + " minutes to seconds", val * 60);
            } else if (type == 2) {
                return new Question("Convert " + val + " days to hours", val * 24);
            } else {
                return new Question("Convert " + val + " weeks to days", val * 7);
            }
        } else {
            // Mass & volume: L to mL, kg to g, g to mg
            int type = random.nextInt(3);
            int val = random.nextInt(10) + 2;
            if (type == 0) {
                return new Question("Convert " + val + " liters to milliliters", val * 1000);
            } else if (type == 1) {
                return new Question("Convert " + val + " kilograms to grams", val * 1000);
            } else {
                return new Question("Convert " + val + " grams to milligrams", val * 1000);
            }
        }
    }
}
