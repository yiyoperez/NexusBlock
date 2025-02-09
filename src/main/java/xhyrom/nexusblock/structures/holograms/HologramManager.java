package xhyrom.nexusblock.structures.holograms;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.implementation.DecentHolograms;
import xhyrom.nexusblock.structures.holograms.implementation.HologramInterface;
import xhyrom.nexusblock.structures.holograms.implementation.HolographicDisplays;
import xhyrom.nexusblock.structures.nexusConfig.NexusHealthConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.utils.Placeholder;
import xhyrom.nexusblock.utils.StringUtils;

import java.util.List;

public final class HologramManager {

    private final NexusBlock plugin;
    private HologramInterface hologramInterface;

    //TODO: Class will need changes to accept per-state hologram.

    public HologramManager(NexusBlock plugin) {
        this.plugin = plugin;
    }

    public void initHologramsHook() {
        if (plugin.isPluginEnabled("DecentHolograms")) {
            this.hologramInterface = new DecentHolograms();
            plugin.getLogger().info("NexusBlock is now using DecentHolograms");
        } else if (plugin.isPluginEnabled("HolographicDisplays")) {
            this.hologramInterface = new HolographicDisplays(plugin);
            plugin.getLogger().info("NexusBlock is now using HolographicDisplays");
        } else {
            plugin.getLogger().severe("No holograms plugins has been detected!");
            plugin.getLogger().severe("They wont work if you are not using an hologram plugin.");
        }
    }

    public void setupHologram(Nexus nexus) {
        Location location = nexus.getLocationConfig().getLocation();
        if (hologramInterface == null || location == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        double offset = hologramConfig.getHologramOffset();

        Object hologram = hologramInterface.createHologram(location, nexus.getId(), offset);
        hologramConfig.setHologram(hologram);

        // Set hologram content
        List<String> hologramStrings = hologramConfig.getHologramStrings();
        hologramStrings.forEach(line -> {
            if (line.equals("%material%")) {
                hologramInterface.insertItemLine(hologram, hologramStrings.indexOf(line), new ItemStack(nexus.getMaterial()));
            } else {
                hologramInterface.insertTextLine(hologram, hologramStrings.indexOf(line), line);
            }
        });

        updateHologram(nexus);
    }

    public void updateHologram(Nexus nexus) {
        updateHologram(nexus, false);
    }

    public void updateHologram(Nexus nexus, boolean reset) {
        if (hologramInterface == null) return;

        updateHologramHealthPositions(nexus);
        updateHologramPositions(nexus, reset);
        updateHologramLocation(nexus, nexus.getLocationConfig().getLocation());
    }

    public void updateHologramLocation(Nexus nexus, Location location) {
        if (hologramInterface == null) return;

        Object hologram = nexus.getHologramConfig().getHologram();
        if (hologram == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        double offset = hologramConfig.getHologramOffset();

        hologramInterface.updateLocation(hologram, location.clone().add(0.5, offset, 0.5));
    }

    public void deleteHologram(Nexus nexus) {
        if (hologramInterface == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologram();
        if (hologram == null) return;

        hologramConfig.setHologram(null);
        hologramInterface.deleteHologram(hologram);
    }

    private void updateHologramHealthPositions(Nexus nexus) {
        if (hologramInterface == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologram();
        if (hologram == null) return;

        NexusHealthConfig healthStatus = nexus.getHealthStatus();
        List<String> hologramStrings = hologramConfig.getHologramStrings();

        hologramStrings.forEach(line -> {
            if (line.contains("%health%") || line.contains("%maxHealth%")) {
                int stringIndex = hologramStrings.indexOf(line);

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
        if (hologramInterface == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologram();
        if (hologram == null) return;

        YamlDocument config = plugin.getConfiguration();

        // Limit leaderboard players.
        int configLimit = config.getInt("LEADERBOARD.LIMIT");
        // If off-limits set to 3.
        int limit = (configLimit <= 0 || configLimit > 5) ? 3 : configLimit;

        List<String> hologramStrings = hologramConfig.getHologramStrings();
        List<String> destroyers = plugin.getNexusManager().getSortedDestroyers(nexus);

        for (int i = 0; i < limit; i++) {
            // Junkie me, pls kill me
            int ix = i + 1;

            for (String line : hologramStrings) {
                int stringIndex = hologramStrings.indexOf(line);
                if (line.contains("%top_" + ix + "%") || line.contains("%value_" + ix + "%")) {
                    boolean resetOrEmpty = reset || ix > nexus.getDestroyers().size();
                    String topValue = resetOrEmpty ? config.getString("LEADERBOARD.EMPTY_TOP") : destroyers.get(i);
                    String value = resetOrEmpty ? config.getString("LEADERBOARD.EMPTY_VALUE") : String.valueOf(nexus.getDestroyers().get(destroyers.get(i)));

                    hologramInterface.editTextLine(
                            hologram,
                            stringIndex,
                            StringUtils.replace(line,
                                    new Placeholder("%top_" + ix + "%", topValue),
                                    new Placeholder("%value_" + ix + "%", value)
                            )
                    );
                }
            }
        }
    }

    public HologramInterface getHologramInterface() {
        return hologramInterface;
    }
}
