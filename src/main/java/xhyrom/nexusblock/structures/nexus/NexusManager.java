package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
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
import xhyrom.nexusblock.utils.Placeholder;
import xhyrom.nexusblock.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

        //TODO: Get temp data and apply it.
//            Data dataFromDatabase = NexusBlock.getInstance().jsonDatabase.data.get(nexusConfig.id);
//            if (dataFromDatabase == null)
//                dataFromDatabase = new Data(new CopyOnWriteArrayList<>(), new HashMap<String, Integer>(), 0);

        Nexus nexus = new Nexus(
                nexusConfig.getId(),
                material,
                nexusConfig.getRespawn(),
                //TODO: Get damage from temp data.
                0,
                nexusConfig.getHologram(),
                nexusConfig.getLocation(),
                nexusConfig.getHealths(),
                nexusConfig.getRewards(),
                //TODO: Get destroyers from temp data.
                new HashMap<>()
        );
        nexusBlocks.add(nexus);

        // Create block and hologram.
        nexus.getLocation().getBlock().setType(nexus.getMaterial());
        updateHologram(nexus, true);
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

        private Map<String, Integer> destroys = new HashMap<>();

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

        // TODO: WORK ON REWARDS.
        // Give rewards to top players
        List<String> destroyers = nexus.getDestroyers()
                .keySet()
                .stream()
                .sorted(new ModuleComparator(nexus.getDestroyers()))
                .collect(Collectors.toList());

        destroyers.forEach(playerName -> {
            // Limit leaderboard players.
            int configLimit = plugin.getConfiguration().getInt("LEADERBOARD.LIMIT");
            // If off-limits set to 3.
            int limit = (configLimit <= 0 || configLimit > 5) ? 3 : plugin.getConfiguration().getInt("LEADERBOARD.LIMIT");

            for (int i = 0; i < limit; i++) {
                if (nexus.getDestroyers().size() > i) {
                    continue;
                }

                giveRewards(i, playerName, nexus);
            }
        });

        // Give reward to Casper the friendly ghost?
        giveRewards(-2, player.getName(), nexus);

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

    // TODO: WORK ON REWARDS.
    private void giveRewards(int i, String playerName, Nexus nexus) {
        if (!nexus.getRewardsConfig().getRewards().containsKey(i)) return;

        List<String> rewards = nexus.getRewardsConfig().getReward(i);
        rewards.forEach(reward -> {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    reward
                            .replaceAll("\\{playerName}", playerName)
                            .replaceAll("\\{destroys}", nexus.getDestroyers().get(playerName).toString())
            );
        });
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
