package xhyrom.nexusblock;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperJavaPlugin;
import xhyrom.nexusblock.events.BlockDestroy;
import xhyrom.nexusblock.loaders.CommandLoader;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.structures.nexus.NexusService;
import xhyrom.nexusblock.utils.MessageHandler;

import java.io.File;
import java.io.IOException;

public final class NexusBlock extends ZapperJavaPlugin {

    private YamlDocument lang;
    private YamlDocument config;
    private YamlDocument tempData;

    private MessageHandler messageHandler;

    private HologramManager hologramManager;
    private NexusManager nexusManager;
    private NexusService nexusService;
    private CommandLoader commandLoader;

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

        createFiles();
        messageHandler = new MessageHandler(this, lang);

        hologramManager = new HologramManager(this);
        hologramManager.initHologramsHook();

        nexusManager = new NexusManager(this);
        nexusService = new NexusService(this);
        nexusService.loadBlocks();

        commandLoader = new CommandLoader(this);
        commandLoader.load();

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
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void createFiles() {
        try {
            this.lang = YamlDocument.create(new File(getDataFolder(), "lang.yml"),
                    getResource("lang.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE)), "lang-version").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.config = YamlDocument.create(new File(getDataFolder(), "config.yml"),
                    getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE)), "config-version").build());
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

    public boolean isPluginEnabled(String pluginName) {
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

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public NexusManager getNexusManager() {
        return nexusManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

}
