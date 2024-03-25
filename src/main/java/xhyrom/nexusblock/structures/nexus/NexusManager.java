package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Material;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NexusManager {

    private final File folder;
    private final NexusBlock plugin;
    private final List<Nexus> nexusBlocks = new ArrayList<>();

    public NexusManager(NexusBlock plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "warps");
    }

    public void createNexusBlock(String name, Material material) {

    }

    public boolean existsNexusBlock(String name) {
        return false;
    }

    private void loadNexusBlocks() {
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

            NexusConfig nexusConfig = new NexusConfig(nexusBlock.getStringRouteMappedValues(false));

            Material material = Material.getMaterial(nexusConfig.material);
            if (material == null) {
                plugin.getLogger().warning("Invalid material in nexus " + nexusConfig.id);
                continue;
            }

            //TODO: Get temp data and apply it.
//            Data dataFromDatabase = NexusBlock.getInstance().jsonDatabase.data.get(nexusConfig.id);
//            if (dataFromDatabase == null)
//                dataFromDatabase = new Data(new CopyOnWriteArrayList<>(), new HashMap<String, Integer>(), 0);

            nexusBlocks.add(new Nexus(
                            nexusConfig.id,
                            material,
                            nexusConfig.hologram,
                            nexusConfig.location,
                            nexusConfig.respawn,
                            nexusConfig.healths,
                            nexusConfig.hologramLocation,
                            nexusConfig.rewards,
                            //TODO: Update since this is empty.
                            new CopyOnWriteArrayList<>(),
                            new HashMap<>(),
                            0
                    )
            );
        }
    }

    private void saveNexusBlocks() {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        //TODO:
        // Delete file if no longer exists.
        // Save if nexus was edited.
    }

    //TODO:
    // Moved from main.
    // Needs to work.
    /*public void onReload() {
        for (Nexus nexus : this.nexuses) {
            this.nexuses.remove(nexus);
            this.hologram.deleteHologram(nexus.hologramInterface);
        }

        nexuses = Loader.loadBlocks();
    }*/

    //TODO:
    // Moved from main, it saves current destroyers.
    // Executed at scheduler at 300 * 20.
//    private void saveData() {
////        JSONDatabase tempJsonDatabase = new JSONDatabase();
////
////        for (Nexus nexus : this.nexuses) {
////            tempJsonDatabase.addNexus(nexus.id, nexus.getDestroyers(), nexus.getDestroys(), nexus.healths.damaged);
////        }
////
////        tempJsonDatabase.toString(getDataFolder() + "/database.json");
////        this.jsonDatabase = tempJsonDatabase;
//    }

    public List<Nexus> getNexusBlocks() {
        return nexusBlocks;
    }
}
