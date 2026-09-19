package com.threemsystems.rentmanager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginResultTest {
    @Test
    public void parsesJsonSuccess() {
        LoginResult result = LoginResult.parse(
                "{\"success\":\"true\",\"owner_id\":\"OWN1\",\"name\":\"Jane Doe\",\"user_level\":\"9\",\"username\":\"0722\",\"token\":\"abc\"}",
                "0722"
        );
        assertTrue(result.success);
        assertEquals("OWN1", result.ownerId);
        assertEquals("Jane Doe", result.name);
        assertEquals("9", result.userLevel);
        assertEquals("abc", result.token);
    }

    @Test
    public void parsesJsonFailure() {
        LoginResult result = LoginResult.parse("{\"success\":\"false\",\"message\":\"Incorrect username or password.\"}", "0722");
        assertFalse(result.success);
        assertEquals("Incorrect username or password.", result.message);
    }

    @Test
    public void namesWithUnderscoresDoNotBreakJson() {
        LoginResult result = LoginResult.parse(
                "{\"success\":\"true\",\"owner_id\":\"OWN1\",\"name\":\"Mary_Anne\",\"user_level\":\"2\",\"token\":\"tok\"}",
                "user"
        );
        assertTrue(result.success);
        assertEquals("OWN1", result.ownerId);
        assertEquals("Mary_Anne", result.name);
        assertEquals("2", result.userLevel);
    }
}
