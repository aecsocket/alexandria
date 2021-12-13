package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;

import java.util.Locale;

public interface Renderable {
    Component render(I18N i18n, Locale locale);
}
