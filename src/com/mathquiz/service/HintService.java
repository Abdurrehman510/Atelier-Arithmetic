package com.mathquiz.service;

import com.mathquiz.model.Question;

import java.util.regex.Pattern;

/**
 * Dynamic math helper that generates step-by-step, child-friendly hints (scaffolded learning).
 *
 * <p>Generated questions are routed by their {@link com.mathquiz.model.Skill} (BUG-07: routing by
 * wording sent "area <b>of</b> a square" to the percentage hint). Quiz Builder questions have no
 * generator skill and are routed by their wording, using exact fraction/percentage patterns.</p>
 */
public class HintService {

    private static final Pattern PERCENT_OF = Pattern.compile(".*\\d\\s*%\\s*of\\b.*");
    private static final Pattern FRACTION_OF = Pattern.compile(".*\\d+\\s*/\\s*\\d+\\s+of\\b.*");

    private static final String PERCENT_HINT = "<html><body>"
            + "<b>Calculating Percentages:</b><br>"
            + "Divide the total value by <b>100</b>, then multiply it by the percent amount!"
            + "</body></html>";
    private static final String FRACTION_OF_HINT = "<html><body>"
            + "<b>Fraction of a number:</b><br>"
            + "1. Divide the total by the <b>bottom number</b> (denominator) to find one part.<br>"
            + "2. Multiply by the <b>top number</b> (numerator)!"
            + "</body></html>";
    private static final String FRACTION_PRODUCT_HINT = "<html><body>"
            + "<b>Two fractions of a number:</b><br>"
            + "1. Multiply the two bottom numbers, and the two top numbers.<br>"
            + "2. Divide the total by the new <b>bottom number</b>, then multiply by the new top number!"
            + "</body></html>";

    /** The hint for a question, chosen by the skill it practises. */
    public static String generateHint(Question question) {
        if (question == null) {
            return generateHint((String) null);
        }
        String exp = question.getExpression() == null ? "" : question.getExpression().trim();
        switch (question.getSkill()) {
            case ADD_2DIGIT_2TERMS: case ADD_3DIGIT_3TERMS: case ADD_4DIGIT_4TERMS:
                return generateAdditionHint(exp);
            case SUB_2DIGIT_2TERMS: case SUB_3DIGIT_3TERMS: case SUB_4DIGIT_4TERMS:
                return generateSubtractionHint(exp);
            case MUL_2DIGIT_BY_1DIGIT: case MUL_2DIGIT_3FACTORS: case MUL_3DIGIT_3FACTORS:
                return generateMultiplicationHint(exp);
            case DIV_BASIC: case DIV_2DIGIT_QUOTIENT:
                return generateDivisionHint(exp);
            case DIV_CHAINED:
            case ORD_SUM_TIMES_DIFF: case ORD_DIFF_OVER_SUM: case ORD_QUOTIENT_SUM:
            case ORD_PRODUCT_OVER_PRODUCT: case ORD_QUOTIENT_MINUS_PRODUCT:
                return generateSpecialHint(exp);
            case FRAC_OF_QUANTITY:
                return FRACTION_OF_HINT;
            case FRAC_PRODUCT_OF_QUANTITY:
                return FRACTION_PRODUCT_HINT;
            case FRAC_MIXED_TO_IMPROPER:
                return hint("Mixed number to improper fraction",
                        "Multiply the <b>whole number</b> by the bottom number, then add the top number!");
            case FRAC_SIMPLIFY:
                return hint("Simplifying",
                        "Find the biggest number that <b>divides both</b> the top and the bottom, then divide the bottom number by it!");
            case PCT_OF_QUANTITY:
                return PERCENT_HINT;
            case RATIO_MISSING_PART:
                return hint("Using a ratio",
                        "Divide the red marbles by the red part of the <b>ratio</b> to find one group, then multiply by the blue part!");
            case PAT_ARITHMETIC: case PAT_ALTERNATING_STEPS: case PAT_GROWING_STEP: case PAT_GEOMETRIC:
            case PAT_ALTERNATING_OPS: case PAT_FIBONACCI: case PAT_SQUARES_OFFSET: case PAT_INTERLEAVED:
            case PAT_POLYNOMIAL:
                return patternHint();
            case ALG_ONE_STEP_ADD: case ALG_ONE_STEP_SUB: case ALG_ONE_STEP_SUB_FROM: case ALG_ONE_STEP_MUL:
            case ALG_TWO_STEP: case ALG_TWO_STEP_SUB_FROM: case ALG_TWO_STEP_DIV: case ALG_VARS_BOTH_SIDES:
                return algebraHint();
            case ALG_SIMULTANEOUS_SUM_DIFF: case ALG_SIMULTANEOUS_ELIMINATION:
                return hint("Two equations",
                        "Add or subtract the two <b>equations</b> so that y disappears. Then you can find x!");
            case MEAS_CONVERT_LENGTH:
                return lengthConversionHint(exp);
            case MEAS_CONVERT_MASS:
                return hint("Unit fact", "Remember that <b>1 kilogram = 1000 grams</b>! Multiply the kilograms by 1000.");
            case MEAS_CONVERT_CAPACITY:
                return hint("Unit fact", "Remember that <b>1 liter = 1000 milliliters</b>! Multiply the liters by 1000.");
            case TIME_CONVERT:
                return hint("Unit fact", "Remember that <b>1 hour = 60 minutes</b>! Multiply the hours by 60.");
            case TIME_CONVERT_COMPOUND:
                return hint("Hours and minutes", "Turn the hours into minutes (× <b>60</b>), then add the extra minutes!");
            case GEO_PERIMETER_RECT:
                return hint("Perimeter", "Perimeter is the distance all the way <b>around</b>: add the length and the width, then double it!");
            case GEO_AREA_SQUARE:
                return hint("Area of a square", "Area = <b>side × side</b>.");
            case GEO_AREA_RECT:
                return hint("Area of a rectangle", "Area = <b>length × width</b>.");
            case MEAS_CONVERT_LENGTH_COMPOUND:
                return hint("Meters and centimeters", "Turn the meters into centimeters (× <b>100</b>), then add the extra centimeters!");
            case MEAS_CONVERT_CAPACITY_COMPOUND:
                return hint("Liters and milliliters", "Turn the liters into milliliters (× <b>1000</b>), then add the other bottle!");
            case GEO_AREA_FROM_PERIMETER:
                return hint("Square garden", "A square has <b>4 equal sides</b>: divide the perimeter by 4 to get one side, then multiply side × side!");
            case GEO_PERIMETER_FROM_AREA:
                return hint("Rectangle field", "Find the width first: <b>area ÷ length</b>. Then add length + width and double it!");
            case GEO_VOLUME_PRISM:
                return hint("Volume of a box", "Volume = length × width × <b>height</b>.");
            case CUSTOM:
            default:
                return generateHint(exp);
        }
    }

