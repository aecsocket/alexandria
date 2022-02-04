package com.github.aecsocket.minecommons.core;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a position on the client screen which can contain an arbitrary text {@link Component}.
 */
@FunctionalInterface
public interface ChatPosition {
    /**
     * Creates a chat position which changes the name of a boss bar.
     * @param bar The boss bar to modify the name of.
     * @return The chat position.
     */
    static ChatPosition bossBarName(BossBar bar) {
        return (viewer, content) -> bar.name(content);
    }

    /**
     * Sends a message to a viewer in this position.
     * @param viewer The viewer.
     * @param content The component to send.
     */
    void send(Audience viewer, Component content);

    /**
     * The default implementations of the chat position, as an enum.
     */
    enum Named implements ChatPosition {
        /** Sends the message in the chat. */
        CHAT {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendMessage(content);
            }
        },
        /** Sends the message in the action bar. */
        ACTION_BAR {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendActionBar(content);
            }
        },
        /** Sends the message in the title. */
        TITLE {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendTitlePart(TitlePart.TITLE, content);
            }
        },
        /** Sends the message in the subtitle. */
        SUBTITLE {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendTitlePart(TitlePart.SUBTITLE, content);
            }
        },
        /** Sends the message in the tab list header. */
        TAB_HEADER {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendPlayerListHeader(content);
            }
        },
        /** Sends the message in the tab list footer. */
        TAB_FOOTER {
            @Override
            public void send(Audience viewer, Component content) {
                viewer.sendPlayerListFooter(content);
            }
        };

        private static Map<String, Named> buildByName() {
            Map<String, Named> result = new HashMap<>();
            for (var val : values()) {
                result.put(val.name().toLowerCase(Locale.ROOT), val);
            }
            return result;
        }

        /** A map containing the named chat positions, mapped to their lowercase names. */
        public static final Map<String, Named> BY_NAME = Collections.unmodifiableMap(buildByName());
    }
}
