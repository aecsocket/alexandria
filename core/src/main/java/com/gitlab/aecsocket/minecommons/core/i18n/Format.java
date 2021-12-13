package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

public sealed interface Format permits FormatImpl {
    @Nullable Style style();
    Map<String, Style> templates();
}
