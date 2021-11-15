package com.gitlab.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.*;
import static net.kyori.adventure.text.format.Style.*;

public final class ConfigurationNodes {
    private ConfigurationNodes() {}

    public record RenderOptions(
            Style scalar,
            Style bool,
            Style number,
            Style string,

            Style comment,
            Style bracket,
            Style separator,
            Style key,
            Style listIndex,
            Component keySeparator,
            Component indent,
            Component listIndexSuffix
    ) {
        public static final RenderOptions DEFAULT = new RenderOptions(
                style(NamedTextColor.WHITE),
                style(NamedTextColor.YELLOW),
                style(NamedTextColor.AQUA),
                style(NamedTextColor.WHITE),

                style(NamedTextColor.DARK_GREEN),
                style(NamedTextColor.DARK_GRAY),
                style(NamedTextColor.GRAY),
                style(NamedTextColor.GRAY),
                style(NamedTextColor.WHITE),
                text(": ", NamedTextColor.DARK_GRAY),
                text("  "),
                text(") ", NamedTextColor.WHITE)
        );
    }

    private static void addComment(CommentedConfigurationNode commented, List<Component> lines, RenderOptions options) {
        String comment = commented.comment();
        if (comment != null) {
            comment.lines()
                    .forEach(line -> lines.add(text("# " + line, options.comment)));
        }
    }

    private static List<Component> render(ConfigurationNode node, RenderOptions options, boolean showComments, boolean commentsNow) {
        JoinConfiguration join = separator(text(", ", options.separator));
        List<Component> lines = new ArrayList<>();
        if (showComments && commentsNow && node instanceof CommentedConfigurationNode commented)
            addComment(commented, lines, options);
        if (node.isMap()) {
            for (var entry : node.childrenMap().entrySet()) {
                ConfigurationNode child = entry.getValue();
                Component start = text(""+entry.getKey(), options.key)
                        .append(options.keySeparator);

                if (showComments && child instanceof CommentedConfigurationNode commented)
                    addComment(commented, lines, options);
                List<Component> childLines = render(child, options, showComments, false);
                if (childLines.size() == 1) {
                    lines.add(start
                            .append(childLines.get(0)));
                } else {
                    lines.add(start);
                    childLines.forEach(l -> lines.add(options.indent.append(l)));
                }
            }
        } else if (node.isList()) {
            List<List<Component>> childrenLines = new ArrayList<>();
            boolean singleLine = true;
            for (var child : node.childrenList()) {
                var childLines = render(child, options, showComments, true);
                if (childLines.size() != 1)
                    singleLine = false;
                childrenLines.add(childLines);
            }

            if (singleLine) {
                Component line = text("[ ", options.bracket);
                List<Component> children = new ArrayList<>();
                for (var childLines : childrenLines)
                    children.add(childLines.get(0));
                lines.add(text("[ ", options.bracket)
                        .append(join(join, children))
                        .append(text(" ]", options.bracket)));
            } else {
                for (int i = 0; i < childrenLines.size(); i++) {
                    Component indexRender = text(i + 1, options.listIndex)
                            .append(options.listIndexSuffix);
                    int indexLength = PlainTextComponentSerializer.plainText().serialize(indexRender).length();
                    List<Component> childLines = childrenLines.get(i);
                    for (int j = 0; j < childLines.size(); j++) {
                        lines.add(options.indent
                                .append(j == 0
                                        ? indexRender
                                        : text(" ".repeat(indexLength), options.listIndex))
                                .append(childLines.get(j)));
                    }
                }
            }
        } else {
            Object raw = node.raw();
            if (raw instanceof Boolean val) lines.add(text(val.toString(), options.bool));
            else if (raw instanceof Number val) lines.add(text(val.toString(), options.number));
            else if (raw instanceof String val) lines.add(text(val, options.string));
            else lines.add(text("" + node.rawScalar(), options.scalar));
        }
        return lines;
    }

    public static List<Component> render(ConfigurationNode node, RenderOptions options, boolean showComments) {
        return render(node, options, showComments, true);
    }
}
