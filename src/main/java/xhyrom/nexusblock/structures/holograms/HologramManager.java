package xhyrom.nexusblock.structures.holograms;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.implementation.HologramInterface;
import xhyrom.nexusblock.structures.nexusConfig.NexusHealthConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.utils.Placeholder;
import xhyrom.nexusblock.utils.StringUtils;

import java.util.List;

public class HologramManager {

    private final NexusBlock plugin;

    public HologramManager(NexusBlock plugin) {
        this.plugin = plugin;
    }

    public void setupHologram(Nexus nexus) {
        HologramInterface hologramInterface = plugin.getHologram();
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
        HologramInterface hologramInterface = plugin.getHologram();
        if (hologramInterface == null) return;

        updateHologramHealthPositions(nexus);
        updateHologramPositions(nexus, reset);
    }

    public void updateHologramLocation(Nexus nexus, Location location) {
        HologramInterface hologramInterface = plugin.getHologram();
        if (hologramInterface == null) return;

        Object hologram = nexus.getHologramConfig().getHologram();
        if (hologram == null) return;

        hologramInterface.updateLocation(hologram, location);
    }

    public void deleteHologram(Nexus nexus) {
        HologramInterface hologramInterface = plugin.getHologram();
        if (hologramInterface == null) return;

        Object hologram = nexus.getHologramConfig().getHologram();
        if (hologram == null) return;

        hologramInterface.deleteHologram(hologram);
    }

    private void updateHologramHealthPositions(Nexus nexus) {
        HologramInterface hologramInterface = plugin.getHologram();
        if (hologramInterface == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologram();

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
        HologramInterface hologramInterface = plugin.getHologram();
        if (hologramInterface == null) return;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        Object hologram = hologramConfig.getHologram();
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
}
