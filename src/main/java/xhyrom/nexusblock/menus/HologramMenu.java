package xhyrom.nexusblock.menus;

import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.button.actions.ButtonClickAction;
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.utils.MessageHandler;

public final class HologramMenu implements Menu {

    private final Nexus nexus;
    private final MessageHandler messageHandler;
    private final Menu lastMenu;
    private final NexusBlock plugin;

    private final Material enabledMaterial = Material.LIME_STAINED_GLASS_PANE;
    private final Material disabledMaterial = Material.RED_STAINED_GLASS_PANE;

    public HologramMenu(Nexus nexus, NexusBlock plugin, Menu lastMenu) {
        this.nexus = nexus;
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.lastMenu = lastMenu;
    }

    @Override
    public String getName() {
        return "Editor > Hologram";
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        return Content.builder(capacity)
                .setButton(12, getStatusButton(player))
                .setButton(14, getOffsetButton(player))
                .setButton(40, returnButton(player))
                .build();
    }

    private Button getStatusButton(Player player) {
        boolean hologramStatus = plugin.getHologram() != null;

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();

        return Button.clickable(
                ItemBuilder.modern(hologramStatus ? enabledMaterial : disabledMaterial, 1)
                        .setDisplay(Component.text("Hologram Status " + (hologramStatus ? "enabled" : "No hologram plugin found.")))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    menuView.updateButton(event.getSlot(), (button) -> {
                        boolean currentStatus = button.getNamedData("status");

                        button.setNamedData("status", !currentStatus);
                        nexus.setEnabled(!currentStatus);

                        //TODO: Create message.
                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status has been set to " + !currentStatus);
                        button.setItem(ItemBuilder.modern(!currentStatus ? enabledMaterial : disabledMaterial)
                                .setDisplay(Component.text("Hologram Status " + (!currentStatus ? "enabled" : "No hologram plugin found.")))
                                .build());
                    });

                })
        ).setNamedData("status", nexus.isEnabled());
    }

    private Button getOffsetButton(Player player) {
        if (plugin.getHologram() == null) {
            return Button.empty(ItemBuilder.modern(Material.BARRIER)
                    .setDisplay(Component.text("Holograms are disabled."))
                    .build());
        }

        NexusHologramConfig hologramConfig = nexus.getHologramConfig();
        return Button.clickable(
                ItemBuilder.modern(Material.INK_SAC, 1)
                        .setDisplay(Component.text("Hologram Offset " + String.format("%.1f", hologramConfig.getHologramOffset())))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);

                    menuView.updateButton(event.getSlot(), (button) -> {
                        double currentOffset = button.getNamedData("offset");

                        if (event.isLeftClick()) {
                            if (event.isShiftClick()) {
                                currentOffset += 1.0;
                            } else {
                                currentOffset += 0.1;
                            }
                            button.setNamedData("offset", currentOffset);
                        }
                        if (event.isRightClick()) {
                            if (currentOffset > 0) {
                                if (event.isShiftClick()) {
                                    currentOffset -= 1.0;
                                } else {
                                    currentOffset -= 0.1;
                                }
                                button.setNamedData("offset", currentOffset);
                            }
                        }

                        button.setItem(ItemBuilder.modern(Material.INK_SAC)
                                .setDisplay(Component.text("Hologram Offset " + String.format("%.1f", currentOffset)))
                                .build());

                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " hologram offset has been set to " + String.format("%.2f", currentOffset));
                        hologramConfig.setHologramOffset(currentOffset);
                        plugin.getHologramManager().updateHologram(nexus);
                    });
                })
        ).setNamedData("offset", hologramConfig.getHologramOffset());
    }


    private Button returnButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.FEATHER).setDisplay(Component.text("DENY")).build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    plugin.getLotus().openMenu(player, lastMenu);
                })
        );
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern("Editor > Hologram");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(5);
    }
}
