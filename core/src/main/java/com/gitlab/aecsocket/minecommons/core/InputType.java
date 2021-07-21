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
    /** Swapping to a different hotbar slot. */
    SWAP,
    /** Changing hotbar slot in the mouse wheel up direction. */
    SCROLL_UP,
    /** Changing hotbar slot in the mouse wheel down direction. */
    SCROLL_DOWN,
    /** Starting to sneak. */
    SNEAK_START,
    /** Stopping sneaking. */
    SNEAK_STOP,
    /** Starting to sprint. */
    SPRINT_START,
    /** Stopping sprinting. */
    SPRINT_STOP,
    /** Starting to fly. */
    FLIGHT_START,
    /** Stopping flying. */
    FLIGHT_STOP,
    /** Opening the advancements menu. */
    ADVANCEMENTS
}
