package com.gitlab.aecsocket.minecommons.core;

/**
 * Types of inputs that a client can enter, and a server can usually detect.
 */
public enum InputType {
    /** Left-clicking with the mouse. */
    MOUSE_LEFT,
    /** Right-clicking, or having right-click held for ~200ms, with the mouse. */
    MOUSE_RIGHT,
    /** Pressing the "swap hands" key. */
    OFFHAND,
    /** Dropping the current item, including dropping the entire stack. */
    DROP,
    /** Opening the advancements menu. */
    ADVANCEMENTS
}
