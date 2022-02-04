package com.github.aecsocket.minecommons.paper.plugin;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;

import com.github.aecsocket.minecommons.core.ConfigurationNodes;
import com.github.aecsocket.minecommons.core.Settings;
import com.github.aecsocket.minecommons.core.i18n.I18N;
import com.github.aecsocket.minecommons.paper.command.DurationArgument;
import com.github.aecsocket.minecommons.paper.command.KeyArgument;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for using a Cloud {@link PaperCommandManager}.
 */
public class BaseCommand<P extends BasePlugin<P>> {
    private static final String ERROR_COMMAND = "error.command";
    private static final String ERROR_EXCEPTION_NO_MESSAGE = "error.exception.no_message";
    private static final String ERROR_EXCEPTION_MESSAGE = "error.exception.message";
    private static final String ERROR_CAPTION = "error.caption";
    private static final String ERROR_NO_ARG = "error.no_arg";
    private static final String ERROR_NO_TARGETS = "error.no_targets";
    private static final String ERROR_NO_NODE_VALUE = "error.no_node_value";

    private static final String COMMAND_VERSION = "command.version";
    private static final String COMMAND_RELOAD_START = "command.reload.start";
    private static final String COMMAND_RELOAD_END = "command.reload.end";
    private static final String COMMAND_SETTING = "command.setting";

    /**
     * An exception that represents a user-facing error.
     */
    protected static class CommandException extends RuntimeException {
        /** The localization key. */
        private final String key;
        /** The localization arguments. */
        private final transient I18N.TemplateFactory[] templates;

        /**
         * Creates an instance.
         * @param key The localization key.
         * @param templates The localization arguments.
         * @param cause The cause of this exception.
         */
        public CommandException(String key, I18N.TemplateFactory[] templates, @Nullable Throwable cause) {
            super(cause);
            this.key = key;
            this.templates = templates;
        }

        /**
         * Creates an instance.
         * @param key The localization key.
         * @param templates The localization arguments.
         */
        public CommandException(String key, I18N.TemplateFactory... templates) {
            this(key, templates, null);
        }

        /**
         * Gets the localization key.
         * @return The key.
         */
        public String key() { return key; }

        /**
         * Gets the localization arguments.
         * @return The arguments.
         */
        public I18N.TemplateFactory[] args() { return templates; }
    }

    /** The plugin that this command is registered under. */
    protected final P plugin;
    /** The plugin's localizer. */
    protected final I18N i18n;
    /** The underlying command manager. */
    protected final PaperCommandManager<CommandSender> manager;
    /** The help command builder. */
    protected final MinecraftHelp<CommandSender> help;
    /** The factory for caption messages. */
    protected final BiFunction<Caption, CommandSender, String> captionLocalizer;
    /** The writable version of the caption registry. */
    protected final FactoryDelegatingCaptionRegistry<CommandSender> captions;
    /** The exception handler. */
    protected final MinecraftExceptionHandler<CommandSender> exceptionHandler;
    /** The name of the root command. */
    protected final String rootName;
    /** The name of the root command. */
    protected final Command.Builder<CommandSender> root;

