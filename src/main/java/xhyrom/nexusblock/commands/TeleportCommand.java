package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.utils.Placeholder;

public class TeleportCommand extends NexusBlockCommand {

    public TeleportCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "teleport")
    @Permission("nexusblock.command.teleport")
    @Description("Teleport to nexus block.")
    public void teleportCommand(Player player, @Suggestion("available-blocks") String nexusName) {
        NexusManager nexusManager = getNexusManager();
        Nexus nexus = nexusManager.getNexus(nexusName);

        Placeholder placeholder = new Placeholder("%nexusName%", nexusName);
        if (nexus == null) {
            getMessageHandler().sendMessage(player, "NEXUS.DOES_NOT_EXIST", placeholder);
            return;
        }

        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        if (locationConfig.getLocation() == null) {
            getMessageHandler().sendMessage(player, "NEXUS.LOCATION_NOT_FOUND", placeholder);
            return;
        }

        player.teleport(locationConfig.getLocation());
        getMessageHandler().sendMessage(player, "NEXUS.TELEPORTED", placeholder);
    }
}
