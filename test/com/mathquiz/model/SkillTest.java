package com.mathquiz.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Skill IDs are persisted keys (R-DOM-12): this test fails on any rename, removal or reorder. */
class SkillTest {

    private static final List<String> GENERATOR_CATEGORIES = List.of(
            "Addition", "Difference", "Multiplication", "Division", "Special",
            "Fractions", "Patterns", "Algebra", "Measurement");

    @Test
    void skillIdsAreStable() {
        assertEquals(List.of(
                "add.2digit.2terms", "add.3digit.3terms", "add.4digit.4terms",
                "sub.2digit.2terms", "sub.3digit.3terms", "sub.4digit.4terms",
                "mul.2digit_by_1digit", "mul.2digit.3factors", "mul.3digit.3factors",
                "div.basic", "div.chained", "div.2digit_quotient",
                "ord.sum_times_diff", "ord.diff_over_sum", "ord.quotient_sum",
                "ord.product_over_product", "ord.quotient_minus_product",
                "frac.of_quantity", "frac.mixed_to_improper", "frac.simplify",
                "pct.of_quantity", "ratio.missing_part", "frac.product_of_quantity",
                "pat.arithmetic", "pat.alternating_steps", "pat.growing_step", "pat.geometric",
                "pat.alternating_ops", "pat.fibonacci", "pat.squares_offset", "pat.interleaved",
                "pat.polynomial",
                "alg.one_step_add", "alg.one_step_sub", "alg.one_step_sub_from", "alg.one_step_mul",
                "alg.two_step", "alg.two_step_sub_from", "alg.two_step_div", "alg.vars_both_sides",
                "alg.simultaneous_sum_diff", "alg.simultaneous_elimination",
                "meas.convert_length", "meas.convert_mass", "meas.convert_capacity",
                "time.convert", "time.convert_compound", "geo.perimeter_rect", "geo.area_square",
                "geo.area_rect", "meas.convert_length_compound", "meas.convert_capacity_compound",
                "geo.area_from_perimeter", "geo.perimeter_from_area", "geo.volume_prism",
                "custom"),
                Arrays.stream(Skill.values()).map(Skill::id).toList());
    }

    @Test
    void idsAreUniqueAndWellFormed() {
        List<String> ids = Arrays.stream(Skill.values()).map(Skill::id).toList();
        for (String id : ids) {
            assertTrue(id.matches("[a-z]+(\\.[a-z0-9_]+)*"), id);
        }
        assertEquals(Skill.values().length, new HashSet<>(ids).size());
    }

    @Test
    void everyGeneratorCategoryHasSkills() {
        for (String category : GENERATOR_CATEGORIES) {
            assertFalse(Skill.forCategory(category).isEmpty(), category);
        }
        assertEquals(List.of(Skill.CUSTOM), Skill.forCategory("Custom"));
    }

    @Test
    void fromIdRoundTripsAndRejectsUnknown() {
        for (Skill skill : Skill.values()) {
            assertEquals(Optional.of(skill), Skill.fromId(skill.id()));
        }
        assertTrue(Skill.fromId("nope").isEmpty());
        assertTrue(Skill.fromId(null).isEmpty());
    }
}