    /** Wording-based hint, used for Quiz Builder (custom) questions. */
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

        // 2. Only real "p% of" / "a/b of" wording gets the percentage / fraction hint (BUG-07)
        if (PERCENT_OF.matcher(expression).matches()) {
            return PERCENT_HINT;
        }
        if (FRACTION_OF.matcher(expression).matches()) {
            return FRACTION_OF_HINT;
        }
        if (expression.startsWith("Pattern:")) {
            return patternHint();
        }
        if (expression.startsWith("Solve for x:")) {
            return algebraHint();
        }
        if (expression.startsWith("Convert")) {
            if (expression.contains("meters to centimeters")) {
                return lengthConversionHint(expression);
            } else if (expression.contains("hours to minutes")) {
                return hint("Unit fact", "Remember that <b>1 hour = 60 minutes</b>! Multiply the hours by 60.");
            } else if (expression.contains("liters to milliliters")) {
                return hint("Unit fact", "Remember that <b>1 liter = 1000 milliliters</b>! Multiply the liters by 1000.");
            }
        }

        // 3. Simple expression (A op B)
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

    private static String hint(String title, String body) {
        return "<html><body><b>" + title + ":</b><br>" + body + "</body></html>";
    }

    private static String patternHint() {
        return "<html><body><b>Hint:</b> Check how much the numbers increase or decrease between each step to find the hidden rule!</body></html>";
    }

    private static String algebraHint() {
        return "<html><body><b>Hint:</b> Try to isolate x! Perform the opposite operation on both sides of the equation to balance it.</body></html>";
    }

    private static String lengthConversionHint(String exp) {
        if (exp.contains("centimeters to millimeters")) {
            return hint("Unit fact", "Remember that <b>1 centimeter = 10 millimeters</b>! Multiply the centimeters by 10.");
        }
        if (exp.contains("kilometers to meters")) {
            return hint("Unit fact", "Remember that <b>1 kilometer = 1000 meters</b>! Multiply the kilometers by 1000.");
        }
        return hint("Unit fact", "Remember that <b>1 meter = 100 centimeters</b>! Multiply the meters by 100.");
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
            } catch (NumberFormatException e) {
                // not a plain "A + B": fall through to the general hint
            }
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
            } catch (NumberFormatException e) {
                // not a plain "A - B": fall through to the general hint
            }
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
            } catch (NumberFormatException e) {
                // not a plain "A * B": fall through to the general hint
            }
        }
        return "<html><body><b>Hint:</b> Break one of the numbers into tens and ones, multiply each part, and add the answers together!</body></html>";
    }

    private static String generateDivisionHint(String exp) {
        String[] parts = exp.split("/");
        if (parts.length == 2) {
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (b != 0 && a % b == 0) {
                    return "<html><body>"
                            + "<b>Think Reverse Multiplication:</b><br>"
                            + "What number times <b>" + b + "</b> equals <b>" + a + "</b>?<br>"
                            + "Solve: <b>" + b + " × ? = " + a + "</b><br>"
                            + "(Hint: <b>" + b + " × " + (a / b) + " = " + a + "</b>)"
                            + "</body></html>";
                }
            } catch (NumberFormatException e) {
                // not a plain "A / B": fall through to the general hint
            }
        }
        return "<html><body><b>Hint:</b> Division is sharing equally. Think of how many times the second number fits into the first!</body></html>";
    }
}
