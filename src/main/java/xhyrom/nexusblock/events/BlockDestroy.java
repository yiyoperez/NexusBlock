package xhyrom.nexusblock.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;

public class BlockDestroy implements Listener {

    private final NexusManager manager;

    public BlockDestroy(NexusBlock nexusBlock) {
        this.manager = nexusBlock.getNexusManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNexusDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        if (manager.getNexusBlocks().isEmpty()) return;

        Nexus nexusBlock = manager.getNexusBlocks()
                .stream()
                // Filter out nexus blocks with no location set.
                .filter(nexus -> nexus.getLocationConfig().getLocation() != null)
                // Check if broken block location is the same as nexus block.
                .filter(nexus -> nexus.getLocationConfig().getLocation().equals(blockLocation))
                .findAny()
                .orElse(null);

        if (nexusBlock != null) {
            event.setCancelled(true);

            if (block.getType() != Material.BEDROCK)
                manager.handleBreakActions(player, nexusBlock);
        }
    }
}
