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
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;

public class BlockDestroy implements Listener {

    private final NexusManager manager;
    private final HologramManager hologramManager;

    public BlockDestroy(NexusBlock plugin) {
        this.manager = plugin.getNexusManager();
        this.hologramManager = plugin.getHologramManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNexusDestroy(BlockBreakEvent event) {
        if (manager.getNexusBlocks().isEmpty()) return;

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        // Check if broken block location is the same as nexus block.
        for (Nexus nexus : manager.getNexusBlocks()) {
            if (!nexus.getLocationConfig().getLocation().equals(blockLocation)) continue;
            event.setCancelled(true);

            if (!nexus.isEnabled()) continue;

            Player player = event.getPlayer();
            if (player.hasPermission("nexusblock.admin.break") && player.isSneaking()) {
                hologramManager.deleteHologram(nexus);
                nexus.getHologramConfig().setHologram(null);
                block.setType(Material.AIR);
                nexus.getLocationConfig().resetLocationConfig();
                return;
            }

            if (block.getType() != Material.BEDROCK) {
                manager.handleBreakActions(player, nexus);
                break;
            }
        }
    }
}
