package com.mathquiz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Catalog of micro-skills practised by generated questions (premium roadmap A1, initial set):
 * one skill per existing generator sub-type, plus {@link #CUSTOM} for Quiz Builder questions.
 *
 * <p>{@link #id()} values are persisted in the database. Never rename, reuse or remove an ID;
 * only add new constants (rules.md R-DOM-12). {@link #category()} is the stored category string
 * of rules.md R-DOM-07, or "Custom".</p>
 */
public enum Skill {
    ADD_2DIGIT_2TERMS("add.2digit.2terms", "Addition"),
    ADD_3DIGIT_3TERMS("add.3digit.3terms", "Addition"),
    ADD_4DIGIT_4TERMS("add.4digit.4terms", "Addition"),

    SUB_2DIGIT_2TERMS("sub.2digit.2terms", "Difference"),
    SUB_3DIGIT_3TERMS("sub.3digit.3terms", "Difference"),
    SUB_4DIGIT_4TERMS("sub.4digit.4terms", "Difference"),

    MUL_2DIGIT_BY_1DIGIT("mul.2digit_by_1digit", "Multiplication"),
    MUL_2DIGIT_3FACTORS("mul.2digit.3factors", "Multiplication"),
    MUL_3DIGIT_3FACTORS("mul.3digit.3factors", "Multiplication"),

    DIV_BASIC("div.basic", "Division"),
    DIV_CHAINED("div.chained", "Division"),
    DIV_2DIGIT_QUOTIENT("div.2digit_quotient", "Division"),

    ORD_SUM_TIMES_DIFF("ord.sum_times_diff", "Special"),
    ORD_DIFF_OVER_SUM("ord.diff_over_sum", "Special"),
    ORD_QUOTIENT_SUM("ord.quotient_sum", "Special"),
    ORD_PRODUCT_OVER_PRODUCT("ord.product_over_product", "Special"),
    ORD_QUOTIENT_MINUS_PRODUCT("ord.quotient_minus_product", "Special"),

    FRAC_OF_QUANTITY("frac.of_quantity", "Fractions"),
    FRAC_MIXED_TO_IMPROPER("frac.mixed_to_improper", "Fractions"),
    FRAC_SIMPLIFY("frac.simplify", "Fractions"),
    PCT_OF_QUANTITY("pct.of_quantity", "Fractions"),
    RATIO_MISSING_PART("ratio.missing_part", "Fractions"),
    FRAC_PRODUCT_OF_QUANTITY("frac.product_of_quantity", "Fractions"),

    PAT_ARITHMETIC("pat.arithmetic", "Patterns"),
    PAT_ALTERNATING_STEPS("pat.alternating_steps", "Patterns"),
    PAT_GROWING_STEP("pat.growing_step", "Patterns"),
    PAT_GEOMETRIC("pat.geometric", "Patterns"),
    PAT_ALTERNATING_OPS("pat.alternating_ops", "Patterns"),
    PAT_FIBONACCI("pat.fibonacci", "Patterns"),
    PAT_SQUARES_OFFSET("pat.squares_offset", "Patterns"),
    PAT_INTERLEAVED("pat.interleaved", "Patterns"),
    PAT_POLYNOMIAL("pat.polynomial", "Patterns"),

    ALG_ONE_STEP_ADD("alg.one_step_add", "Algebra"),
    ALG_ONE_STEP_SUB("alg.one_step_sub", "Algebra"),
    ALG_ONE_STEP_SUB_FROM("alg.one_step_sub_from", "Algebra"),
    ALG_ONE_STEP_MUL("alg.one_step_mul", "Algebra"),
    ALG_TWO_STEP("alg.two_step", "Algebra"),
    ALG_TWO_STEP_SUB_FROM("alg.two_step_sub_from", "Algebra"),
    ALG_TWO_STEP_DIV("alg.two_step_div", "Algebra"),
    ALG_VARS_BOTH_SIDES("alg.vars_both_sides", "Algebra"),
    ALG_SIMULTANEOUS_SUM_DIFF("alg.simultaneous_sum_diff", "Algebra"),
    ALG_SIMULTANEOUS_ELIMINATION("alg.simultaneous_elimination", "Algebra"),

    MEAS_CONVERT_LENGTH("meas.convert_length", "Measurement"),
    MEAS_CONVERT_MASS("meas.convert_mass", "Measurement"),
    MEAS_CONVERT_CAPACITY("meas.convert_capacity", "Measurement"),
    TIME_CONVERT("time.convert", "Measurement"),
    TIME_CONVERT_COMPOUND("time.convert_compound", "Measurement"),
    GEO_PERIMETER_RECT("geo.perimeter_rect", "Measurement"),
    GEO_AREA_SQUARE("geo.area_square", "Measurement"),
    GEO_AREA_RECT("geo.area_rect", "Measurement"),
    MEAS_CONVERT_LENGTH_COMPOUND("meas.convert_length_compound", "Measurement"),
    MEAS_CONVERT_CAPACITY_COMPOUND("meas.convert_capacity_compound", "Measurement"),
    GEO_AREA_FROM_PERIMETER("geo.area_from_perimeter", "Measurement"),
    GEO_PERIMETER_FROM_AREA("geo.perimeter_from_area", "Measurement"),
    GEO_VOLUME_PRISM("geo.volume_prism", "Measurement"),

    CUSTOM("custom", "Custom");

    private final String id;
    private final String category;

    Skill(String id, String category) {
        this.id = id;
        this.category = category;
    }

    /** The persisted key, e.g. {@code "add.2digit.2terms"}. */
    public String id() {
        return id;
    }

    /** The stored category string this skill belongs to, e.g. {@code "Addition"}. */
    public String category() {
        return category;
    }

    /** Looks up a skill by its persisted ID; empty for {@code null} or an unknown ID. */
    public static Optional<Skill> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        for (Skill skill : values()) {
            if (skill.id.equals(id)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    /** All skills of one category (exact, case-sensitive match), in declaration order. */
    public static List<Skill> forCategory(String category) {
        List<Skill> result = new ArrayList<>();
        for (Skill skill : values()) {
            if (skill.category.equals(category)) {
                result.add(skill);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
