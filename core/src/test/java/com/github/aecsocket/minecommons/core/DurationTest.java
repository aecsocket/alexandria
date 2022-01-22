package com.github.aecsocket.minecommons.core;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.github.aecsocket.minecommons.core.Duration.*;
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
    void testAsString() {
        assertEquals("5m", duration(0, 0, 5, 0, 0).toString());
        assertEquals("5m", duration(1000 * 60 * 5).toString());
        assertEquals("1d 2h 3m 4.500s", duration(1, 2, 3, 4, 500).toString());
        assertEquals("0.000s", duration(0).toString());

        assertEquals("4.500s", duration(4500).asString(Locale.ENGLISH));
        assertEquals("4,500s", duration(4500).asString(Locale.GERMAN));
    }

    @Test
    void testParse() {
        assertEquals(duration(500), duration("0.5s"));
        assertEquals(duration(1, 2, 3, 4, 500), duration("1d2h3m4.5s"));
        assertEquals(duration(1, 2, 3, 4, 500), duration("1d2h 3m4.5s"));
        assertEquals(duration(1, 2, 3, 4, 500), duration("1d 2h 3m 4.5s"));
        assertEquals(duration(0, 1, 0, 2, 0), duration("1h2s"));
        assertEquals(duration(0), duration(""));
    }
}
