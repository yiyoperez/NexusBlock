package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

@Command(name = "nexusblock setlocation")
public class SetLocationCommand {

    private final NexusBlock plugin;

    public SetLocationCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.setlocation")
    @Description("Set nexusblock location where you currently are watching.")
    public void setLocationCommand(@Context Player player, @Arg @Key("available-blocks") String nexusName) {
        NexusManager nexusManager = plugin.getNexusManager();
        HologramManager hologramManager = plugin.getHologramManager();
        MessageHandler messageHandler = plugin.getMessageHandler();
        
        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            messageHandler.sendMessage(player, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
            return;
        }

        // Iteration to find block/location player is looking at.
        BlockIterator blockIterator = new BlockIterator(player, 10);
        Block lastBlock = blockIterator.next();

        while (blockIterator.hasNext()) {
            lastBlock = blockIterator.next();

            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }

        // Self-explanatory.
        Location lookingLocation = lastBlock.getLocation();

        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        if (locationConfig.getLocation() != null && locationConfig.getLocation().equals(lookingLocation)) {
            messageHandler.sendMessage(player, "NEXUS.SETLOCATION", new Placeholder("%nexusName%", nexusName));
            return;
        }

        if (nexusManager.isNexusLocation(lookingLocation)) {
            messageHandler.sendMessage(player, "NEXUS.LOCATION_OCCUPIED");
            return;
        }

        //Remove old block
        if (locationConfig.getLocation() != null) {
            Block block = locationConfig.getLocation().getBlock();
            if (!block.isEmpty()) {
                block.setType(Material.AIR);
            }
        }

        // Update nexus block location at config.
        nexus.getLocationConfig().setLocation(lookingLocation);

        // Setup create block at new location.
        nexusManager.setWorldBlock(nexus);

        // Handle holograms if enabled.
        if (plugin.getHologram() != null) {
            if (nexus.getHologramConfig().getHologram() == null) {
                // Create hologram if didn't have one.
                hologramManager.setupHologram(nexus);
            } else {
                // Get hologram offset.
                NexusHologramConfig hologramConfig = nexus.getHologramConfig();
                double offset = hologramConfig.getHologramOffset();

                // Update current hologram location if any.
                hologramManager.updateHologramLocation(nexus, lookingLocation.clone().add(0.5D, offset, 0.5D));
            }
        }

        // Send message to player notifying location update.
        messageHandler.sendMessage(player, "NEXUS.SETLOCATION", new Placeholder("%nexusName%", nexusName));
    }
}
