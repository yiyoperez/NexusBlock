package xhyrom.nexusblock.structures.nexus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.api.events.PlayerDestroyNexus;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramInterface;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
                nexusConfig.getHologram(),
                nexusConfig.getLocation(),
                nexusConfig.getRespawn(),
                nexusConfig.getHealths(),
                nexusConfig.getHologramLocation(),
                nexusConfig.getRewards(),
                //TODO: Get destroys and destroyers from temp data.
                new CopyOnWriteArrayList<>(),
                new HashMap<>(),
                0
        );
        nexusBlocks.add(nexus);
        updateHologram(nexus, true);
    }

    public void onHit(Player player, Nexus nexus) {
        nexus.getHealthStatus().increaseDamage();

        nexus.getDestroys().merge(player.getName(), 1, Integer::sum);

        if (!nexus.getDestroyers().contains(player.getName())) {
            nexus.getDestroyers().add(player.getName());
        }

        nexus.getDestroyers().sort(new ModuleComparator(nexus.getDestroys()));
        if (nexus.getDestroyers().size() > nexus.getHologramConfig().positionsHologramPositions.size()) {
            nexus.getDestroyers().remove(nexus.getHologramConfig().positionsHologramPositions.size());
        }

        updateHologram(nexus, false);
        if (nexus.getHealthStatus().getDamage() >= nexus.getHealthStatus().getMaximumHealth()) {
            destroy(player, nexus);
        }
    }

    private static class ModuleComparator implements Comparator<String> {

        private HashMap<String, Integer> destroys = new HashMap<>();

        public ModuleComparator(HashMap<String, Integer> destroys) {
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

        nexus.getLocation().getWorld().strikeLightningEffect(nexus.getLocation());

        Block block = nexus.getLocation().getBlock();
        block.setType(Material.BEDROCK);

        // Give rewards to top players
        for (int i = 0; i <= nexus.getHologramConfig().positionsHologramPositions.size(); i++) {
            if (nexus.getDestroyers().size() <= i) break;

            String playerName = nexus.getDestroyers().get(i);
            if (playerName == null) continue;

            giveRewards(i, playerName, nexus);
        }

        // Give reward to Casper the friendly ghost?
        giveRewards(-2, player.getName(), nexus);

        // So... this resets the nexus block.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block1 = nexus.getLocation().getBlock();
            block1.setType(nexus.getMaterial());

            nexus.getHealthStatus().setDamage(0);
            updateHologramHealthPositions(nexus);

            nexus.getDestroyers().clear();
            nexus.getDestroys().clear();
            updateHologramPositions(nexus, true);
        }, nexus.getRespawnDelay() * 20L);
    }

    private void giveRewards(int i, String playerName, Nexus nexus) {
        if (!nexus.getRewardsConfig().getRewards().containsKey(i)) return;

        List<String> rewards = nexus.getRewardsConfig().getReward(i);
        rewards.forEach(reward -> {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    reward
                            .replaceAll("\\{playerName}", playerName)
                            .replaceAll("\\{destroys}", String.valueOf(nexus.getDestroys().get(playerName)))
            );
        });
    }

    private void updateHologram(Nexus nexus, boolean setup) {
        HologramInterface hologramInterface = plugin.getHologram();

        if (setup) {
            int i = 0;
            for (String line : nexus.getHologramConfig().main) {
                if (line.contains("{health}") || line.contains("{maximumHealth}")) {
                    nexus.getHologramConfig().healthVariablesPositions.put(i, line);
                }

                line = line
                        .replaceAll("\\{health}", String.valueOf(nexus.getHealthStatus().getDamage()))
                        .replaceAll("\\{maximumHealth}", String.valueOf(nexus.getHealthStatus().getMaximumHealth()));

                if (line.contains("\n")) {
                    for (String l : line.split("\n")) {
                        hologramInterface.insertTextLine(
                                hologramInterface,
                                i,
                                l
                                        .replaceAll("\\{playerName}", "-")
                                        .replaceAll("\\{count}", "0")
                        );
                        nexus.getHologramConfig().positionsHologramPositions.put(i, l);
                        i++;
                    }

                    continue;
                }

                if (line.equals("{BLOCK:MATERIAL}")) {
                    hologramInterface.insertItemLine(hologramInterface, i, new ItemStack(nexus.getMaterial()));
                } else {
                    hologramInterface.insertTextLine(hologramInterface, i, line);
                }

                i++;
            }
        }

        updateHologramHealthPositions(nexus);
        updateHologramPositions(nexus, false);
    }

    private void updateHologramHealthPositions(Nexus nexus) {
        HologramInterface hologramInterface = plugin.getHologram();

        nexus.getHologramConfig().healthVariablesPositions.forEach((i, line) -> {
            hologramInterface.editTextLine(
                    hologramInterface,
                    i,
                    line
                            .replaceAll("\\{health}", String.valueOf(nexus.getHealthStatus().getDamage()))
                            .replaceAll("\\{maximumHealth}", String.valueOf(nexus.getHealthStatus().getMaximumHealth())),
                    false
            );
        });
    }

    private void updateHologramPositions(Nexus nexus, boolean reset) {
        HologramInterface hologramInterface = plugin.getHologram();

        int i = 0;
        for (Map.Entry<Integer, String> line : nexus.getHologramConfig().positionsHologramPositions.entrySet()) {
            if (reset) {
                hologramInterface.editTextLine(
                        hologramInterface,
                        line.getKey(),
                        line.getValue()
                                .replaceAll("\\{playerName}", "-")
                                .replaceAll("\\{count}", "0"),
                        false
                );

                continue;
            }

            if (nexus.getDestroyers().size() <= i) break;

            String playerName = nexus.getDestroyers().get(i);
            if (playerName == null) continue;

            hologramInterface.editTextLine(
                    hologramInterface,
                    line.getKey(),
                    line.getValue()
                            .replaceAll("\\{playerName}", playerName)
                            .replaceAll("\\{count}", String.valueOf(nexus.getDestroys().get(playerName))),
                    false
            );

            i++;
        }
    }

    //    private void updateHologram(boolean setup) {
