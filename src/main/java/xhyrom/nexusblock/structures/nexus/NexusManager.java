package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.api.events.PlayerDestroyNexus;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusHealthConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusRewardsConfig;
import xhyrom.nexusblock.utils.Placeholder;
import xhyrom.nexusblock.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NexusManager {

    private final NexusBlock plugin;
    private final Set<Nexus> nexusBlocks = new HashSet<>();

    public NexusManager(NexusBlock plugin) {
        this.plugin = plugin;
    }

    public void createNexusBlock(String nexusName, Material material) {
        nexusBlocks.add(new Nexus(
                nexusName,
                material
        ));
    }

    public void createNexusBlock(Map<String, Object> map) {
        NexusConfig nexusConfig = new NexusConfig(map);

        if (Material.matchMaterial(nexusConfig.getBlockMaterial()) == null) {
            nexusConfig.setBlockMaterial("COAL_BLOCK");
            plugin.getLogger().severe("Invalid material in nexus " + nexusConfig.getId());
            plugin.getLogger().warning("Changed to COAL_BLOCK to keep the block working.");
            plugin.getLogger().warning("Please use a valid material and avoid modifying the config manually.");
        }

        Nexus nexus = new Nexus(
                nexusConfig.getId(),
                Material.matchMaterial(nexusConfig.getBlockMaterial()),
                nexusConfig.isEnabled(),
                nexusConfig.getRespawnInterval(),
                nexusConfig.getHologram(),
                nexusConfig.getLocation(),
                nexusConfig.getHealths(),
                nexusConfig.getRewards()
        );

        // Update values from temporal data if any.
        updateFromTemporalData(nexusConfig, nexus);

        // Add to list.
        nexusBlocks.add(nexus);
    }

    public void deleteNexus(Nexus nexus) {
        deleteFile(nexus.getId());
        nexusBlocks.remove(nexus);
    }

    public void deleteFile(String nexusName) {
        File folder = new File(plugin.getDataFolder(), "blocks");
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String id = file.getFileName().toString().split("\\.")[0];
                        if (id.equalsIgnoreCase(nexusName)) {
                            try {
                                plugin.getLogger().warning("Deleting file " + file.getFileName());
                                Files.deleteIfExists(file);
                            } catch (IOException ignored) {
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFromTemporalData(NexusConfig nexusConfig, Nexus nexus) {
        YamlDocument tempData = plugin.getTempData();
        Section section = tempData.getSection(nexusConfig.getId());
        if (section == null) return;

        if (tempData.contains(nexusConfig.getId())) {
            // Update stored damage.
            int damage = section.getInt("DAMAGE", 0);
            nexus.getHealthStatus().setDamage(damage);

            // Apply stored destroyers data to nexus if available.
            if (section.contains("DESTROYERS")) {
                section.getSection("DESTROYERS")
                        .getStringRouteMappedValues(false)
                        .replaceAll((d, v) -> nexus.getDestroyers().put(d, (Integer) v));
            }

            // Clear the section
            section.clear();
        }
    }

    public void setWorldBlock(Nexus nexus) {
        Location location = nexus.getLocationConfig().getLocation();
        if (location == null) {
            Bukkit.getLogger().warning("Could not set nexus block " + nexus.getId() + " since it doesn't have a location.");
            Bukkit.getLogger().warning("Please use /nexusblock setlocation ");
            Bukkit.getLogger().warning("to set your current eye location as nexus block location.");
            return;
        }

        location.getBlock().setType(nexus.getMaterial());
    }

    public void handleBreakActions(Player player, Nexus nexus) {
        HologramManager hologramManager = plugin.getHologramManager();
        NexusHealthConfig healthStatus = nexus.getHealthStatus();
        healthStatus.increaseDamage();

        nexus.getDestroyers().putIfAbsent(player.getName(), 0);
        nexus.getDestroyers().merge(player.getName(), 1, Integer::sum);

        hologramManager.updateHologram(nexus);
        if (healthStatus.getDamage() >= healthStatus.getMaximumHealth()) {
            destroy(player, nexus);
        }
    }

    public void destroy(Player player, Nexus nexus) {
        PlayerDestroyNexus event = new PlayerDestroyNexus(player, nexus);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        NexusLocationConfig locationConfig = nexus.getLocationConfig();
        World world = locationConfig.getWorld();
        Location location = locationConfig.getLocation();

        if (location != null) {
            world.strikeLightningEffect(location);

            Block block = location.getBlock();
            block.setType(Material.BEDROCK);
        }

        handleRewards(player, nexus);
        resetNexus(nexus, location);
    }

    private void handleRewards(Player player, Nexus nexus) {
        // Give reward to the one who finally broke the block.
        giveRewards(player.getName(), nexus);

        // Give rewards to top players
        List<String> destroyers = getSortedDestroyers(nexus);
        // Limit leaderboard players.
        int configLimit = plugin.getConfiguration().getInt("LEADERBOARD.LIMIT");
        // If off-limits set to 3.
        int limit = (configLimit <= 0 || configLimit > 5) ? 3 : configLimit;

        for (String destroyer : destroyers) {
            // Got all players from the limit so break the loop.
            if (limit <= 0) {
                break;
            }

            Player destroyerPlayer = Bukkit.getPlayer(destroyer);
            // Player not online.
            if (destroyerPlayer != null) {
                giveRewards(destroyer, nexus, destroyers.indexOf(destroyer) + 1);
            }
            limit--;
        }
    }

    private void resetNexus(Nexus nexus, Location location) {
        HologramManager hologramManager = plugin.getHologramManager();

        // So... this resets the nexus block.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block = location.getBlock();
            if (nexus.getLocationConfig().getLocation() == location) {
                block.setType(nexus.getMaterial());
            }

            nexus.getDestroyers().clear();
            nexus.getHealthStatus().setDamage(0);

            hologramManager.updateHologram(nexus, true);
        }, nexus.getRespawnDelay() * 20L);
    }

    private void giveRewards(String playerName, Nexus nexus) {
        NexusRewardsConfig rewardsConfig = nexus.getRewardsConfig();
        if (rewardsConfig.getDestroyerRewards().isEmpty()) return;

        runCommandRewards(playerName, nexus, rewardsConfig.getDestroyerRewards());
    }

    private void giveRewards(String playerName, Nexus nexus, int playerDestroys) {
        NexusRewardsConfig rewardsConfig = nexus.getRewardsConfig();

        if (rewardsConfig.getRewards().isEmpty()) return;
        if (!rewardsConfig.getRewards().containsKey(playerDestroys)) return;

        List<String> rewards = nexus.getRewardsConfig().getReward(playerDestroys);
        runCommandRewards(playerName, nexus, rewards);
    }

    private void runCommandRewards(String playerName, Nexus nexus, List<String> rewardsList) {
        for (String reward : rewardsList) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    StringUtils.replace(reward,
                            new Placeholder("%player%", playerName),
                            new Placeholder("%destroys%", nexus.getDestroyers().get(playerName))
                    )
            );
        }
    }

    public Nexus getNexus(String nexusName) {
        if (!existsNexusBlock(nexusName)) return null;

        for (Nexus nexusBlock : nexusBlocks) {
            if (nexusBlock
                    .getId()
                    .equalsIgnoreCase(nexusName)) {
                return nexusBlock;
            }
        }
        return null;
    }

    public boolean existsNexusBlock(String nexusName) {
        return nexusBlocks.stream().anyMatch(block -> block.getId().equalsIgnoreCase(nexusName));
    }

    public Set<Nexus> getNexusBlocks() {
        return nexusBlocks;
    }

    public List<String> getSortedDestroyers(Nexus nexus) {
        return nexus.getDestroyers()
                .keySet()
                .stream()
                .sorted(new ModuleComparator(nexus.getDestroyers()))
                .collect(Collectors.toList());
    }

    private static class ModuleComparator implements Comparator<String> {

        private final Map<String, Integer> destroys;

        public ModuleComparator(Map<String, Integer> destroys) {
            this.destroys = destroys;
        }

        @Override
        public int compare(String arg0, String arg1) {
            int destroys1 = this.destroys.get(arg0);
            int destroys2 = this.destroys.get(arg1);
            return Integer.compare(destroys2, destroys1);
        }

    }
}
