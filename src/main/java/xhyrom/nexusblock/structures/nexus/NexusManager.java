package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.api.events.PlayerDestroyNexus;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramInterface;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigHealthStatus;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigHologram;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigRewards;
import xhyrom.nexusblock.utils.Placeholder;
import xhyrom.nexusblock.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NexusManager {

    private final NexusBlock plugin;
    private final List<Nexus> nexusBlocks = new ArrayList<>();

    public NexusManager(NexusBlock plugin) {
        this.plugin = plugin;
    }

    public void createNexusBlock(Map<String, Object> map) {
        NexusConfig nexusConfig = new NexusConfig(map);

        Material material = Material.getMaterial(nexusConfig.getMaterial());
        if (material == null) {
            plugin.getLogger().warning("Invalid material in nexus " + nexusConfig.getId());
            return;
        }

        Nexus nexus = new Nexus(
                nexusConfig.getId(),
                material,
                nexusConfig.getRespawn(),
                nexusConfig.getHologram(),
                nexusConfig.getLocation(),
                nexusConfig.getHealths(),
                nexusConfig.getRewards()
        );

        // Update values from temporal data if any.
        updateFromTemporalData(nexusConfig, nexus);

        // Add to list.
        nexusBlocks.add(nexus);

        // Create block and hologram.
        nexus.getLocation().getBlock().setType(nexus.getMaterial());
        updateHologram(nexus, true);
    }

    private void updateFromTemporalData(NexusConfig nexusConfig, Nexus nexus) {
        YamlDocument tempData = plugin.getTempData();
        if (tempData.contains(nexusConfig.getId())) {
            Section section = tempData.getSection(nexusConfig.getId());
            if (section == null) return;

            // Update stored damage.
            int damage = section.getInt("DAMAGE", 0);
            nexus.getHealthStatus().setDamage(damage);
            // Gets stored data and apply to nexus.
            if (section.contains("DESTROYERS")) {
                section.getSection("DESTROYERS")
                        .getStringRouteMappedValues(false)
                        .replaceAll((d, v) -> nexus.getDestroyers().put(d, (Integer) v));
            }
            section.clear();
        }
    }

    public void onHit(Player player, Nexus nexus) {
        NexusConfigHealthStatus healthStatus = nexus.getHealthStatus();
        healthStatus.increaseDamage();

        nexus.getDestroyers().putIfAbsent(player.getName(), 0);
        nexus.getDestroyers().merge(player.getName(), 1, Integer::sum);

        updateHologram(nexus, false);
        if (healthStatus.getDamage() >= healthStatus.getMaximumHealth()) {
            destroy(player, nexus);
        }
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
            if (destroys1 < destroys2) {
                return 1;
            }
            if (destroys1 > destroys2) {
                return -1;
            }
            return 0;
        }

    }

    public void destroy(Player player, Nexus nexus) {
        PlayerDestroyNexus event = new PlayerDestroyNexus(player, nexus);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        nexus.getWorld().strikeLightningEffect(nexus.getLocation());

        Block block = nexus.getLocation().getBlock();
        block.setType(Material.BEDROCK);

        // Give rewards to top players
        List<String> destroyers = nexus.getDestroyers()
                .keySet()
                .stream()
                .sorted(new ModuleComparator(nexus.getDestroyers()))
                .collect(Collectors.toList());

        // Give reward to the one who finally broke the block.
        giveRewards(player.getName(), nexus);

        // Give rewards to other destroyers.
        for (int i = 0; i < destroyers.size(); i++) {
            // Limit leaderboard players.
            int configLimit = plugin.getConfiguration().getInt("LEADERBOARD.LIMIT");
            // If off-limits set to 3.
            int limit = (configLimit <= 0 || configLimit > 5) ? 3 : plugin.getConfiguration().getInt("LEADERBOARD.LIMIT");

            // Player is beyond nexus limits.
            if (i > limit) return;

            String playerName = destroyers.get(i);
            // Player not online.
            if (Bukkit.getPlayer(playerName) == null) return;

            giveRewards(playerName, nexus, i + 1);
        }

        // So... this resets the nexus block.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block1 = nexus.getLocation().getBlock();
            block1.setType(nexus.getMaterial());

            nexus.getHealthStatus().setDamage(0);
            updateHologramHealthPositions(nexus);

            nexus.getDestroyers().clear();
            updateHologramPositions(nexus, true);
        }, nexus.getRespawnDelay() * 20L);
    }

    private void giveRewards(String playerName, Nexus nexus) {
        NexusConfigRewards rewardsConfig = nexus.getRewardsConfig();
        if (rewardsConfig.getDestroyerRewards().isEmpty()) return;

        for (String reward : rewardsConfig.getDestroyerRewards()) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    StringUtils.replace(reward,
                            new Placeholder("%player%", playerName),
                            new Placeholder("%destroys%", nexus.getDestroyers().get(playerName))
                    )
            );
        }
    }

    private void giveRewards(String playerName, Nexus nexus, int playerDestroys) {
        NexusConfigRewards rewardsConfig = nexus.getRewardsConfig();

        if (rewardsConfig.getRewards().isEmpty()) return;
        if (!rewardsConfig.getRewards().containsKey(playerDestroys)) return;

        List<String> rewards = nexus.getRewardsConfig().getReward(playerDestroys);
        rewards.forEach(reward ->
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        StringUtils.replace(reward,
                                new Placeholder("%player%", playerName),
                                new Placeholder("%destroys%", nexus.getDestroyers().get(playerName))
                        )
                ));
    }

    private void updateHologram(Nexus nexus, boolean setup) {
        HologramInterface hologramInterface = plugin.getHologram();

        if (hologramInterface == null) return;
        if (setup) {
            Location location = nexus.getLocation();
            NexusConfigHologram hologramConfig = nexus.getHologramConfig();
            NexusConfigHealthStatus healthStatus = nexus.getHealthStatus();

            double offset = hologramConfig.getHologramOffset();
            Object hologram = hologramInterface.createHologram(location, nexus.getId(), offset);
            hologramConfig.setHologramInterface(hologram);


            // Set hologram content
            hologramConfig.getHologramStrings().forEach(line -> {
                int stringIndex = hologramConfig.getHologramStrings().indexOf(line);
                if (line.equals("%material%")) {
                    hologramInterface.insertItemLine(hologram, stringIndex, new ItemStack(nexus.getMaterial()));
                    return;
                }
                hologramInterface.insertTextLine(
                        hologram,
                        stringIndex,
                        StringUtils.replace(line,
                                new Placeholder("%health%", healthStatus.getDamage()),
                                new Placeholder("%maxHealth%", healthStatus.getMaximumHealth())
                        ));
            });
        }

        updateHologramHealthPositions(nexus);
        updateHologramPositions(nexus, false);
    }

    private void updateHologramHealthPositions(Nexus nexus) {
        HologramInterface hologramInterface = plugin.getHologram();

        if (hologramInterface == null) return;
        NexusConfigHologram hologramConfig = nexus.getHologramConfig();
        NexusConfigHealthStatus healthStatus = nexus.getHealthStatus();
        Object hologram = hologramConfig.getHologramInterface();

        hologramConfig.getHologramStrings().forEach(line -> {
            if (line.contains("%health%") || line.contains("%maxHealth%")) {
                int stringIndex = hologramConfig.getHologramStrings().indexOf(line);

                hologramInterface.editTextLine(
                        hologram,
                        stringIndex,
                        StringUtils.replace(line,
                                new Placeholder("%health%", healthStatus.getDamage()),
                                new Placeholder("%maxHealth%", healthStatus.getMaximumHealth())
                        )
                );
            }
        });
    }

    private void updateHologramPositions(Nexus nexus, boolean reset) {
        HologramInterface hologramInterface = plugin.getHologram();

        if (hologramInterface == null) return;
        NexusConfigHologram hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologramInterface();
        YamlDocument config = plugin.getConfiguration();

        // Limit leaderboard players.
        int configLimit = config.getInt("LEADERBOARD.LIMIT");
        // If off-limits set to 3.
        int limit = (configLimit <= 0 || configLimit > 5) ? 3 : config.getInt("LEADERBOARD.LIMIT");

        for (int i = 0; i < limit; i++) {
            for (String line : hologramConfig.getHologramStrings()) {
                int stringIndex = hologramConfig.getHologramStrings().indexOf(line);

                // Junkie me, pls kill me
                int ix = i + 1;

                if (line.contains("%top_" + ix + "%") || line.contains("%value_" + ix + "%")) {

                    boolean resetOrEmpty = reset || ix > nexus.getDestroyers().size();
                    List<String> destroyers = nexus.getDestroyers()
                            .keySet()
                            .stream()
                            .sorted(new ModuleComparator(nexus.getDestroyers()))
                            .collect(Collectors.toList());

                    hologramInterface.editTextLine(
                            hologram,
                            stringIndex,
                            StringUtils.replace(line,
                                    new Placeholder("%top_" + ix + "%", resetOrEmpty ? config.getString("LEADERBOARD.EMPTY_TOP") : destroyers.get(i)),
                                    new Placeholder("%value_" + ix + "%", resetOrEmpty ? config.getString("LEADERBOARD.EMPTY_VALUE") : String.valueOf(nexus.getDestroyers().get(destroyers.get(i))
                                    ))
                            )
                    );
                }
            }
        }
    }

    public Nexus getNexus(String yes) {
        return null;
    }

    public boolean existsNexusBlock(String name) {
        return false;
    }

    public List<Nexus> getNexusBlocks() {
        return nexusBlocks;
    }
}
