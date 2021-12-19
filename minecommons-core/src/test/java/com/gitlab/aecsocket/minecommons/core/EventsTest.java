package com.gitlab.aecsocket.minecommons.core;

import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import io.leangen.geantyref.TypeToken;
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

    static class SubEvent extends Event {
        public SubEvent(int flag) {
            super(flag);
        }
    }

    static class GenericEvent<T> {
        T value;

        public GenericEvent(T value) {
            this.value = value;
        }
    }

    static class GenericSubEvent<T> extends GenericEvent<T> {
        public GenericSubEvent(T value) {
            super(value);
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
            events.call(new SubEvent(3));
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

    @Test
    @SuppressWarnings("rawtypes")
    void testGenerics() {
        EventDispatcher<GenericEvent<?>> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        // check if non-parameterized types don't error out
        events.register(new TypeToken<GenericEvent>(){}, evt -> {});

        events.register(new TypeToken<GenericEvent<Integer>>(){}, evt -> flag.set(evt.value));

        assertThrows(ClassCastException.class, () -> events.call(new GenericEvent<>(10L)));

        assertTimeout(ofMillis(10), () -> {
            events.call(new GenericEvent<>(10));
            assertEquals(10, flag.get());
        });

        assertTimeout(ofMillis(10), () -> {
            events.call(new GenericSubEvent<>(20));
            assertEquals(20, flag.get());
        });
    }
}
