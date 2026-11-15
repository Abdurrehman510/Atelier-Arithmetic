package com.mathquiz.testutil;

/**
 * Rule R-TST-07 guard: tests that write through {@code AppConfig} or the default data folder must
 * call {@link #requireThrowAwayHome()} first (e.g. from {@code @BeforeAll}), so running them outside
 * {@code test.ps1} (an IDE, a bare JUnit command) can never touch a real ~/.atelier-arithmetic folder.
 */
public final class TestHome {

    /** The throw-away home that {@code test.ps1} passes as {@code -Duser.home}. */
    public static final String SUFFIX = "/build/test/home";

    private TestHome() {
    }

    /** Fails unless the JVM's {@code user.home} is the throw-away test home. */
    public static void requireThrowAwayHome() {
        check(System.getProperty("user.home"));
    }

    /** Fails unless {@code home} ends with {@value #SUFFIX} (either path separator). */
    public static void check(String home) {
        if (home == null || !home.replace('\\', '/').endsWith(SUFFIX)) {
            throw new IllegalStateException(
                    "Refusing to run: user.home is not the throw-away test home (" + home + "). Run tests with test.ps1 (R-TST-07).");
        }
    }
}
