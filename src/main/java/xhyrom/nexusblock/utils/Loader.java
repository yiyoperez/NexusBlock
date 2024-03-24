package xhyrom.nexusblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.database.Data;
import xhyrom.nexusblock.structures.holograms.DecentHolograms;
import xhyrom.nexusblock.structures.holograms.HologramInterface;
import xhyrom.nexusblock.structures.holograms.HolographicDisplays;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Loader {
    public static List<Nexus> loadBlocks() {
        List<Nexus> nexuses = new ArrayList<>();

        for (final Object nexusObject : NexusBlock.getInstance().config.getList("nexuses")) {
            HashMap<String, Object> nexusHashmap = read((HashMap) nexusObject);
            NexusConfig nexusConfig = new NexusConfig(nexusHashmap);

            Material material = Material.getMaterial(nexusConfig.material);
            if (material == null) {
                NexusBlock.getInstance().getLogger().warning("Invalid material in nexus " + nexusConfig.id);
                continue;
            }

            Data dataFromDatabase = NexusBlock.getInstance().jsonDatabase.data.get(nexusConfig.id);
            if (dataFromDatabase == null)
                dataFromDatabase = new Data(new CopyOnWriteArrayList<>(), new HashMap<String, Integer>(), 0);

            nexuses.add(
                    new Nexus(
                            nexusConfig.id,
                            material,
                            nexusConfig.hologram,
                            nexusConfig.location,
                            nexusConfig.respawn,
                            nexusConfig.healths,
                            nexusConfig.hologramLocation,
                            nexusConfig.rewards,
                            dataFromDatabase.destroyers,
                            dataFromDatabase.destroys,
                            dataFromDatabase.damaged
                    )
            );
        }

        return nexuses;
    }

    public static HologramInterface loadHologram() {
        if (checkDependency("DecentHolograms")) {
            return new DecentHolograms();
        } else if (checkDependency("HolographicDisplays")) {
            return new HolographicDisplays();
        } else return null;
    }

    private static boolean checkDependency(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

    private static HashMap<String, Object> read(final HashMap nexusObject) {
        HashMap<String, Object> map = new HashMap<>();

        for (Object key : nexusObject.keySet()) {
            Object value = nexusObject.get(key.toString());
            if (value instanceof HashMap) {
                map.put(key.toString(), value);

                continue;
            }

            map.put(key.toString(), value.toString());
        }

        return map;
    }
}
