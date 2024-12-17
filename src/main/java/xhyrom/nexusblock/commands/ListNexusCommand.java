package xhyrom.nexusblock.commands;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;

import java.util.StringJoiner;


@Command(name = "nexusblock list")
public class ListNexusCommand {

    private final NexusBlock plugin;

    public ListNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.list")
    public void listCommand(@Context CommandSender sender) {
        NexusManager nexusManager = plugin.getNexusManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        if (nexusManager.getNexusBlocks().isEmpty()) {
            messageHandler.sendMessage(sender, "NEXUS.EMPTY_LIST");
            return;
        }

        Section listFormatSection = plugin.getLang().getSection("NEXUS.LIST-FORMAT");

        String enabled = listFormatSection.getString("ENABLED");
        String disabled = listFormatSection.getString("DISABLED");
        String prefix = listFormatSection.getString("PREFIX");
        String delimiter = listFormatSection.getString("DELIMITER");
        String suffix = listFormatSection.getString("SUFFIX");

        StringJoiner joiner = new StringJoiner(delimiter, prefix, suffix);
        nexusManager.getNexusBlocks().forEach(nexus -> joiner.add((nexus.isEnabled() ? enabled : disabled) + nexus.getId()));

        messageHandler.sendManualMessage(sender, joiner.toString());
    }

}
