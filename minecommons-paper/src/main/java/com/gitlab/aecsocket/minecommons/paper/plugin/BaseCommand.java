package com.gitlab.aecsocket.minecommons.paper.plugin;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for using a Cloud {@link PaperCommandManager}.
 */
public class BaseCommand<P extends BasePlugin<P>> {
    protected final P plugin;
    protected final PaperCommandManager<CommandSender> manager;
    protected final MinecraftHelp<CommandSender> help;
    protected final String rootName;
    protected final Command.Builder<CommandSender> root;

    public BaseCommand(P plugin, String rootName, BiFunction<PaperCommandManager<CommandSender>, String, Command.Builder<CommandSender>> rootFactory) throws Exception {
        this.plugin = plugin;
        manager = new PaperCommandManager<>(plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(), Function.identity());
        manager.registerBrigadier();
        manager.registerAsynchronousCompletions();

        this.rootName = rootName;
        help = new MinecraftHelp<>("/%s help".formatted(rootName), s -> s, manager);
        root = rootFactory.apply(manager, rootName);

        manager.command(root
                .literal("help", ArgumentDescription.of("Lists help information."))
                .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
                .handler(ctx -> help.queryCommands(ctx.getOrDefault("query", ""), ctx.getSender())));
        manager.command(root
                .literal("version", ArgumentDescription.of("Gets version information."))
                .handler(this::version));
        manager.command(root
                .literal("reload", ArgumentDescription.of("Reloads all plugin data."))
                .permission("%s.command.reload".formatted(rootName))
                .handler(this::reload));
    }

    public P plugin() { return plugin; }
    public PaperCommandManager<CommandSender> manager() { return manager; }
    public MinecraftHelp<CommandSender> help() { return help; }
    public String rootName() { return rootName; }
    public Command.Builder<CommandSender> root() { return root; }

    /**
     * Gets the locale of a command sender.
     * @param sender The sender.
     * @return The locale.
     * @see BasePlugin#locale(CommandSender)
     */
    protected Locale locale(CommandSender sender) { return plugin().locale(sender); }

    /**
     * Localizes a key and arguments into a component, using a specific locale.
     * @param locale The locale to localize for.
     * @param key The key of the localization value.
     * @param args The arguments.
     * @return The localized component.
     * @see BasePlugin#localize(Locale, String, Object...)
     */
    protected Component localize(Locale locale, String key, Object... args) { return plugin().localize(locale, key, args); }

    /**
     * Returns a player if the sender if a player, otherwise null.
     * @param sender The sender.
     * @return The player, or null.
     */
    protected Player player(CommandSender sender) { return sender instanceof Player ? (Player) sender : null; }

    protected void version(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        Locale locale = locale(sender);
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(localize(locale, "chat.version",
                "name", desc.getName(),
                "version", desc.getVersion(),
                "authors", String.join(", ", desc.getAuthors())));
    }

    protected void reload(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        Locale locale = locale(sender);
        sender.sendMessage(localize(locale, "chat.reload.start"));
        plugin.reload();
        sender.sendMessage(localize(locale, "chat.reload.end"));
    }
}
