package xhyrom.nexusblock.loaders;

import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Material;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.commands.CreateNexusCommand;
import xhyrom.nexusblock.commands.DeleteNexusCommand;
import xhyrom.nexusblock.commands.EditNexusCommand;
import xhyrom.nexusblock.commands.InfoNexusCommand;
import xhyrom.nexusblock.commands.ListNexusCommand;
import xhyrom.nexusblock.commands.NexusBlockCommand;
import xhyrom.nexusblock.commands.SetLocationCommand;
import xhyrom.nexusblock.commands.TeleportCommand;
import xhyrom.nexusblock.commands.handlers.NexusInvalidUsageHandler;
import xhyrom.nexusblock.commands.handlers.NexusMissingPermissionHandler;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandLoader {

    private final NexusBlock plugin;
    private final MessageHandler messageHandler;

    public CommandLoader(NexusBlock plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
    }

    public void load() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("nexusblock")
                        .nativePermissions(false)
                )

                .commands(
                        new TeleportCommand(plugin),
                        new EditNexusCommand(plugin),
                        new InfoNexusCommand(plugin),
                        new ListNexusCommand(plugin),
                        new NexusBlockCommand(plugin),
                        new CreateNexusCommand(plugin),
                        new DeleteNexusCommand(plugin),
                        new SetLocationCommand(plugin)
                )

                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, input -> messageHandler.getRawMessage("PLAYER_NOT_FOUND", new Placeholder("%player%", input)))
                .message(LiteBukkitMessages.PLAYER_ONLY, messageHandler.getRawMessage("COMMAND_MANAGER.TRANSLATIONS.ONLY_PLAYER"))

                .schematicGenerator(SchematicFormat.angleBrackets())

                .invalidUsage(new NexusInvalidUsageHandler(messageHandler))
                .missingPermission(new NexusMissingPermissionHandler(messageHandler))

                // Argument suggestions.
                .argumentSuggestion(String.class, ArgumentKey.of("available-blocks"),
                        SuggestionResult.of(plugin.getNexusManager()
                                .getNexusBlocks()
                                .stream()
                                .map(Nexus::getId)
                                .collect(Collectors.toList())))
                .argumentSuggestion(String.class, ArgumentKey.of("block-materials"),
                        SuggestionResult.of(Arrays.stream(Material.values())
                                .filter(material -> material.isBlock() && material.isSolid())
                                .map(Enum::name)
                                .collect(Collectors.toList())))

                .build();
    }
}
