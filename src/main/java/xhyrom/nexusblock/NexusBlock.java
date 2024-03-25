package xhyrom.nexusblock;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xhyrom.nexusblock.commands.NexusBlockCommand;
import xhyrom.nexusblock.events.BlockDestroy;
import xhyrom.nexusblock.structures.holograms.DecentHolograms;
import xhyrom.nexusblock.structures.holograms.HologramInterface;
import xhyrom.nexusblock.structures.holograms.HolographicDisplays;
import xhyrom.nexusblock.structures.nexus.NexusManager;

import java.io.File;
import java.io.IOException;

public final class NexusBlock extends JavaPlugin {

    private YamlDocument lang;
    private YamlDocument config;
    private YamlDocument tempData;

    private HologramInterface hologram;
    private NexusManager nexusManager;


    @Override
    public void onEnable() {
        createFiles();

        nexusManager = new NexusManager(this);

        setupHolograms();

        getCommand("nexusblock").setExecutor(new NexusBlockCommand());
        getServer().getPluginManager().registerEvents(new BlockDestroy(this), this);
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

    private void createFiles() {
        try {
            this.lang = YamlDocument.create(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("lang-version")).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.tempData = YamlDocument.create(new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public YamlDocument getLang() {
        return lang;
    }

    public YamlDocument getConfiguration() {
        return config;
    }

    public YamlDocument getTempData() {
        return tempData;
    }

    public HologramInterface getHologram() {
        return hologram;
    }

    public NexusManager getNexusManager() {
        return nexusManager;
    }
}
