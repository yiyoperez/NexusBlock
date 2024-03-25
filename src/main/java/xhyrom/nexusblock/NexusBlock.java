package xhyrom.nexusblock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xhyrom.nexusblock.commands.nexusblock;
import xhyrom.nexusblock.events.BlockDestroy;
import org.bukkit.Bukkit;
import xhyrom.nexusblock.structures.holograms.DecentHolograms;
import xhyrom.nexusblock.structures.holograms.HologramInterface;
import xhyrom.nexusblock.structures.holograms.HolographicDisplays;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class NexusBlock extends JavaPlugin {
    private static NexusBlock Instance;
    public List<Nexus> nexuses = new ArrayList<>();
    public HologramInterface hologram;
    public FileConfiguration config = getConfig();
    public JSONDatabase jsonDatabase;
    private HologramInterface hologram;

    @Override
    public void onEnable() {
        Instance = this;

        this.saveDefaultConfig();
        config.options().copyDefaults(true);

        setupHolograms();

        hologram = Loader.loadHologram();
        nexuses = Loader.loadBlocks();
        getCommand("nexusblock").setExecutor(new nexusblock());
        getServer().getPluginManager().registerEvents(new BlockDestroy(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::saveData, 1L, (long) 300 * 20);
    }

    private void setupHolograms() {
        if (Bukkit.getPluginManager().getPlugin("DecentHolograms").isEnabled()) {
            this.hologram = new DecentHolograms();
        } else if (Bukkit.getPluginManager().getPlugin("DecentHolograms").isEnabled()) {
            this.hologram = new HolographicDisplays(this);
        } else {
            getLogger().severe("No holograms plugins has been detected!");
            getLogger().severe("They wont work if");
        }
    }

    @Override
    public void saveDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        File databaseFile = new File(getDataFolder(), "database.json");

        if (!configFile.exists()) {
            super.saveResource("config.yml", false);
        }

        if (!databaseFile.exists()) {
            super.saveResource("database.json", false);
        }

        //TODO: Change temporary database.
//        try {
//            this.jsonDatabase = gson.fromJson(new FileReader(databaseFile), JSONDatabase.class);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void onReload() {
        for (Nexus nexus : this.nexuses) {
            this.nexuses.remove(nexus);
            this.hologram.deleteHologram(nexus.hologramInterface);
        }

        nexuses = Loader.loadBlocks();
    }

    public HologramInterface getHologram() {
        return hologram;
    }

    public static NexusBlock getInstance() {
        return Instance;
    }
}
