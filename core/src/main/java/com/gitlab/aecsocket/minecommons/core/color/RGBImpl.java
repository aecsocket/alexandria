package com.gitlab.aecsocket.minecommons.core.color;

/* package */ record RGBImpl(int value) implements RGB {
    @Override public int value() { return value; }

    @Override public String toString() { return asHex(); }
}
