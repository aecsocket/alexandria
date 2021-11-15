package com.gitlab.aecsocket.minecommons.paper.plugin;

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
import com.gitlab.aecsocket.minecommons.core.ConfigurationNodes;
import com.gitlab.aecsocket.minecommons.core.Settings;
import com.gitlab.aecsocket.minecommons.core.translation.Localizer;
import com.gitlab.aecsocket.minecommons.paper.command.DurationArgument;
import com.gitlab.aecsocket.minecommons.paper.command.KeyArgument;
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
    /** The prefix for chat keys. */
    public static final String PREFIX_CHAT = "chat";
    /** The prefix for chat command keys. */
    public static final String PREFIX_COMMAND = PREFIX_CHAT + ".command";
    /** The prefix for chat error keys. */
    public static final String PREFIX_ERROR = PREFIX_CHAT + ".error";

    /** The key for a generic command error. */
    public static final String KEY_COMMAND_ERROR = PREFIX_COMMAND + ".error";

    /**
     * An exception that represents a user-facing error.
     */
    protected static class CommandException extends RuntimeException {
        /** The localization key. */
        private final String key;
        /** The localization arguments. */
        private final Object[] args;

        /**
         * Creates an instance.
         * @param key The localization key.
         * @param args The localization arguments.
         * @param cause The cause of this exception.
         */
        public CommandException(String key, Object[] args, @Nullable Throwable cause) {
            super(cause);
            this.key = key;
            this.args = args;
        }

        /**
         * Creates an instance.
         * @param key The localization key. This is automatically prefixed with {@link #PREFIX_ERROR}.
         * @param args The localization arguments.
         */
        public CommandException(String key, Object[] args) {
            this(key, args, null);
        }

        /**
         * Gets the localization key. This is automatically prefixed with {@link #PREFIX_ERROR}.
         * @return The key.
         */
        public String key() { return key; }

        /**
         * Gets the localization arguments.
         * @return The arguments.
         */
        public Object[] args() { return args; }
    }

    /** The plugin that this command is registered under. */
    protected final P plugin;
    /** The plugin's localizer. */
    protected final Localizer lc;
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
        lc = plugin.localizer;
        manager = new PaperCommandManager<>(plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(), Function.identity());
        manager.registerBrigadier();
        manager.registerAsynchronousCompletions();

        this.rootName = rootName;
        help = new MinecraftHelp<>("/%s help".formatted(rootName), s -> s, manager);

        captionLocalizer = (cap, snd) ->
                PlainTextComponentSerializer.plainText().serialize(lc.safe(locale(snd), PREFIX_ERROR + ".caption." + cap.getKey()));

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
                .withDecorator(msg -> lc.get(plugin.defaultLocale(), KEY_COMMAND_ERROR,
                        "message", msg)
                        .orElseThrow(() -> new IllegalArgumentException("Could not get command error localization at " + KEY_COMMAND_ERROR)));
        exceptionHandler.apply(manager, s -> s);

        root = rootFactory.apply(manager, rootName);
        manager.command(root
                .literal("help", ArgumentDescription.of("Lists help information."))
                .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
                .handler(ctx -> {
                    //noinspection ConstantConditions
                    help.queryCommands(ctx.getOrDefault("query", ""), ctx.getSender());
                }));
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
     * Returns a player if the sender if a player, otherwise null.
     * @param sender The sender.
     * @return The player, or null.
     */
    protected Player player(CommandSender sender) { return sender instanceof Player ? (Player) sender : null; }

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
     * Formats and sends a {@link CommandException} to an Audience.
     * @param error The error.
     * @param locale The locale.
     * @param audience The audience.
     */
    protected void sendError(CommandException error, Locale locale, Audience audience) {
        lc.lines(locale, error.key, error.args)
                .ifPresent(m -> m.forEach(audience::sendMessage));
        if (error.getCause() != null) {
            for (Throwable cur = error.getCause(); cur != null; cur = cur.getCause()) {
                String type = cur.getClass().getSimpleName();
                String message = cur.getMessage();
                (message == null
                        ? lc.lines(locale, PREFIX_ERROR + ".exception.no_message",
                        "type", type)
                        : lc.lines(locale, PREFIX_ERROR + ".exception.message",
                        "type", type,
                        "message", message)
                ).ifPresent(m -> m.forEach(audience::sendMessage));
            }
        }
    }

    /**
     * Creates an error, which is caught by {@link #handle(CommandContext, CommandHandler)}.
     * @param key The error chat key, localized with prefix {@link #PREFIX_ERROR}.
     * @param cause The cause of the error. Stack trace will be localized in a user-friendly manner.
     * @param args The localization arguments.
     * @return The exception.
     */
    protected static CommandException error(String key, Throwable cause, Object... args) {
        return new CommandException(PREFIX_ERROR + "." + key, args, cause);
    }

    /**
     * Creates an error, which is caught by {@link #handle(CommandContext, CommandHandler)}.
     * @param key The error chat key, localized with prefix {@link #PREFIX_ERROR}.
     * @param args The localization arguments.
     * @return The exception.
     */
    protected static CommandException error(String key, Object... args) {
        return new CommandException(PREFIX_ERROR + "." + key, args);
    }

    /**
     * Sends a message to a command sender.
     * @param sender The sender.
     * @param locale The locale to generate for.
     * @param key The chat key, localized with prefix {@link #PREFIX_COMMAND}
     * @param args The localization arguments.
     */
    protected void send(CommandSender sender, Locale locale, String key, Object... args) {
        lc.lines(locale, PREFIX_COMMAND + "." + key, args).ifPresent(m -> m.forEach(sender::sendMessage));
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
                throw error("no_arg", "arg", key);
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
        if (targets.size() == 0)
            throw error("no_targets");
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
        send(sender, locale, "version",
                "name", desc.getName(),
                "version", desc.getVersion(),
                "authors", String.join(", ", desc.getAuthors()));
    }

    /**
     * Command for {@code reload}.
     * @param ctx The command context.
     * @param sender The command sender.
     * @param locale The locale of the sender.
     * @param pSender The sender as a player, if they are a player, otherwise null.
     */
    protected void reload(CommandContext<CommandSender> ctx, CommandSender sender, Locale locale, Player pSender) {
        send(sender, locale, "reload.start");
        plugin.reload();
        send(sender, locale, "reload.end");
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
            throw error("no_node_value",
                    "path", nodePath.toString());

        if (node.parent() != null) {
            ConfigurationNode value = node;
            node = plugin.loaderBuilder().build().createNode();
            node.node(value.key()).from(value);
        }
        List<Component> lines = ConfigurationNodes.render(node, ConfigurationNodes.RenderOptions.DEFAULT, ctx.flags().isPresent("comments"));

        for (var line : lines) {
            send(sender, locale, "setting",
                    "line", line);
        }
    }
}
