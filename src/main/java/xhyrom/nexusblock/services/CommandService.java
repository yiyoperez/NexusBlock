package xhyrom.nexusblock.services;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.commands.CreateNexusCommand;
import xhyrom.nexusblock.commands.DeleteNexusCommand;
import xhyrom.nexusblock.commands.InfoNexusCommand;
import xhyrom.nexusblock.commands.ListNexusCommand;
import xhyrom.nexusblock.commands.NexusBlockCommand;
import xhyrom.nexusblock.commands.SetLocationCommand;
import xhyrom.nexusblock.commands.TeleportCommand;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

        // Main Command
        commandSet.add(new NexusBlockCommand(plugin));
        // Sub Commands
        commandSet.add(new TeleportCommand(plugin));
        commandSet.add(new ListNexusCommand(plugin));
        commandSet.add(new InfoNexusCommand(plugin));
        commandSet.add(new SetLocationCommand(plugin));
        commandSet.add(new CreateNexusCommand(plugin));
        commandSet.add(new DeleteNexusCommand(plugin));

        commandSet.forEach(commandManager::registerCommand);
    }

    public void finish() {
        if (commandManager == null) return;
        if (!commandSet.isEmpty()) {
            commandSet.forEach(commandManager::unregisterCommand);
        }
    }

    private void registerSuggestions() {
        logger.info("Registering suggestions.");
        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) ->
                Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()));
        commandManager.registerSuggestion(SuggestionKey.of("available-blocks"), (sender, context) ->
                plugin.getNexusManager()
                        .getNexusBlocks()
                        .stream()
                        .map(Nexus::getId)
                        .collect(Collectors.toList()));
        commandManager.registerSuggestion(SuggestionKey.of("block-materials"), (sender, context) ->
                Arrays.stream(Material.values())
                        .filter(material -> material.isBlock() && material.isSolid())
                        .map(Enum::name)
                        .collect(Collectors.toList()));
    }

    private void registerTranslations() {
        logger.info("Registering command manager translation messages.");
        // Bukkit message keys
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) ->
                messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.NO_PERMISSION", new Placeholder("%command%", context.getCommand())));

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) ->
                messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.ONLY_PLAYERS"));

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) ->
                messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.ONLY_CONSOLE"));

        // Default message keys
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) ->
                messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.INVALID_SUB_COMMAND", new Placeholder("%sub-command%", context.getSubCommand())));

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            messageHandler.sendMessage(sender, "COMMAND_MANAGER.USAGE_HEADER");
            String usagePrefix = messageHandler.getMessage(sender, "COMMAND_MANAGER.USAGE_PREFIX");

            String command = context.getCommand();
            String subCommand = context.getSubCommand();
            String usageTarget = subCommand.equals("TH_DEFAULT") ? command.toUpperCase() : subCommand.toUpperCase();

            String usage = messageHandler.getMessage(sender, "COMMAND_MANAGER.USAGE." + usageTarget,
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
