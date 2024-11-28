package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.utils.Placeholder;

public class DeleteNexusCommand extends NexusBlockCommand {

    public DeleteNexusCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand("delete")
    @Permission("nexusblock.command.delete")
    @Description("Delete a nexus block.")
    public void deleteNexusCommand(CommandSender sender, @Suggestion("available-blocks") String nexusName) {
        NexusManager nexusManager = getNexusManager();
        HologramManager hologramManager = getHologramManager();
        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            getMessageHandler().sendMessage(sender, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
            return;
        }

        hologramManager.deleteHologram(nexus);
        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        if (locationConfig.getLocation() != null) {
            Block block = locationConfig.getLocation().getBlock();
            block.setType(Material.AIR);
        }
        nexusManager.deleteNexus(nexus);

        getMessageHandler().sendMessage(sender, "NEXUS.DELETED", new Placeholder("%nexusName%", nexusName));
    }
}