//        HologramInterface hologramInterfaceNexusBlock = NexusBlock.getInstance().hologram;
//
//        if (setup) {
//            int i = 0;
//            for (String line : this.hologram.main) {
//                if (line.contains("{health}") || line.contains("{maximumHealth}")) {
//                    this.hologram.healthVariablesPositions.put(i, line);
//                }
//
//                line = line
//                        .replaceAll("\\{health}", String.valueOf(this.healths.damaged))
//                        .replaceAll("\\{maximumHealth}", String.valueOf(this.healths.maximumHealth));
//
//                if (line.contains("\n")) {
//                    for (String l : line.split("\n")) {
//                        hologramInterfaceNexusBlock.insertTextLine(
//                                this.hologramInterface,
//                                i,
//                                l
//                                        .replaceAll("\\{playerName}", "-")
//                                        .replaceAll("\\{count}", "0")
//                        );
//                        this.hologram.positionsHologramPositions.put(i, l);
//                        i++;
//                    }
//
//                    continue;
//                }
//
//                if (line.equals("{BLOCK:MATERIAL}")) NexusBlock.getInstance().hologram.insertItemLine(this.hologramInterface, i, new ItemStack(this.material));
//                else NexusBlock.getInstance().hologram.insertTextLine(this.hologramInterface, i, line);
//
//                i++;
//            }
//        }
//
//        updateHologramHealthPositions();
//        updateHologramPositions(false);
//    }
//
//    private void updateHologramHealthPositions() {
//        HologramInterface hologramInterfaceNexusBlock = NexusBlock.getInstance().hologram;
//
//        this.hologram.healthVariablesPositions.forEach((i, line) -> {
//            hologramInterfaceNexusBlock.editTextLine(
//                    this.hologramInterface,
//                    i,
//                    line
//                            .replaceAll("\\{health}", String.valueOf(this.healths.damaged))
//                            .replaceAll("\\{maximumHealth}", String.valueOf(this.healths.maximumHealth)),
//                    false
//            );
//        });
//    }
//
//    private void updateHologramPositions(boolean reset) {
//        HologramInterface hologramInterfaceNexusBlock = NexusBlock.getInstance().hologram;
//
//        int i = 0;
//        for (Map.Entry<Integer, String> line : this.hologram.positionsHologramPositions.entrySet()) {
//            if (reset) {
//                hologramInterfaceNexusBlock.editTextLine(
//                        this.hologramInterface,
//                        line.getKey(),
//                        line.getValue()
//                                .replaceAll("\\{playerName}", "-")
//                                .replaceAll("\\{count}", "0"),
//                        false
//                );
//
//                continue;
//            }
//
//            if (this.destroyers.size() <= i) break;
//
//            String playerName = this.destroyers.get(i);
//            if (playerName == null) continue;
//
//            hologramInterfaceNexusBlock.editTextLine(
//                    this.hologramInterface,
//                    line.getKey(),
//                    line.getValue()
//                            .replaceAll("\\{playerName}", playerName)
//                            .replaceAll("\\{count}", String.valueOf(this.destroys.get(playerName))),
//                    false
//            );
//
//            i++;
//        }
//    }

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
