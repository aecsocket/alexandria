package com.gitlab.aecsocket.minecommons.core;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

/**
 * Represents a position on the client screen which can contain an arbitrary text {@link Component}.
 */
public enum ChatPosition {
    /** The chat. */
    CHAT        (Audience::sendMessage),
    /** The action bar, above the hotbar. */
    ACTION_BAR  (Audience::sendActionBar),
    /** The large title. */
    TITLE       ((viewer, content) -> viewer.showTitle(Title.title(content, Component.empty()))),
    /** The smaller subtitle. */
    SUBTITLE    ((viewer, content) -> viewer.showTitle(Title.title(Component.empty(), content))),
    /** The player list header. */
    TAB_HEADER  (Audience::sendPlayerListHeader),
    /** The player list footer. */
    TAB_FOOTER  (Audience::sendPlayerListFooter);

    private interface Sender {
        void send(Audience viewer, Component content);
    }

    private final Sender sender;

    ChatPosition(Sender sender) {
        this.sender = sender;
    }

    /**
     * Sends a message to a viewer in this position.
     * @param viewer The viewer.
     * @param content The component to send.
     */
    public void send(Audience viewer, Component content) {
        sender.send(viewer, content);
    }
}
