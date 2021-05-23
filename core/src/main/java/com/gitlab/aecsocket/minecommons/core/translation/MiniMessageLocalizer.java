package com.gitlab.aecsocket.minecommons.core.translation;

import com.gitlab.aecsocket.minecommons.core.Validation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Locale;

/**
 * A localizer which uses a {@link MiniMessage} to format components.
 * <p>
 * Uses {@link MiniMessage#parse(String, Object...)} to parse arguments.
 */
public final class MiniMessageLocalizer extends AbstractLocalizer {
    /**
     * The builder for a {@link MiniMessageLocalizer}.
     */
    public static final class Builder extends AbstractLocalizer.Builder {
        private MiniMessage miniMessage = MiniMessage.get();

        public MiniMessage miniMessage() { return miniMessage; }
        public Builder miniMessage(MiniMessage miniMessage) { this.miniMessage = miniMessage; return this; }

        @Override
        public MiniMessageLocalizer build() {
            Validation.notNull(miniMessage, "miniMessage");
            Validation.notNull(defaultLocale, "defaultLocale");
            Validation.notNull(fallbackMessage, "fallbackMessage");
            return new MiniMessageLocalizer(miniMessage, defaultLocale, fallbackMessage);
        }
    }

    private MiniMessage miniMessage;

    public MiniMessageLocalizer(MiniMessage miniMessage, Locale defaultLocale, String fallbackMessage) {
        super(defaultLocale, fallbackMessage);
        this.miniMessage = miniMessage;
    }

    /**
     * Creates a new builder.
     * @return The builder.
     */
    public static Builder builder() { return new Builder(); }

    public MiniMessage miniMessage() { return miniMessage; }
    public void miniMessage(MiniMessage miniMessage) { this.miniMessage = miniMessage; }

    @Override
    public Component format(String value, Object... args) {
        return miniMessage.parse(value, args);
    }
}
