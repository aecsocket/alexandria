package com.gitlab.aecsocket.minecommons.core;

import org.junit.jupiter.api.Test;

import static com.gitlab.aecsocket.minecommons.core.Duration.*;
import static org.junit.jupiter.api.Assertions.*;

public class DurationTest {
    @Test
    void testEquality() {
        Duration one = duration(1000 * 60 * 5);
        Duration two = duration(0, 0, 5, 0, 0);
        assertEquals(one, two);

        Duration three = duration(0, 0, 6, 0, 0);
        assertNotEquals(one, three);
    }

    @Test
    void testToString() {
        assertEquals(duration(0, 0, 5, 0, 0).toString(), "5m");
        assertEquals(duration(1000 * 60 * 5).toString(), "5m");
        assertEquals(duration(1, 2, 3, 4, 500).toString(), "1d2h3m4.5s");
        assertEquals(duration(0).toString(), "0.0s");
    }

    @Test
    void testParse() {
        assertEquals(duration(500), duration("0.5s"));
        assertEquals(duration(1, 2, 3, 4, 500), duration("1d2h3m4.5s"));
        assertEquals(duration(0, 1, 0, 2, 0), duration("1h2s"));
        assertEquals(duration(0), duration(""));
    }
}
