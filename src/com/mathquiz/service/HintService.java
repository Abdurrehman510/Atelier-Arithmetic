package com.mathquiz.service;

/**
 * Dynamic math helper that parses math expressions and generates step-by-step
 * child-friendly hints (scaffolded learning).
 *
 * Phase 4 — Advanced Features.
 */
public class HintService {

    public static String generateHint(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return "Read the expression carefully and try your best!";
        }

        expression = expression.trim();

        // 1. Bracket expressions (Special Category)
        if (expression.startsWith("(") && expression.contains(")") && 
            (expression.contains("+") || expression.contains("-") || expression.contains("*") || expression.contains("/"))) {
            return generateSpecialHint(expression);
        }

        // New Categories Routing
        if (expression.contains("of")) {
            return generateFractionHint(expression);
        }
        if (expression.startsWith("Pattern:")) {
            return "<html><body><b>Hint:</b> Check how much the numbers increase or decrease between each step to find the hidden rule!</body></html>";
        }
        if (expression.startsWith("Solve for x:")) {
            return "<html><body><b>Hint:</b> Try to isolate x! Perform the opposite operation on both sides of the equation to balance it.</body></html>";
        }
        if (expression.startsWith("Convert")) {
            if (expression.contains("meters to centimeters")) {
                return "<html><body><b>Hint:</b> Remember that 1 meter = 100 centimeters! Multiply the meters by 100.</body></html>";
            } else if (expression.contains("hours to minutes")) {
                return "<html><body><b>Hint:</b> Remember that 1 hour = 60 minutes! Multiply the hours by 60.</body></html>";
            } else if (expression.contains("liters to milliliters")) {
                return "<html><body><b>Hint:</b> Remember that 1 liter = 1000 milliliters! Multiply the liters by 1000.</body></html>";
            }
        }

        // 2. Simple expression (A op B)
        if (expression.contains("+")) {
            return generateAdditionHint(expression);
        } else if (expression.contains("-")) {
            return generateSubtractionHint(expression);
        } else if (expression.contains("*") || expression.contains("×")) {
            return generateMultiplicationHint(expression);
        } else if (expression.contains("/")) {
            return generateDivisionHint(expression);
        }

