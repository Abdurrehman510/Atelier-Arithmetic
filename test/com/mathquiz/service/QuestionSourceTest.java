package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.QuizSession;
import com.mathquiz.model.Skill;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class QuestionSourceTest {

    private final QuestionSource source = new QuestionSource(new QuestionGenerator());

    @Test
    void dailyChallengeIsSeededMixed() {
        QuestionSource.Prepared p = source.next(new QuizSession(10, "Medium", "Daily Challenge"), null, 20261108L);
        assertEquals(new QuestionGenerator().generateSeeded("Medium", "Mixed", 20261108L, 0).getExpression(),
                p.question().getExpression());
        assertEquals("Medium", p.difficulty());
    }

    @Test
    void difficultyChangeAppliesToTheNextQuestion() {
        QuizSession session = new QuizSession(10, "Easy", "Addition");
        QuestionSource.Prepared first = source.next(session, null, 20261108L);
        assertEquals("Easy", first.difficulty());
        assertEquals(Skill.ADD_2DIGIT_2TERMS, first.question().getSkill());

        session.setDifficulty("Hard");
        QuestionSource.Prepared next = source.next(session, null, 20261108L);
        assertEquals("Hard", next.difficulty());
        assertEquals(Skill.ADD_4DIGIT_4TERMS, next.question().getSkill());
    }

    @Test
    void customQuestionsComeFirst() {
        List<Question> custom = List.of(new Question("7 * 8", 56), new Question("9 * 6", 54));
        QuestionSource.Prepared p = source.next(new QuizSession(2, "Medium", "Custom: Times"), custom, 20261108L);
        assertSame(custom.get(0), p.question());
        assertEquals("Medium", p.difficulty());
        assertEquals(Skill.CUSTOM, p.question().getSkill());
    }
}
