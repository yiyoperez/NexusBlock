package xhyrom.nexusblock;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.github.mqzen.menus.Lotus;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xhyrom.nexusblock.events.BlockDestroy;
import xhyrom.nexusblock.services.CommandService;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.holograms.implementation.DecentHolograms;
import xhyrom.nexusblock.structures.holograms.implementation.HologramInterface;
import xhyrom.nexusblock.structures.holograms.implementation.HolographicDisplays;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexus.NexusService;
import xhyrom.nexusblock.utils.MessageHandler;

import java.io.File;
import java.io.IOException;

public final class NexusBlock extends JavaPlugin {

    private YamlDocument lang;
    private YamlDocument config;
    private YamlDocument tempData;

    private MessageHandler messageHandler;

    private HologramInterface hologram;
    private HologramManager hologramManager;
    private NexusManager nexusManager;
    private NexusService nexusService;
    private CommandService commandService;

    private Lotus lotus;
    private BukkitAudiences adventure;

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        lotus = Lotus.load(this);

        createFiles();
        messageHandler = new MessageHandler(this, lang);

        setupHolograms();
        hologramManager = new HologramManager(this);
        nexusManager = new NexusManager(this);
        nexusService = new NexusService(this);
        nexusService.loadBlocks();

        commandService = new CommandService(this);
        commandService.start();

        getServer().getPluginManager().registerEvents(new BlockDestroy(this), this);
    }

    @Override
    public void reloadConfig() {
        nexusService.onReload();
    }

    @Override
    public void onDisable() {
        if (nexusService != null) {
            nexusService.saveNexusBlocks();
        }
        if (commandService != null) {
            commandService.finish();
        }
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    private void setupHolograms() {
        if (isEnabled("DecentHolograms")) {
            this.hologram = new DecentHolograms();
            getLogger().info("NexusBlock is now using DecentHolograms");
        } else if (isEnabled("HolographicDisplays")) {
            this.hologram = new HolographicDisplays(this);
            getLogger().info("NexusBlock is now using HolographicDisplays");
        } else {
            getLogger().severe("No holograms plugins has been detected!");
            getLogger().severe("They wont work if you are not using an hologram plugin.");
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

        boolean isPluginLoadingForFirstTime = !new File(getDataFolder() + File.separator + "blocks").exists();
        if (isPluginLoadingForFirstTime) {
            getLogger().info("Creating default nexus block.");

            try {
                YamlDocument defaultConfig = YamlDocument.create(new File(getDataFolder(), "blocks/default.yml"),
                        getResource("default.yml"));

                defaultConfig.update();
                defaultConfig.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
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

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public NexusManager getNexusManager() {
        return nexusManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public Lotus getLotus() {
        return lotus;
    }
}
