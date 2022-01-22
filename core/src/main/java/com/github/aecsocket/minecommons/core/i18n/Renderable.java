package com.github.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;

import java.util.Locale;

/**
 * An object which can be expressed as a component when using an i18n service.
 */
public interface Renderable {
    /**
     * Renders this object as a component.
     * @param i18n The i18n service.
     * @param locale The locale.
     * @return The rendered form.
     */
    Component render(I18N i18n, Locale locale);
}
