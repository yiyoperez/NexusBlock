package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;

import java.util.StringJoiner;


public class ListSubCommand extends NexusBlockCommand {
    public ListSubCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "list")
    @Permission("nexusblock.command.list")
    public void listCommand(CommandSender sender) {
        StringJoiner joiner = new StringJoiner(",");
        getNexusManager().getNexusBlocks().forEach(nexus -> joiner.add(nexus.getId()));

        getMessageHandler().sendManualMessage(sender, "Nexus disponibles:");
        getMessageHandler().sendManualMessage(sender, joiner.toString());
    }

}
