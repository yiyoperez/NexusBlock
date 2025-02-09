package xhyrom.nexusblock.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.menus.utils.BaseMenuCreator;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.utils.MessageHandler;

public final class HologramMenu implements BaseMenuCreator {

    private final Nexus nexus;
    private final NexusBlock plugin;
    private final MessageHandler messageHandler;

    private final Material enabledMaterial = Material.LIME_STAINED_GLASS_PANE;
    private final Material disabledMaterial = Material.RED_STAINED_GLASS_PANE;

    public HologramMenu(Nexus nexus, NexusBlock plugin) {
        this.nexus = nexus;
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
    }

    @Override
    public void open(Player player, Object... objects) {
        Gui gui = Gui.gui()
                .title(Component.text("Editor > Hologram"))
                .rows(3)
                .disableAllInteractions()
                .create();

        if (plugin.getHologramManager().getHologramInterface() == null) {
            GuiItem disabled = ItemBuilder.from(Material.BARRIER).name(Component.text("Holograms are disabled.")).asGuiItem();
            gui.setItem(11, disabled);
            gui.setItem(15, disabled);
            return;
        }

        configureHologramControls(gui, nexus, player);

        GuiItem returnItem = ItemBuilder.from(Material.ARROW).name(Component.text("Return to latest inventory.")).asGuiItem();
        gui.setItem(22, returnItem);

        for (Object object : objects) {
            if (object instanceof BaseMenuCreator lastMenu) {
                returnItem.setAction(event -> lastMenu.open(player));
            }
        }

        gui.open(player);
    }

    private void configureHologramControls(Gui gui, Nexus nexus, Player player) {
        NexusHologramConfig hologramConfig = nexus.getHologramConfig();

        GuiItem statusItem = ItemBuilder.from(hologramConfig.getHologram() != null ? enabledMaterial : disabledMaterial)
                .name(Component.text("Hologram Status " + (hologramConfig.getHologram() != null ? "enabled" : "disabled"))).asGuiItem();

        GuiItem offsetItem = ItemBuilder.from(Material.FEATHER)
                .name(Component.text("Hologram Offset " + hologramConfig.getHologramOffset()))
                .asGuiItem();

        statusItem.setAction(event -> {
            boolean status = hologramConfig.getHologram() == null;

            if (status) {
                plugin.getHologramManager().setupHologram(nexus);
            } else {
                plugin.getHologramManager().deleteHologram(nexus);
            }

            messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status has been set to " + status);
            statusItem.setItemStack(ItemBuilder.from(status ? enabledMaterial : disabledMaterial)
                    .name(Component.text("Hologram Status " + (status ? "enabled" : "disabled")))
                    .build());

            gui.update();
        });

        offsetItem.setAction(event -> {
            if (hologramConfig.getHologram() == null) {
                messageHandler.sendManualMessage(player, "Hologram is disabled cannot change offset.");
                return;
            }

            double currentOffset = hologramConfig.getHologramOffset();

            // Adjust offset based on click type
            if (event.isLeftClick()) {
                currentOffset += event.isShiftClick() ? 1.0 : 0.1;
            } else if (event.isRightClick() && currentOffset > 0) {
                currentOffset -= event.isShiftClick() ? 1.0 : 0.1;
            }

            currentOffset = Math.round(currentOffset * 10.0) / 10.0;

            messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " hologram offset has been set to " + currentOffset);
            hologramConfig.setHologramOffset(currentOffset);
            plugin.getHologramManager().updateHologram(nexus);

            // Update GUI item
            offsetItem.setItemStack(ItemBuilder.from(offsetItem.getItemStack())
                    .name(Component.text("Hologram Offset " + currentOffset))
                    .build());

            gui.update();
        });

        gui.setItem(11, statusItem); // Central position for status control
        gui.setItem(15, offsetItem); // Below status for offset control
    }
}
