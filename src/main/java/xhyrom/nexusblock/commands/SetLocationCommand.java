package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
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
import xhyrom.nexusblock.utils.Placeholder;

public class SetLocationCommand extends NexusBlockCommand {

    private final NexusBlock plugin;

    public SetLocationCommand(NexusBlock plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SubCommand(value = "setlocation")
    @Permission("nexusblock.command.setlocation")
    @Description("Set nexusblock location where you currently are watching.")
    public void setLocationCommand(Player player, @Suggestion("available-blocks") String nexusName) {
        NexusManager nexusManager = getNexusManager();
        HologramManager hologramManager = getHologramManager();
        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            getMessageHandler().sendMessage(player, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
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
            getMessageHandler().sendMessage(player, "NEXUS.SETLOCATION", new Placeholder("%nexusName%", nexusName));
            return;
        }

        if (nexusManager.isNexusLocation(lookingLocation)) {
            getMessageHandler().sendMessage(player, "NEXUS.LOCATION_OCCUPIED");
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
        getMessageHandler().sendMessage(player, "NEXUS.SETLOCATION", new Placeholder("%nexusName%", nexusName));
    }
}