        return "Break the numbers into parts (tens, units) to solve step by step!";
    }

    private static String generateSpecialHint(String exp) {
        String op = " ";
        int splitIdx = -1;
        String[] operators = {"*", "+", "-", "/"};
        String[] displayOps = {"×", "+", "−", "÷"};
        for (int i = 0; i < operators.length; i++) {
            int idx = exp.indexOf(") " + operators[i] + " (");
            if (idx >= 0) {
                op = displayOps[i];
                splitIdx = idx;
                break;
            }
        }

        if (splitIdx < 0) {
            return "<html><body><b>Hint:</b> Solve the calculations inside the brackets <b>first</b>, then perform the operation in the middle!</body></html>";
        }

        String leftPart = exp.substring(1, splitIdx).trim(); // strip leading '('
        String rightPart = exp.substring(splitIdx + 5, exp.length() - 1).trim(); // strip trailing ')'

        return "<html><body>"
                + "<b>Follow BODMAS Rules:</b><br>"
                + "1. Solve left bracket: <b>" + leftPart + "</b><br>"
                + "2. Solve right bracket: <b>" + rightPart + "</b><br>"
                + "3. Combine the two results using the middle operator: <b>" + op + "</b>!"
                + "</body></html>";
    }

    private static String generateAdditionHint(String exp) {
        String[] parts = exp.split("\\+");
        if (parts.length == 2) {
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (a >= 10 && b >= 10 && a < 100 && b < 100) {
                    int aTen = (a / 10) * 10;
                    int aOne = a % 10;
                    int bTen = (b / 10) * 10;
                    int bOne = b % 10;
                    int tenSum = aTen + bTen;
                    int oneSum = aOne + bOne;
                    return "<html><body>"
                            + "<b>Partitioning Addition:</b><br>"
                            + "1. Add tens: <b>" + aTen + " + " + bTen + " = " + tenSum + "</b><br>"
                            + "2. Add units: <b>" + aOne + " + " + bOne + " = " + oneSum + "</b><br>"
                            + "3. Combine them: <b>" + tenSum + " + " + oneSum + " = " + (tenSum + oneSum) + "</b>!"
                            + "</body></html>";
                }
            } catch (Exception e) {}
        }
        return "<html><body><b>Hint:</b> Try adding number by number. Start with the ones columns, then carry over to tens and hundreds!</body></html>";
    }

    private static String generateSubtractionHint(String exp) {
        String[] parts = exp.split("-");
        if (parts.length == 2) {
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (a >= 10 && b >= 10 && a < 100 && b < 100) {
                    int bTen = (b / 10) * 10;
                    int bOne = b % 10;
                    int step1 = a - bTen;
                    return "<html><body>"
                            + "<b>Subtract in Steps:</b><br>"
                            + "1. First, subtract the tens: <b>" + a + " − " + bTen + " = " + step1 + "</b><br>"
                            + "2. Next, subtract the ones: <b>" + step1 + " − " + bOne + " = " + (step1 - bOne) + "</b>!"
                            + "</body></html>";
                }
            } catch (Exception e) {}
        }
        return "<html><body><b>Hint:</b> Align units under units. If a digit is too small to subtract, borrow from the left column!</body></html>";
    }

    private static String generateMultiplicationHint(String exp) {
        String[] parts = exp.split("[*×]");
        if (parts.length == 2) {
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (a >= 10 && a < 100 && b < 10) {
                    int aTen = (a / 10) * 10;
                    int aOne = a % 10;
                    int p1 = aTen * b;
                    int p2 = aOne * b;
                    return "<html><body>"
                            + "<b>Break Multiplication Down:</b><br>"
                            + "1. Multiply tens: <b>" + aTen + " × " + b + " = " + p1 + "</b><br>"
                            + "2. Multiply ones: <b>" + aOne + " × " + b + " = " + p2 + "</b><br>"
                            + "3. Add results: <b>" + p1 + " + " + p2 + " = " + (p1 + p2) + "</b>!"
                            + "</body></html>";
                }
            } catch (Exception e) {}
        }
        return "<html><body><b>Hint:</b> Break one of the numbers into tens and ones, multiply each part, and add the answers together!</body></html>";
    }

    private static String generateDivisionHint(String exp) {
        String[] parts = exp.split("/");
        if (parts.length == 2) {
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (a % b == 0) {
                    return "<html><body>"
                            + "<b>Think Reverse Multiplication:</b><br>"
                            + "What number times <b>" + b + "</b> equals <b>" + a + "</b>?<br>"
                            + "Solve: <b>" + b + " × ? = " + a + "</b><br>"
                            + "(Hint: <b>" + b + " × " + (a / b) + " = " + a + "</b>)"
                            + "</body></html>";
                }
            } catch (Exception e) {}
        }
        return "<html><body><b>Hint:</b> Division is sharing equally. Think of how many times the second number fits into the first!</body></html>";
    }

    private static String generateFractionHint(String exp) {
        if (exp.contains("1/2")) {
            return "<html><body>"
                    + "<b>Finding Half (1/2):</b><br>"
                    + "Divide the number by <b>2</b> to find exactly half!"
                    + "</body></html>";
        } else if (exp.contains("3/4")) {
            return "<html><body>"
                    + "<b>Finding Three-Quarters (3/4):</b><br>"
                    + "1. Divide by <b>4</b> to find one quarter.<br>"
                    + "2. Multiply that result by <b>3</b>!"
                    + "</body></html>";
        } else {
            return "<html><body>"
                    + "<b>Calculating Percentages:</b><br>"
                    + "Divide the total value by <b>100</b>, then multiply it by the percent amount!"
                    + "</body></html>";
        }
    }
}
