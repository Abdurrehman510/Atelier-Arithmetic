package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import javax.sound.sampled.*;

/**
 * Programmatic audio player that generates synthetic sounds at runtime.
 * Fits within the zero-dependency goal (uses standard javax.sound.sampled).
 *
 * Tones generated:
 *   - Correct Answer: Pleasing short C-major arpeggio (C5 to E5, 523.25Hz -> 659.25Hz)
 *   - Incorrect Answer: Soft warning buzzer tone (220Hz -> 180Hz downward slide)
 *
 * Phase 3 — Engagement & Gamification.
 */
public class SoundService {

    private final AppConfig config;

    public SoundService(AppConfig config) {
        this.config = config;
    }

    /** Plays a pleasant success sound effect (C5 then E5 double arpeggio). */
    public void playCorrect() {
        if (!config.isSoundEnabled()) return;
        new Thread(() -> {
            playTone(44100, 523.25, 80, 0.25); // C5
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            playTone(44100, 659.25, 120, 0.25); // E5
        }).start();
    }

    /** Plays a soft buzz tone. */
    public void playIncorrect() {
        if (!config.isSoundEnabled()) return;
        new Thread(() -> {
            playToneSlide(44100, 220.0, 160.0, 250, 0.25);
        }).start();
    }

    // =========================================================================
    // Programmatic Synth Tones
    // =========================================================================

    private void playTone(int rate, double hz, int msecs, double vol) {
        try {
            byte[] buf = new byte[msecs * rate / 1000];
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (rate / hz) * 2.0 * Math.PI;
                // Add envelope (smooth fade out) to prevent pops/clicks
                double envelope = 1.0 - ((double) i / buf.length);
                buf[i] = (byte) (Math.sin(angle) * 127.0 * vol * envelope);
            }
            AudioFormat format = new AudioFormat(rate, 8, 1, true, true);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(format);
            sdl.open(format);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.close();
        } catch (Exception ignored) {}
    }

    private void playToneSlide(int rate, double startHz, double endHz, int msecs, double vol) {
        try {
            byte[] buf = new byte[msecs * rate / 1000];
            for (int i = 0; i < buf.length; i++) {
                double progress = (double) i / buf.length;
                double hz = startHz + (endHz - startHz) * progress;
                double angle = i / (rate / hz) * 2.0 * Math.PI;
                double envelope = 1.0 - progress;
                buf[i] = (byte) (Math.sin(angle) * 127.0 * vol * envelope);
            }
            AudioFormat format = new AudioFormat(rate, 8, 1, true, true);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(format);
            sdl.open(format);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.close();
        } catch (Exception ignored) {}
    }
}
