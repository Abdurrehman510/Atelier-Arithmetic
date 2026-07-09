package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import javax.sound.sampled.*;

/**
 * Programmatic audio player that generates synthetic sounds at runtime.
 * Fits within the zero-dependency goal (uses standard javax.sound.sampled).
 * Contains volume control and a rich suite of child-friendly synthesizer tones.
 */
public class SoundService {

    private final AppConfig config;

    public SoundService(AppConfig config) {
        this.config = config;
    }

    private double getVolumeFactor() {
        if (!config.isSoundEnabled()) return 0.0;
        return config.getSoundVolume() / 100.0;
    }

    /** Plays a very short, high-frequency, extremely soft hover tick. */
    public void playHover() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 1200.0, 15, vol * 0.1); // very quiet
        }).start();
    }

    /** Plays a brief, clean click pop. */
    public void playClick() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 600.0, 30, vol * 0.35);
        }).start();
    }

    /** Plays a pleasant success sound (C-major triad arpeggio: C5 -> E5 -> G5). */
    public void playCorrect() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 523.25, 100, vol * 0.5); // C5
            try { Thread.sleep(70); } catch (InterruptedException ignored) {}
            playTone(44100, 659.25, 100, vol * 0.5); // E5
            try { Thread.sleep(70); } catch (InterruptedException ignored) {}
            playTone(44100, 783.99, 140, vol * 0.6); // G5
        }).start();
    }

    /** Plays a soft buzz tone downward slide. */
    public void playIncorrect() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playToneSlide(44100, 220.0, 150.0, 300, vol * 0.4);
        }).start();
    }

    /** App Launch: Rising warm chord chime. */
    public void playLaunch() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 261.63, 150, vol * 0.4); // C4
            try { Thread.sleep(90); } catch (InterruptedException ignored) {}
            playTone(44100, 329.63, 150, vol * 0.4); // E4
            try { Thread.sleep(90); } catch (InterruptedException ignored) {}
            playTone(44100, 392.00, 200, vol * 0.5); // G4
            try { Thread.sleep(90); } catch (InterruptedException ignored) {}
            playTone(44100, 523.25, 350, vol * 0.5); // C5
        }).start();
    }

    /** Navigation / Transition: Quick slide up chime. */
    public void playTransition() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playToneSlide(44100, 400.0, 800.0, 180, vol * 0.3);
        }).start();
    }

    /** Category select / click card: Bright bell chime. */
    public void playCategorySelect() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 880.00, 150, vol * 0.45); // A5
        }).start();
    }

    /** Quiz Start: Energetic rising arpeggio. */
    public void playQuizStart() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 523.25, 120, vol * 0.5); // C5
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            playTone(44100, 659.25, 120, vol * 0.5); // E5
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            playTone(44100, 783.99, 120, vol * 0.5); // G5
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            playTone(44100, 1046.50, 250, vol * 0.6); // C6
        }).start();
    }

    /** Clock Timer Tick: Very quiet wooden clock tick. */
    public void playTick() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 900.0, 10, vol * 0.12);
        }).start();
    }

    /** Streak Milestone: Uplifting quick slide up. */
    public void playStreak() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playToneSlide(44100, 300.0, 900.0, 400, vol * 0.5);
        }).start();
    }

    /** Achievement Badge Unlocked: Sparkling high bell double arpeggio. */
    public void playAchievement() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 1046.50, 100, vol * 0.4); // C6
            try { Thread.sleep(70); } catch (InterruptedException ignored) {}
            playTone(44100, 1318.51, 100, vol * 0.4); // E6
            try { Thread.sleep(70); } catch (InterruptedException ignored) {}
            playTone(44100, 1567.98, 300, vol * 0.5); // G6
        }).start();
    }

    /** Fanfare on Quiz Completion / Results Reveal. */
    public void playFanfare() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 261.63, 200, vol * 0.35); // C4
            playTone(44100, 329.63, 200, vol * 0.35); // E4
            playTone(44100, 392.00, 200, vol * 0.35); // G4
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            playTone(44100, 523.25, 450, vol * 0.5); // C5
        }).start();
    }

    /** Notification / Warning double pop. */
    public void playNotification() {
        double vol = getVolumeFactor();
        if (vol <= 0.0) return;
        new Thread(() -> {
            playTone(44100, 440.0, 80, vol * 0.4);
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            playTone(44100, 440.0, 150, vol * 0.4);
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
