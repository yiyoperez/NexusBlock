package xhyrom.nexusblock.services;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.commands.NexusBlockCommand;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommandService {

    private final Logger logger;

    private final NexusBlock plugin;
    private final MessageHandler messageHandler;
    private final Set<BaseCommand> commandSet = new HashSet<>();
    private final BukkitCommandManager<CommandSender> commandManager;

    public CommandService(NexusBlock plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.messageHandler = plugin.getMessageHandler();
        this.commandManager = BukkitCommandManager.create(plugin);
    }

    public void start() {
        registerSuggestions();
        registerTranslations();

        commandSet.add(new NexusBlockCommand(plugin));

        commandSet.forEach(command -> {
            logger.log(Level.INFO, "Trying to register command " + command.getClass().getSimpleName());
            commandManager.registerCommand(command);
        });
    }

    public void finish() {
        if (commandManager == null) return;
        if (!commandSet.isEmpty()) {
            commandSet.forEach(command -> {
                logger.info("Trying to un-register command " + command.getClass().getSimpleName());
                commandManager.unregisterCommand(command);
            });
        }
    }

    private void registerSuggestions() {
        logger.info("Registering suggestions.");
        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) ->
                Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()));
    }

    private void registerTranslations() {
        logger.info("Registering command manager translation messages.");
        // Bukkit message keys
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.NO_PERMISSION", new Placeholder("%command%", context.getCommand()));
        });

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.ONLY_PLAYERS");
        });

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.ONLY_CONSOLE");
        });

        // Default message keys
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.INVALID_SUB_COMMAND", new Placeholder("%sub-command%", context.getSubCommand()));
        });

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.USAGE_HEADER");
            String usagePrefix = messageHandler.getMessage(sender, "COMMAND_MANAGER.USAGE_PREFIX");
            String usage = messageHandler.getMessage(sender, "COMMAND_MANAGER.USAGE." + context.getCommand().toUpperCase(),
                    new Placeholder("%command%", context.getCommand()));

            messageHandler.sendManualMessage(sender, usagePrefix + usage);
        });

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            messageHandler.sendManualMessage(sender, context.getArgumentType().getName());
            switch (context.getArgumentType().getName()) {
                case "integer":
                    messageHandler.sendMessage(sender, "NOT_INTEGER", new Placeholder("%value%", context.getTypedArgument()));
                    break;
                case "double":
                    messageHandler.sendMessage(sender, "NOT_DOUBLE", new Placeholder("%value%", context.getTypedArgument()));
                    break;
            }
        });
    }
}
