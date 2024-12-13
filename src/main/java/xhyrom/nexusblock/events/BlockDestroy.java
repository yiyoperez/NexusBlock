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

import java.util.Optional;

public class BlockDestroy implements Listener {

    private final NexusManager manager;
    private final HologramManager hologramManager;

    public BlockDestroy(NexusBlock plugin) {
        this.manager = plugin.getNexusManager();
        this.hologramManager = plugin.getHologramManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNexusDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        if (manager.getNexusBlocks().isEmpty()) return;

        Optional<Nexus> nexusBlock = manager.getNexusBlocks()
                .stream()
                // Filter out nexus blocks with no location set.
                .filter(nexus -> nexus.getLocationConfig().getLocation() != null)
                // Check if broken block location is the same as nexus block.
                .filter(nexus -> nexus.getLocationConfig().getLocation().equals(blockLocation))
                .findAny();

        if (nexusBlock.isPresent()) {
            Nexus nexus = nexusBlock.get();
            event.setCancelled(true);

            if (player.hasPermission("nexusblock.admin.break") && player.isSneaking()) {
                hologramManager.deleteHologram(nexus);
                nexus.getHologramConfig().setHologram(null);
                block.setType(Material.AIR);
                nexus.getLocationConfig().resetLocationConfig();
                return;
            }

            if (block.getType() != Material.BEDROCK)
                manager.handleBreakActions(player, nexus);
        }
    }
}
