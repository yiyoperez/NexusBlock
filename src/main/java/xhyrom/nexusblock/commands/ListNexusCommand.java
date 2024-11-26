package xhyrom.nexusblock.commands;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;

import java.util.StringJoiner;


public class ListNexusCommand extends NexusBlockCommand {
    public ListNexusCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "list")
    @Permission("nexusblock.command.list")
    public void listCommand(CommandSender sender) {
        Section listFormatSection = getConfiguration().getSection("LIST-FORMAT");
        
        String enabled = listFormatSection.getString("ENABLED");
        String disabled = listFormatSection.getString("DISABLED");
        String prefix = listFormatSection.getString("PREFIX");
        String delimiter = listFormatSection.getString("DELIMITER");
        String suffix = listFormatSection.getString("SUFFIX");

        StringJoiner joiner = new StringJoiner(delimiter, prefix, suffix);
        getNexusManager().getNexusBlocks().forEach(nexus -> joiner.add((nexus.isEnabled() ? enabled : disabled) + nexus.getId()));

        getMessageHandler().sendManualMessage(sender, joiner.toString());
    }

}
