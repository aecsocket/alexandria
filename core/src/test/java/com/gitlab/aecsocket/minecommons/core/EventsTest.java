package com.gitlab.aecsocket.minecommons.core;

import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static java.time.Duration.*;

public class EventsTest {
    static class Event {
        int flag;

        public Event(int flag) {
            this.flag = flag;
        }
    }

    static class ExtraEvent extends Event {
        public ExtraEvent(int flag) {
            super(flag);
        }
    }

    static record Listener(String name, Consumer<Event> consumer) implements Consumer<Event> {
        @Override
        public void accept(Event event) {
            consumer.accept(event);
        }

        @Override
        public String toString() { return name; }
    }

    Listener listen(String name, Consumer<Event> consumer) {
        return new Listener(name, consumer);
    }

    @Test
    void testDispatch() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        events.register(Event.class, evt -> flag.set(evt.flag));
        assertTimeout(ofMillis(10), () -> {
            events.call(new Event(3));
            assertEquals(3, flag.get());
        });
    }

    @Test
    void testSpecific() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        events.register(Event.class, true, evt -> flag.set(evt.flag));
        assertTimeout(ofMillis(10), () -> {
            events.call(new ExtraEvent(3));
            assertEquals(0, flag.get());
        });
    }

    @Test
    void testPriority() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        events.register(Event.class, listen("+100", evt -> flag.set(flag.get() + 100)), 1);
        events.register(Event.class, listen("/flag", evt -> flag.set(flag.get() / evt.flag)));
        events.register(Event.class, listen("+10", evt -> flag.set(flag.get() + 10)), -1);
        assertTimeout(ofMillis(10), () -> {
            events.call(new Event(2));
            assertEquals(105, flag.get());
        });
    }
}
