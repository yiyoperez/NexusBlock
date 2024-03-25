package xhyrom.nexusblock.structures.nexus;

import dev.dejvokep.boostedyaml.YamlDocument;
import xhyrom.nexusblock.NexusBlock;

import java.io.File;
import java.io.IOException;

public class NexusService {

    private final File folder;
    private final NexusBlock plugin;
    private final NexusManager nexusManager;

    public NexusService(NexusBlock plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "blocks");
        this.nexusManager = plugin.getNexusManager();
    }

    public void loadBlocks() {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            // Not really needed, yet?
            String id = file.getName().split("\\.")[0];

            YamlDocument nexusBlock;
            try {
                nexusBlock = YamlDocument.create(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            nexusManager.createNexusBlock(nexusBlock.getStringRouteMappedValues(false));
        }
    }

    public void saveNexusBlocks() {
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
}
