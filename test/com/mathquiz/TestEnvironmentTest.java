package com.mathquiz;

import com.mathquiz.config.AppConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Guards rule R-TST-07: tests must never run against a real data folder. */
class TestEnvironmentTest {

    @Test
    void testsRunWithAThrowAwayHomeFolder() {
        String home = System.getProperty("user.home").replace('\\', '/');
        assertTrue(home.endsWith("/build/test/home"),
                "Run tests through test.ps1 only (R-TST-07); user.home was " + home);
        assertTrue(AppConfig.getAppDir().replace('\\', '/').startsWith(home));
    }
}
