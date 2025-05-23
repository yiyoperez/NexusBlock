package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

@Command(name = "nexusblock delete")
public class DeleteNexusCommand {

    private final NexusBlock plugin;

    public DeleteNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.delete")
    @Description("Delete a nexus block.")
    public void deleteNexusCommand(@Context CommandSender sender, @Arg("nexusName") Nexus nexus) {
        NexusManager nexusManager = plugin.getNexusManager();
        HologramManager hologramManager = plugin.getHologramManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        if (locationConfig.getLocation() != null) {
            Block block = locationConfig.getLocation().getBlock();
            block.setType(Material.AIR);
        }

        messageHandler.sendMessage(sender, "NEXUS.DELETED", new Placeholder("%nexusName%", nexus.getId()));
        nexusManager.deleteNexus(nexus);
        hologramManager.deleteHologram(nexus);
    }
}
