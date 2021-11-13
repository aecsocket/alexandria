package com.gitlab.aecsocket.minecommons.core;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.title.Title.*;
import static net.kyori.adventure.text.Component.*;

/**
 * Represents a position on the client screen which can contain an arbitrary text {@link Component}.
 */
@FunctionalInterface
public interface ChatPosition {
    /** Sends the message in the chat. */
    ChatPosition CHAT = Audience::sendMessage;
    /** Sends the message in the action bar. */
    ChatPosition ACTION_BAR = Audience::sendActionBar;
    /** Sends the message in the title, with an empty subtitle. */
    ChatPosition TITLE = (viewer, content) -> viewer.showTitle(title(content, empty()));
    /** Sends the message in the subtitle, with an empty title. */
    ChatPosition SUBTITLE = (viewer, content) -> viewer.showTitle(title(empty(), content));
    /** Sends the message in the tab list header. */
    ChatPosition TAB_HEADER = Audience::sendPlayerListHeader;
    /** Sends the message in the tab list footer. */
    ChatPosition TAB_FOOTER = Audience::sendPlayerListFooter;

    /** Map of default values to their keys. */
    Map<String, ChatPosition> VALUES = CollectionBuilder.map(new HashMap<String, ChatPosition>())
            .put("chat", CHAT)
            .put("action_bar", ACTION_BAR)
            .put("title", TITLE)
            .put("subtitle", SUBTITLE)
            .put("tab_header", TAB_HEADER)
            .put("tab_footer", TAB_FOOTER)
            .build();

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
}
