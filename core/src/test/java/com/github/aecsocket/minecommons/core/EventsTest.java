package com.github.aecsocket.minecommons.core;

import io.leangen.geantyref.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.github.aecsocket.minecommons.core.event.EventDispatcher;

import static org.junit.jupiter.api.Assertions.*;

class EventsTest {
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

    record Listener<E>(String name, Consumer<E> consumer) implements Consumer<E> {
        @Override
        public void accept(E event) {
            consumer.accept(event);
        }

        @Override
        public String toString() { return name; }
    }

    <E> Listener<E> listen(String name, Consumer<E> consumer) {
        return new Listener<>(name, consumer);
    }

    @Test
    void testDispatch() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        events.register(Event.class, false, 0, evt -> flag.set(evt.flag));
        events.call(new Event(3));
        assertEquals(3, flag.get());
    }

    @Test
    void testSpecific() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();
        events.register(Event.class, true, 0, evt -> flag.set(evt.flag));
        events.call(new SubEvent(3));
        assertEquals(0, flag.get());
    }

    @Test
    void testPriority() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();

        events.register(Event.class, false, -1, listen("+10", evt -> flag.set(flag.get() + 10)));
        events.register(Event.class, false, 0, listen("/flag", evt -> flag.set(flag.get() / evt.flag)));
        events.register(Event.class, false, 1, listen("+100", evt -> flag.set(flag.get() + 100)));
        events.call(new Event(2));
        assertEquals(105, flag.get());

        flag.set(0);
        events.unregisterAll();
        // make sure the register order doesn't matter
        events.register(Event.class, false, 0, listen("/flag", evt -> flag.set(flag.get() / evt.flag)));
        events.register(Event.class, false, 1, listen("+100", evt -> flag.set(flag.get() + 100)));
        events.register(Event.class, false, -1, listen("+10", evt -> flag.set(flag.get() + 10)));
        events.call(new Event(2));
        assertEquals(105, flag.get());
    }

    @Test
    void testDuplicatePriority() {
        EventDispatcher<Event> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();

        events.register(Event.class, false, 0, listen("1", evt -> flag.addAndGet(evt.flag)));
        events.register(Event.class, false, 0, listen("2", evt -> flag.addAndGet(evt.flag)));
        events.call(new Event(10));
        assertEquals(20, flag.get());
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testGenerics() {
        EventDispatcher<GenericEvent<?>> events = new EventDispatcher<>();
        AtomicInteger flag = new AtomicInteger();

        // check if non-parameterized types don't error out
        events.register(new TypeToken<GenericEvent>(){}, false, 0, this.listen("non-param", evt -> {}));
        events.register(new TypeToken<GenericEvent<Integer>>(){}, false, 0, listen("flag set", evt -> flag.set(evt.value)));

        assertThrows(ClassCastException.class, () -> events.call(new GenericEvent<>(10L)));

        events.call(new GenericEvent<>(10));
        assertEquals(10, flag.get());

        events.call(new GenericSubEvent<>(20));
        assertEquals(20, flag.get());
    }
}
