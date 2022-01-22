package com.github.aecsocket.minecommons.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Stores an object along with an amount of it.
 * @param <T> The type of object stored.
 * @param object The object stored.
 * @param amount The amount.
 */
public record Quantifier<T>(T object, int amount) {
    /**
     * Creates a quantifier with the same object, and a different amount.
     * @param amount The new amount.
     * @return The new quantifier.
     */
    public Quantifier<T> set(int amount) { return new Quantifier<>(object, amount); }

    /**
     * Creates a quantifier with the same object, and an increased amount.
     * @param amount The amount to add.
     * @return The new quantifier.
     */
    public Quantifier<T> add(int amount) { return set(amount() + amount); }

    /**
     * Creates a quantifier with the same object, and an amount increased by 1.
     * @return The new quantifier.
     */
    public Quantifier<T> add() { return add(1); }

    /**
     * Adds {@code object} to a collection {@code amount} amount of times.
     * @param collection The collection to add to.
     * @param <C> The type of collection.
     * @return The collection modified.
     */
    public <C extends Collection<T>> C addTo(C collection) {
        for (int i = 0; i < amount; i++) {
            collection.add(object);
        }
        return collection;
    }

    /**
     * Returns a new list with {@code object} added to it {@code amount} amount of times.
     * @return The new list.
     */
    public List<T> asList() { return addTo(new ArrayList<>()); }

    @Override public String toString() { return "%s x%d".formatted(object, amount); }

    /**
     * Totals up all amounts of quantifiers in an {@link Iterable}.
     * @param quantifiers The quantifiers.
     * @param <T> The type of quantifier.
     * @return The total.
     */
    public static <T> int total(Iterable<Quantifier<T>> quantifiers) {
        int total = 0;
        for (Quantifier<T> object : quantifiers) {
            total += object.amount;
        }
        return total;
    }

    /**
     * Totals up all amounts of quantifiers in an array of quantifiers.
     * @param quantifiers The quantifiers.
     * @param <T> The type of quantifier.
     * @return The total.
     */
    @SafeVarargs
    public static <T> int total(Quantifier<T>... quantifiers) {
        int total = 0;
        for (Quantifier<T> object : quantifiers) {
            total += object.amount;
        }
        return total;
    }
}
