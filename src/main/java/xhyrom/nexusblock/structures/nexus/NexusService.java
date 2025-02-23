package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class NexusService {

    private final File folder;
    private final NexusBlock plugin;
    private final NexusManager nexusManager;
    private final HologramManager hologramManager;
    private final YamlDocument tempData;

    public NexusService(NexusBlock plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "blocks");
        this.tempData = plugin.getTempData();
        this.nexusManager = plugin.getNexusManager();
        this.hologramManager = plugin.getHologramManager();
    }

    public void loadBlocks() {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().split("\\.")[0];

            YamlDocument nexusBlock;
            try {
                nexusBlock = YamlDocument.create(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            nexusManager.createNexusBlock(nexusBlock.getStringRouteMappedValues(false));
            plugin.getLogger().info("Loaded nexus " + id);
        }
    }

    public void saveNexusBlocks() {
        if (nexusManager.getNexusBlocks().isEmpty()) return;

        nexusManager.getNexusBlocks().forEach(nexus -> {
            plugin.getLogger().info("Saving nexus " + nexus.getId());

            // Delete holograms.
            hologramManager.deleteHologram(nexus);

            // Save current damage and destroyers values.
            saveTemporalData(nexus);

            // Update if nexus was edited or create file if it's new.
            try {
                YamlDocument file = YamlDocument.create(new File(folder, nexus.getId() + ".yml"));

                file.set("ID", nexus.getId());
                file.set("ENABLED", nexus.isEnabled());
                file.set("MATERIAL", nexus.getMaterial().name());
                file.set("HEALTH", nexus.getHealthStatus().getMaximumHealth());
                file.set("STATE", nexus.getState().name());
                file.set("HOLOGRAM-HEIGHT", nexus.getHologramConfig().getHologramOffset());
                file.set("RESPAWN_INTERVAL", nexus.getRespawnDelay());

                NexusLocationConfig locationConfig = nexus.getLocationConfig();
                if (locationConfig.getLocation() != null) {
                    file.set("LOCATION.X", nexus.getLocationConfig().getLocation().getX());
                    file.set("LOCATION.Y", nexus.getLocationConfig().getLocation().getY());
                    file.set("LOCATION.Z", nexus.getLocationConfig().getLocation().getZ());
                    file.set("LOCATION.WORLD", nexus.getLocationConfig().getWorld().getName());
                }

                file.set("HOLOGRAM", nexus.getHologramConfig().getHologramStrings());
                file.set("REWARDS.DESTROYER", nexus.getRewardsConfig().getDestroyerRewards());
                nexus.getRewardsConfig().getRewards().forEach((entry, value) ->
                        file.set("REWARDS.DESTROYERS." + entry, value));

                file.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        // Empty list.
        nexusManager.getNexusBlocks().clear();
    }

    // Not even sure if implement it.
    private void deleteOldFiles() {
        // Delete file if no longer exists.
        if (!folder.exists()) return;

//        File[] files = folder.listFiles();
//        if (files == null) return;
//
//
//        // Delete file if no longer exists.
//        for (File file : files) {
//
//            String id = file.getName().split("\\.")[0];
//            if (nexusManager.existsNexusBlock(id)) return;
//
//            try {
//                plugin.getLogger().warning("Deleting file " + file.getName());
//                Files.deleteIfExists(file.toPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String id = file.getFileName().toString().split("\\.")[0];
                        if (!nexusManager.existsNexusBlock(id)) {
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

    public void onReload() {
        saveNexusBlocks();
        loadBlocks();
    }

    private void saveTemporalData(Nexus nexus) {
        if (nexus.getHealthStatus().getDamage() == 0 || nexus.getDestroyers().isEmpty()) return;

        // Save nexus current damage.
        Section section = tempData.createSection(nexus.getId());
        section.set("DAMAGE", nexus.getHealthStatus().getDamage());
        // Save destroyers data.
        nexus.getDestroyers().forEach((destroyer, value) ->
                section.createSection("DESTROYERS").set(destroyer, value));

        try {
            tempData.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