    /**
     * Creates an instance.
     * @param plugin The plugin this command is registered under.
     * @param rootName The name of the root command.
     * @param rootFactory A factory for the root command.
     * @throws Exception If an error occurred when making the command manager.
     */
    public BaseCommand(P plugin, String rootName, BiFunction<PaperCommandManager<CommandSender>, String, Command.Builder<CommandSender>> rootFactory) throws Exception {
        this.plugin = plugin;
        i18n = plugin.i18n;
        manager = new PaperCommandManager<>(plugin,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(), Function.identity());
        manager.registerBrigadier();
        manager.registerAsynchronousCompletions();

        this.rootName = rootName;
        help = new MinecraftHelp<>("/%s help".formatted(rootName), s -> s, manager);

        captionLocalizer = (cap, snd) ->
            PlainTextComponentSerializer.plainText().serialize(i18n.line(locale(snd), ERROR_CAPTION + "." + cap.getKey()));

        if (manager.getCaptionRegistry() instanceof FactoryDelegatingCaptionRegistry<CommandSender> captions) {
            this.captions = captions;
            captions.registerMessageFactory(DurationArgument.ARGUMENT_PARSE_FAILURE_DURATION, captionLocalizer);
            captions.registerMessageFactory(KeyArgument.ARGUMENT_PARSE_FAILURE_KEY, captionLocalizer);
        } else
            captions = null;

        exceptionHandler = new MinecraftExceptionHandler<CommandSender>()
            .withArgumentParsingHandler()
            .withInvalidSenderHandler()
            .withInvalidSyntaxHandler()
            .withNoPermissionHandler()
            .withCommandExecutionHandler()
            .withDecorator(msg -> i18n.line(plugin.defaultLocale(), ERROR_COMMAND,
                c -> c.of("message", msg)));
        exceptionHandler.apply(manager, s -> s);

        root = rootFactory.apply(manager, rootName);
        manager.command(root
            .literal("help", ArgumentDescription.of("Lists help information."))
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler(ctx -> help.queryCommands(ctx.getOrDefault("query", ""), ctx.getSender()));
        manager.command(root
            .literal("version", ArgumentDescription.of("Gets version information."))
            .handler(c -> handle(c, this::version)));
        manager.command(root
            .literal("reload", ArgumentDescription.of("Reloads all plugin data."))
            .permission("%s.command.reload".formatted(rootName))
            .handler(c -> handle(c, this::reload)));
        manager.command(root
            .literal("setting", ArgumentDescription.of("Gets a value from the settings file."))
            .argument(StringArgument.optional("path", StringArgument.StringMode.QUOTED), ArgumentDescription.of("The path to the node."))
            .flag(CommandFlag.newBuilder("comments")
                .withAliases("c")
                .withDescription(ArgumentDescription.of("Displays comments.")))
            .permission("%s.command.setting".formatted(rootName))
            .handler(c -> handle(c, this::setting)));
    }

    /**
     * Gets the plugin this is registered under.
     * @return The plugin.
     */
    public P plugin() { return plugin; }

    /**
     * Gets the underlying command manager.
     * @return The manager.
     */
    public PaperCommandManager<CommandSender> manager() { return manager; }

    /**
     * Gets the help command builder.
     * @return The help command builder.
     */
    public MinecraftHelp<CommandSender> help() { return help; }

    /**
     * Gets the name of the root command.
     * @return The root name.
     */
    public String rootName() { return rootName; }

    /**
     * Gets the root command builder.
     * @return The root command builder.
     */
    public Command.Builder<CommandSender> root() { return root; }

    /**
     * Gets the locale of a command sender.
     * @param sender The sender.
     * @return The locale.
     * @see BasePlugin#locale(CommandSender)
     */
    protected Locale locale(CommandSender sender) { return plugin().locale(sender); }

    /**
     * Returns a player if the sender is a player, otherwise null.
     * @param sender The sender.
     * @return The player, or null.
     */
    protected Player player(CommandSender sender) { return sender instanceof Player player ? player : null; }

    /** A command handler, with pre-determined slots. */
    protected interface CommandHandler {
        /**
         * Handles a command.
         * @param ctx The command context.
         * @param sender The command sender.
         * @param locale The locale of the sender.
         * @param pSender The sender as a player, if they are a player, otherwise null.
         */
        void handle(CommandContext<CommandSender> ctx, CommandSender sender, Locale locale, @Nullable Player pSender);
    }

    /**
     * Handles a command by using a {@link CommandHandler}.
     * @param ctx The command context.
     * @param handler The handler.
     */
    protected void handle(CommandContext<CommandSender> ctx, CommandHandler handler) {
        CommandSender sender = ctx.getSender();
        Locale locale = locale(sender);
        try {
            handler.handle(ctx, sender, locale, player(sender));
        } catch (CommandException e) {
            sendError(e, locale, sender);
        }
    }

    /**
     * Sends a message to an audience, prefixing with the plugin prefix using {@link BasePlugin#sendMessage(Audience, Locale, Component)}.
     * @param audience The audience.
     * @param locale The locale.
     * @param lines The lines to send.
     */
    protected void send(Audience audience, Locale locale, List<Component> lines) {
        for (var line : lines) {
            plugin.sendMessage(audience, locale, line);
        }
    }

    /**
     * Sends a message to an audience, localizing using the localizer.
     * @param audience The audience.
     * @param locale The locale.
     * @param key The localization key.
     * @param templates The localization placeholder templates.
     */
    protected void send(Audience audience, Locale locale, String key, I18N.TemplateFactory... templates) {
        send(audience, locale, i18n.lines(locale, key, templates));
    }

    /**
     * Formats and sends a {@link CommandException} to an audience.
     * @param error The error.
     * @param locale The locale.
     * @param audience The audience.
     */
    protected void sendError(CommandException error, Locale locale, Audience audience) {
        send(audience, locale, error.key, error.templates);
        if (error.getCause() != null) {
            for (Throwable cur = error.getCause(); cur != null; cur = cur.getCause()) {
                String type = cur.getClass().getSimpleName();
                String message = cur.getMessage();
                send(audience, locale, message == null
                    ? i18n.lines(locale, ERROR_EXCEPTION_NO_MESSAGE,
                        c -> c.of("type", type))
                    : i18n.lines(locale, ERROR_EXCEPTION_MESSAGE,
                        c -> c.of("type", type),
                        c -> c.of("message", message))
                );
            }
        }
    }

    /**
     * Creates an error, which is caught by {@link #handle(CommandContext, CommandHandler)}.
     * @param key The error localization key.
     * @param cause The cause of the error. Stack trace will be localized in a user-friendly manner.
     * @param args The localization arguments.
     * @return The exception.
     */
    protected static CommandException error(String key, Throwable cause, I18N.TemplateFactory... args) {
        return new CommandException(key, args, cause);
    }

    /**
     * Creates an error, which is caught by {@link #handle(CommandContext, CommandHandler)}.
     * @param key The error localization key.
     * @param args The localization arguments.
     * @return The exception.
     */
    protected static CommandException error(String key, I18N.TemplateFactory... args) {
        return new CommandException(key, args);
    }

    /**
     * Gets an argument from a command context that has a default value based on if the sender is a player.
     * @param ctx The context.
     * @param key The argument key.
     * @param pSender The player sender, or null if the sender is not a player.
     * @param ifPlayer The function to create a {@link T} if the sender is a player.
     * @param <T> The type of argument.
     * @return The argument.
     * @throws CommandException If there was no value, and the default value was null or there was no player sender.
     */
    protected <T> T defaultedArg(CommandContext<CommandSender> ctx, String key, @Nullable Player pSender, Function<Player, T> ifPlayer) throws CommandException {
        return ctx.<T>getOptional(key).orElseGet(() -> {
            T result = pSender == null ? null : ifPlayer.apply(pSender);
            if (result == null)
                throw error(ERROR_NO_ARG,
                    c -> c.of("arg", key));
            return result;
        });
    }

    /**
     * Gets a list of targets based on a command argument. If no targets are specified, throws an error.
     * @param ctx The context.
     * @param key The argument key.
     * @param pSender The player sender, or null if the sender is not a player.
     * @return The players.
     * @throws CommandException If there were no targets selected.
     */
    protected List<Player> targets(CommandContext<CommandSender> ctx, String key, @Nullable Player pSender) throws CommandException {
        List<Player> targets = defaultedArg(ctx, key, pSender,
            p -> new MultiplePlayerSelector("", Collections.singletonList(p))).getPlayers();
        if (targets.isEmpty())
            throw error(ERROR_NO_TARGETS);
        return targets;
    }


    /**
     * Command for {@code version}.
     * @param ctx The command context.
     * @param sender The command sender.
     * @param locale The locale of the sender.
     * @param pSender The sender as a player, if they are a player, otherwise null.
     */
    protected void version(CommandContext<CommandSender> ctx, CommandSender sender, Locale locale, Player pSender) {
        PluginDescriptionFile desc = plugin.getDescription();
        send(sender, locale, COMMAND_VERSION,
            c -> c.of("name", desc.getName()),
            c -> c.of("version", desc.getVersion()),
            c -> c.of("authors", String.join(", ", desc.getAuthors())));
    }

    /**
     * Command for {@code reload}.
     * @param ctx The command context.
     * @param sender The command sender.
     * @param locale The locale of the sender.
     * @param pSender The sender as a player, if they are a player, otherwise null.
     */
    protected void reload(CommandContext<CommandSender> ctx, CommandSender sender, Locale locale, Player pSender) {
        send(sender, locale, COMMAND_RELOAD_START);
        plugin.reload();
        send(sender, locale, COMMAND_RELOAD_END);
    }

    /**
     * Command for {@code setting}.
     * @param ctx The command context.
     * @param sender The command sender.
     * @param locale The locale of the sender.
     * @param pSender The sender as a player, if they are a player, otherwise null.
     */
    protected void setting(CommandContext<CommandSender> ctx, CommandSender sender, Locale locale, Player pSender) {
        Settings settings = plugin.settings;
        String path = ctx.getOrDefault("path", ".");
        //noinspection ConstantConditions
        NodePath nodePath = NodePath.of(path.split("\\."));

        ConfigurationNode node = settings.root().node(nodePath);
        if (node.virtual())
            throw error(ERROR_NO_NODE_VALUE,
                c -> c.of("path", nodePath.toString()));

        if (node.parent() != null) {
            ConfigurationNode value = node;
            node = plugin.loaderBuilder().build().createNode();
            node.node(value.key()).from(value);
        }
        List<Component> lines = ConfigurationNodes.render(node, ConfigurationNodes.RenderOptions.DEFAULT, ctx.flags().isPresent("comments"));

        for (var line : lines) {
            send(sender, locale, COMMAND_SETTING,
                c -> c.of("line", line));
        }
    }
}
