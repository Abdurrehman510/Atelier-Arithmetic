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
                return ArithmeticQuestions.addition(diffLower, random);
            case "difference":
                return ArithmeticQuestions.difference(diffLower, random);
            case "multiplication":
                return ArithmeticQuestions.multiplication(diffLower, random);
            case "division":
                return ArithmeticQuestions.division(diffLower, random);
            case "special":
                return OrderOfOperationsQuestions.special(diffLower, random);
            case "fractions":
                return FractionQuestions.fractions(diffLower, random);
            case "patterns":
                return generatePatterns(diffLower);
            case "algebra":
                return generateAlgebra(diffLower);
            case "measurement":
                return generateMeasurement(diffLower);
            default:
                return ArithmeticQuestions.addition(diffLower, random);
        }
    }

    private Question generatePatterns(String diff) {
        if (diff.equals("easy")) {
            int subType = random.nextInt(2);
            if (subType == 0) {
                // Arithmetic sequence with step in [-10, 10] excluding 0
                int start = random.nextInt(30) + 10;
                int step = random.nextInt(9) + 2; // [2, 10]
                if (random.nextBoolean()) step = -step;
                // prevent negative terms
                if (step < 0 && start + 4 * step < 2) {
                    start = start - 4 * step + 5;
                }
                int s1 = start;
                int s2 = start + step;
                int s3 = start + 2 * step;
                int s4 = start + 3 * step;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 + step);
            } else {
                // Alternating step pattern: +a, -b, +a, -b... where a > b >= 1
                int start = random.nextInt(20) + 5;
                int a = random.nextInt(7) + 3; // [3, 9]
                int b = random.nextInt(a - 2) + 1; // [1, a-2]
                int s1 = start;
                int s2 = start + a;
                int s3 = s2 - b;
                int s4 = s3 + a;
                int s5 = s4 - b;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", s5 + a);
            }
        } else if (diff.equals("medium")) {
            int subType = random.nextInt(3);
            if (subType == 0) {
                // Progressive step pattern: step size increases by a constant k on each term
                int start = random.nextInt(15) + 5;
                int d = random.nextInt(5) + 1; // Initial step
                int k = random.nextInt(3) + 1; // Increment per step
                int s1 = start;
                int s2 = s1 + d;
                int s3 = s2 + (d + k);
                int s4 = s3 + (d + 2 * k);
                int s5 = s4 + (d + 3 * k);
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", s5 + (d + 4 * k));
            } else if (subType == 1) {
                // Geometric progression: start * r^n where r in [2, 4], start in [1, 5]
                int start = random.nextInt(5) + 1;
                int r = random.nextInt(3) + 2; // [2, 4]
                int s1 = start;
                int s2 = start * r;
                int s3 = s2 * r;
                int s4 = s3 * r;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", _", s4 * r);
            } else {
                // Alternating operations: e.g. *r, +k, *r, +k...
                int start = random.nextInt(4) + 2; // [2, 5]
                int r = 2;
                int k = random.nextInt(5) + 2; // [2, 6]
                int s1 = start;
                int s2 = start * r;
                int s3 = s2 + k;
                int s4 = s3 * r;
                int s5 = s4 + k;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", s5 * r);
            }
        } else {
            int subType = random.nextInt(4);
            if (subType == 0) {
                // Fibonacci-like sequence: s1, s2, s1+s2, s2+(s1+s2)...
                int s1 = random.nextInt(9) + 1; // [1, 9]
                int s2 = random.nextInt(9) + 1; // [1, 9]
                int s3 = s1 + s2;
                int s4 = s2 + s3;
                int s5 = s3 + s4;
                int s6 = s4 + s5;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", " + s6 + ", _", s5 + s6);
            } else if (subType == 1) {
                // Squared pattern with offsets: n^2 + c or n^2 - c
                int c = random.nextInt(6) + 1; // [1, 6]
                boolean sign = random.nextBoolean();
                int s1 = 1 * 1 + (sign ? c : -c);
                int s2 = 2 * 2 + (sign ? c : -c);
                int s3 = 3 * 3 + (sign ? c : -c);
                int s4 = 4 * 4 + (sign ? c : -c);
                int s5 = 5 * 5 + (sign ? c : -c);
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", 6 * 6 + (sign ? c : -c));
            } else if (subType == 2) {
                // Alternating double sequence: A, B, A+d, B+d, A+2d, B+2d...
                int a = random.nextInt(10) + 5;
                int b = random.nextInt(20) + 20;
                int d = random.nextInt(5) + 2;
                int s1 = a;
                int s2 = b;
                int s3 = a + d;
                int s4 = b + d;
                int s5 = a + 2 * d;
                int s6 = b + 2 * d;
                return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", " + s6 + ", _", s5 + d);
            } else {
                // Polynomial sequence: n*(n+1) or n^2 - n
                boolean isMul = random.nextBoolean();
                if (isMul) {
                    int s1 = 1 * 2;
                    int s2 = 2 * 3;
                    int s3 = 3 * 4;
                    int s4 = 4 * 5;
                    int s5 = 5 * 6;
                    return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", 6 * 7);
                } else {
                    int s1 = 1 * 1 - 1;
                    int s2 = 2 * 2 - 2;
                    int s3 = 3 * 3 - 3;
                    int s4 = 4 * 4 - 4;
                    int s5 = 5 * 5 - 5;
                    return new Question("Pattern: " + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ", " + s5 + ", _", 6 * 6 - 6);
                }
            }
        }
    }

    private Question generateAlgebra(String diff) {
        if (diff.equals("easy")) {
            int subType = random.nextInt(4);
            if (subType == 0) {
                // x + A = B
                int x = random.nextInt(14) + 2; // [2, 15]
                int a = random.nextInt(19) + 2; // [2, 20]
                return new Question("Solve for x: x + " + a + " = " + (x + a), x);
            } else if (subType == 1) {
                // x - A = B
                int x = random.nextInt(14) + 2;
                int a = random.nextInt(19) + 2;
                return new Question("Solve for x: x - " + a + " = " + (x - a), x);
            } else if (subType == 2) {
                // A - x = B
                int x = random.nextInt(14) + 2;
                int a = x + random.nextInt(19) + 2; // ensures B > 0
                return new Question("Solve for x: " + a + " - x = " + (a - x), x);
            } else {
                // Ax = B
                int x = random.nextInt(11) + 2; // [2, 12]
                int a = random.nextInt(9) + 2;  // [2, 10]
                return new Question("Solve for x: " + a + "x = " + (a * x), x);
            }
        } else if (diff.equals("medium")) {
            int subType = random.nextInt(4);
            if (subType == 0) {
                // Ax + B = C
                int x = random.nextInt(9) + 2;  // [2, 10]
                int a = random.nextInt(7) + 2;  // [2, 8]
                int b = random.nextInt(15) + 1; // [1, 15]
                return new Question("Solve for x: " + a + "x + " + b + " = " + (a * x + b), x);
            } else if (subType == 1) {
                // Ax - B = C
                int x = random.nextInt(9) + 2;
                int a = random.nextInt(7) + 2;
                int b = random.nextInt(15) + 1;
                return new Question("Solve for x: " + a + "x - " + b + " = " + (a * x - b), x);
            } else if (subType == 2) {
                // B - Ax = C
                int x = random.nextInt(9) + 2;
                int a = random.nextInt(7) + 2;
                int c = random.nextInt(15) + 1; // B = Ax + C
                return new Question("Solve for x: " + (a * x + c) + " - " + a + "x = " + c, x);
            } else {
                // (x + B) / A = C -> x = A*C - B
                int x = random.nextInt(14) + 2; // [2, 15]
                int a = random.nextInt(5) + 2;  // [2, 6]
                int c = random.nextInt(7) + 2;  // [2, 8]
                // B must be positive and less than A*C
                int b = a * c - x;
                while (b <= 0) {
                    c++;
                    b = a * c - x;
                }
                return new Question("Solve for x: (x + " + b + ") / " + a + " = " + c, x);
            }
        } else {
            int subType = random.nextInt(4);
            if (subType == 0) {
                // Ax + B = Cx + D -> (A-C)x = D-B
                int x = random.nextInt(7) + 2; // [2, 8]
                int c = random.nextInt(5) + 2; // [2, 6]
                int a = c + random.nextInt(5) + 1; // A > C
                int b = random.nextInt(20) + 1;
                int d = b + (a - c) * x;
                return new Question("Solve for x: " + a + "x + " + b + " = " + c + "x + " + d, x);
            } else if (subType == 1) {
                // Ax - B = Cx + D -> (A-C)x = D+B
                int x = random.nextInt(7) + 2;
                int c = random.nextInt(5) + 2;
                int a = c + random.nextInt(5) + 1;
                int d = random.nextInt(20) + 1;
                int b = (a - c) * x - d;
                while (b <= 0) {
                    x++;
                    b = (a - c) * x - d;
                }
                return new Question("Solve for x: " + a + "x - " + b + " = " + c + "x + " + d, x);
            } else if (subType == 2) {
                // Simultaneous linear equations: x + y = A and x - y = B
                int x = random.nextInt(16) + 5; // [5, 20]
                int y = random.nextInt(x - 2) + 1;
                int sum = x + y;
                int difference = x - y;
                return new Question("If x + y = " + sum + " and x - y = " + difference + ", what is x?", x);
            } else {
                // Simultaneous: x + y = A and 2x + y = B
                int x = random.nextInt(13) + 3; // [3, 15]
                int y = random.nextInt(15) + 1; // [1, 15]
                int sum = x + y;
                int doubleSum = 2 * x + y;
                return new Question("If x + y = " + sum + " and 2x + y = " + doubleSum + ", what is x?", x);
            }
        }
    }

    private Question generateMeasurement(String diff) {
        if (diff.equals("easy")) {
            int subType = random.nextInt(5);
            int val;
            if (subType == 0) {
                val = random.nextInt(19) + 2; // [2, 20]
                return new Question("Convert " + val + " meters to centimeters", val * 100);
            } else if (subType == 1) {
                val = random.nextInt(49) + 2; // [2, 50]
                return new Question("Convert " + val + " centimeters to millimeters", val * 10);
            } else if (subType == 2) {
                val = random.nextInt(9) + 1;  // [1, 9]
                return new Question("Convert " + val + " kilometers to meters", val * 1000);
            } else if (subType == 3) {
                val = random.nextInt(9) + 1;
                return new Question("Convert " + val + " kilograms to grams", val * 1000);
            } else {
                val = random.nextInt(9) + 1;
                return new Question("Convert " + val + " liters to milliliters", val * 1000);
            }
        } else if (diff.equals("medium")) {
            int subType = random.nextInt(5);
            if (subType == 0) {
                int val = random.nextInt(7) + 2; // [2, 8]
                return new Question("Convert " + val + " hours to minutes", val * 60);
            } else if (subType == 1) {
                int h = random.nextInt(4) + 1; // [1, 4]
                int m = random.nextInt(51) + 5; // [5, 55]
                return new Question("How many minutes are in " + h + " hours and " + m + " minutes?", h * 60 + m);
            } else if (subType == 2) {
                // Perimeter of rectangle
                int l = random.nextInt(18) + 3; // [3, 20]
                int w = random.nextInt(18) + 3;
                return new Question("Find the perimeter of a rectangle with length " + l + " and width " + w, 2 * (l + w));
            } else if (subType == 3) {
                // Area of a square
                int s = random.nextInt(13) + 3; // [3, 15]
                return new Question("Find the area of a square with side length " + s, s * s);
            } else {
                // Area of rectangle
                int l = random.nextInt(13) + 3; // [3, 15]
                int w = random.nextInt(13) + 3;
                return new Question("Find the area of a rectangle with length " + l + " and width " + w, l * w);
            }
        } else {
            int subType = random.nextInt(5);
            if (subType == 0) {
                int m = random.nextInt(14) + 2; // [2, 15]
                int cm = random.nextInt(91) + 5; // [5, 95]
                return new Question("Convert " + m + " m and " + cm + " cm to centimeters", m * 100 + cm);
            } else if (subType == 1) {
                int l = random.nextInt(5) + 1; // [1, 5]
                int ml = random.nextInt(801) + 100; // [100, 900]
                return new Question("A bottle has " + ml + " mL of water and another has " + l + " L. Total mL = ?", ml + l * 1000);
            } else if (subType == 2) {
                // Square perimeter P -> Find area (P must be multiple of 4)
                int s = random.nextInt(21) + 5; // side [5, 25]
                int p = 4 * s;
                return new Question("A square garden has a perimeter of " + p + " meters. What is its area in square meters?", s * s);
            } else if (subType == 3) {
                // Rectangle area A and length L -> Find perimeter
                int w = random.nextInt(13) + 3; // [3, 15]
                int l = w + random.nextInt(13) + 1; // length > width
                int a = l * w;
                return new Question("A rectangular field has an area of " + a + " square meters and a length of " + l + " meters. What is its perimeter in meters?", 2 * (l + w));
            } else {
                // Volume of rectangular prism
                int l = random.nextInt(7) + 2; // [2, 8]
                int w = random.nextInt(5) + 2; // [2, 6]
                int h = random.nextInt(4) + 2; // [2, 5]
                return new Question("Find the volume (in cubic cm) of a box with length " + l + " cm, width " + w + " cm, and height " + h + " cm", l * w * h);
            }
        }
    }
}
