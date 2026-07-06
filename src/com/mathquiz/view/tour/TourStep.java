package com.mathquiz.view.tour;

import java.awt.Component;

/**
 * Data class representing a single step in the interactive guided tour.
 *
 * Each step knows:
 *   - which screen it belongs to (so QuizFrame only shows it on the right card)
 *   - the target component to spotlight (null = highlight whole screen)
 *   - the child-friendly message Archie speaks
 *   - where to place the speech bubble relative to the spotlight
 *   - which mascot expression to draw
 */
public class TourStep {

    public enum BubblePosition {
        BOTTOM_CENTER,   // bubble appears below the spotlight
        TOP_CENTER,      // bubble appears above the spotlight
        LEFT_CENTER,     // bubble appears to the left
        RIGHT_CENTER,    // bubble appears to the right
        SCREEN_CENTER    // bubble is centred on screen (for intro/outro steps)
    }

    public enum MascotExpression {
        WAVING,    // greeting / introduction
        HAPPY,     // positive feedback / correct answer
        EXCITED,   // achievement / completion
        THINKING,  // explaining a concept
        POINTING   // pointing at a specific element
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    public final String          screen;
    public final Component       target;        // nullable
    public final String          message;
    public final BubblePosition  bubblePos;
    public final MascotExpression expression;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public TourStep(String screen,
                    Component target,
                    String message,
                    BubblePosition bubblePos,
                    MascotExpression expression) {
        this.screen     = screen;
        this.target     = target;
        this.message    = message;
        this.bubblePos  = bubblePos;
        this.expression = expression;
    }

    /** Convenience constructor with defaults (BOTTOM_CENTER, HAPPY). */
    public TourStep(String screen, Component target, String message) {
        this(screen, target, message, BubblePosition.BOTTOM_CENTER, MascotExpression.HAPPY);
    }
}
