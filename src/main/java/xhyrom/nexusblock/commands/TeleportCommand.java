package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

@Command(name = "nexusblock teleport")
public class TeleportCommand {

    private final NexusBlock plugin;

    public TeleportCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.teleport")
    @Description("Teleport to nexus block.")
    public void teleportCommand(@Context Player player, @Arg("nexusName") @Key("available-blocks") String nexusName) {
        NexusManager nexusManager = plugin.getNexusManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        Nexus nexus = nexusManager.getNexus(nexusName);
        Placeholder placeholder = new Placeholder("%nexusName%", nexusName);
        if (nexus == null) {
            messageHandler.sendMessage(player, "NEXUS.DOES_NOT_EXIST", placeholder);
            return;
        }

        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        if (locationConfig.getLocation() == null) {
            messageHandler.sendMessage(player, "NEXUS.LOCATION_NOT_FOUND", placeholder);
            return;
        }

        player.teleport(locationConfig.getLocation().add(0D, 0.5D, 0D));
        messageHandler.sendMessage(player, "NEXUS.TELEPORTED", placeholder);
    }
}
