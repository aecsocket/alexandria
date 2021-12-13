package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/* package */ record FormatImpl(
        @Nullable Style style,
        Map<String, Style> templates
) implements Format {}
