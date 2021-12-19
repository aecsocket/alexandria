package com.gitlab.aecsocket.minecommons.core.i18n;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/* package */ record FormatImpl(
        @Nullable String style,
        Map<String, String> templates
) implements Format {}
