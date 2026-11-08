package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.List;
import java.util.Random;

/**
 * Unit conversion, time, perimeter, area and volume questions (PRD §9). Random draws match
 * the original generator exactly (R-DOM-04). {@code diff} is lower-case.
 */
final class MeasurementQuestions {

    private MeasurementQuestions() {
    }

    static Question measurement(String diff, Random rng) {
        if (diff.equals("easy")) {
            int subType = rng.nextInt(5);
            int val;
            if (subType == 0) {
                val = rng.nextInt(19) + 2;
                return times("Convert " + val + " meters to centimeters", val, 100, Skill.MEAS_CONVERT_LENGTH);
            } else if (subType == 1) {
                val = rng.nextInt(49) + 2;
                return times("Convert " + val + " centimeters to millimeters", val, 10, Skill.MEAS_CONVERT_LENGTH);
            } else if (subType == 2) {
                val = rng.nextInt(9) + 1;
                return times("Convert " + val + " kilometers to meters", val, 1000, Skill.MEAS_CONVERT_LENGTH);
            } else if (subType == 3) {
                val = rng.nextInt(9) + 1;
                return times("Convert " + val + " kilograms to grams", val, 1000, Skill.MEAS_CONVERT_MASS);
            } else {
                val = rng.nextInt(9) + 1;
                return times("Convert " + val + " liters to milliliters", val, 1000, Skill.MEAS_CONVERT_CAPACITY);
            }
        } else if (diff.equals("medium")) {
            int subType = rng.nextInt(5);
            if (subType == 0) {
                int val = rng.nextInt(7) + 2;
                return times("Convert " + val + " hours to minutes", val, 60, Skill.TIME_CONVERT);
            } else if (subType == 1) {
                int h = rng.nextInt(4) + 1;
                int m = rng.nextInt(51) + 5;
                SolutionStep minutes = SolutionStep.of('*', h, 60);
                return new Question("How many minutes are in " + h + " hours and " + m + " minutes?", h * 60 + m,
                        Skill.TIME_CONVERT_COMPOUND, List.of(minutes, SolutionStep.of('+', minutes.result(), m)));
            } else if (subType == 2) {
                int l = rng.nextInt(18) + 3;
                int w = rng.nextInt(18) + 3;
                SolutionStep half = SolutionStep.of('+', l, w);
                return new Question("Find the perimeter of a rectangle with length " + l + " and width " + w,
                        2 * (l + w), Skill.GEO_PERIMETER_RECT, List.of(half, SolutionStep.of('*', half.result(), 2)));
            } else if (subType == 3) {
                int s = rng.nextInt(13) + 3;
                return times("Find the area of a square with side length " + s, s, s, Skill.GEO_AREA_SQUARE);
            } else {
                int l = rng.nextInt(13) + 3;
                int w = rng.nextInt(13) + 3;
                return times("Find the area of a rectangle with length " + l + " and width " + w, l, w,
                        Skill.GEO_AREA_RECT);
            }
        } else {
            int subType = rng.nextInt(5);
            if (subType == 0) {
                int m = rng.nextInt(14) + 2;
                int cm = rng.nextInt(91) + 5;
                SolutionStep metres = SolutionStep.of('*', m, 100);
                return new Question("Convert " + m + " m and " + cm + " cm to centimeters", m * 100 + cm,
                        Skill.MEAS_CONVERT_LENGTH_COMPOUND, List.of(metres, SolutionStep.of('+', metres.result(), cm)));
            } else if (subType == 1) {
                int l = rng.nextInt(5) + 1;
                int ml = rng.nextInt(801) + 100;
                SolutionStep litres = SolutionStep.of('*', l, 1000);
                return new Question("A bottle has " + ml + " mL of water and another has " + l + " L. Total mL = ?",
                        ml + l * 1000, Skill.MEAS_CONVERT_CAPACITY_COMPOUND,
                        List.of(litres, SolutionStep.of('+', ml, litres.result())));
            } else if (subType == 2) {
                int s = rng.nextInt(21) + 5;
                int p = 4 * s;
                SolutionStep side = SolutionStep.of('/', p, 4);
                return new Question("A square garden has a perimeter of " + p + " meters. What is its area in square meters?",
                        s * s, Skill.GEO_AREA_FROM_PERIMETER,
                        List.of(side, SolutionStep.of('*', side.result(), side.result())));
            } else if (subType == 3) {
                int w = rng.nextInt(13) + 3;
                int l = w + rng.nextInt(13) + 1;
                int a = l * w;
                SolutionStep width = SolutionStep.of('/', a, l);
                SolutionStep half = SolutionStep.of('+', l, width.result());
                return new Question("A rectangular field has an area of " + a + " square meters and a length of " + l
                        + " meters. What is its perimeter in meters?", 2 * (l + w), Skill.GEO_PERIMETER_FROM_AREA,
                        List.of(width, half, SolutionStep.of('*', half.result(), 2)));
            } else {
                int l = rng.nextInt(7) + 2;
                int w = rng.nextInt(5) + 2;
                int h = rng.nextInt(4) + 2;
                SolutionStep base = SolutionStep.of('*', l, w);
                return new Question("Find the volume (in cubic cm) of a box with length " + l + " cm, width " + w
                        + " cm, and height " + h + " cm", l * w * h, Skill.GEO_VOLUME_PRISM,
                        List.of(base, SolutionStep.of('*', base.result(), h)));
            }
        }
    }

    /** A one-step "value × factor" question. */
    private static Question times(String expression, int value, int factor, Skill skill) {
        return new Question(expression, value * factor, skill, List.of(SolutionStep.of('*', value, factor)));
    }
}
