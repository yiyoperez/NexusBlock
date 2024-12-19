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
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

public final class EditMenu implements Menu {

    private final NexusBlock plugin;
    private final Nexus nexus;
    private final MessageHandler messageHandler;

    private final Material enabledMaterial = Material.LIME_STAINED_GLASS_PANE;
    private final Material disabledMaterial = Material.RED_STAINED_GLASS_PANE;

    public EditMenu(Nexus nexus, NexusBlock plugin) {
        this.plugin = plugin;
        this.nexus = nexus;
        this.messageHandler = plugin.getMessageHandler();
    }

    @Override
    public String getName() {
        return "Editor Menu";
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        //TODO: Create nexus block update items. (Material, Health, MaxHealth, ...)
        // Improve item update.

        Button respawnButton = Button.clickable(
                ItemBuilder.modern(Material.BLAZE_POWDER)
                        .setDisplay(Component.text("Respawn Delay " + nexus.getRespawnDelay()))
                        .build(),
                ButtonClickAction.plain(((view, event) -> {
                    event.setCancelled(true);

                    view.updateButton(event.getSlot(), (button) -> {
                        long currentDelay = button.getNamedData("delay");

                        if (event.isLeftClick()) {
                            currentDelay = currentDelay + 1;
                            button.setNamedData("delay", currentDelay);
                        }
                        if (event.isRightClick()) {
                            if (currentDelay > 0) {
                                currentDelay = currentDelay - 1;
                                button.setNamedData("delay", currentDelay);
                            }
                        }

                        button.setItem(ItemBuilder.modern(Material.BLAZE_POWDER)
                                .setDisplay(Component.text("Respawn Delay " + currentDelay))
                                .build());

                        //TODO: Create message.
                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " respawn delay has been set to " + currentDelay);
                        nexus.setRespawnDelay(currentDelay);
                    });
                }))
        ).setNamedData("delay", nexus.getRespawnDelay());

        return Content.builder(capacity)
                .setButton(10, getStatusButton(player))
                .setButton(11, respawnButton)
                .setButton(12, getHologramButton(player))
                .setButton(25, deleteButton(player))
                .build();
    }

    private Button getHologramButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.BOOK)
                        .setDisplay(Component.text("Holograma"))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);

                    plugin.getLotus().openMenu(player,
                            new HologramMenu(nexus, plugin, this));

                })
        );

    }

    private Button getStatusButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(nexus.isEnabled() ? enabledMaterial : disabledMaterial, 1)
                        .setDisplay(Component.text("Nexus Status " + (nexus.isEnabled() ? "enabled" : "disabled")))
                        .build(),

                ButtonClickAction.plain((view, event) -> {
                    event.setCancelled(true);
                    view.updateButton(event.getSlot(), (button) -> {
                        boolean currentStatus = button.getNamedData("status");

                        button.setNamedData("status", !currentStatus);
                        nexus.setEnabled(!currentStatus);

                        //TODO: Create message.
                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status has been set to " + !currentStatus);
                        button.setItem(ItemBuilder.modern(!currentStatus ? enabledMaterial : disabledMaterial)
                                .setDisplay(Component.text("Nexus Status " + (!currentStatus ? "enabled" : "disabled")))
                                .build());
                    });

                })
        ).setNamedData("status", nexus.isEnabled());
    }

    private Button deleteButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.LAVA_BUCKET)
                        .setDisplay(Component.text("Delete nexus " + nexus.getId()))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);

                    plugin.getLotus().openMenu(player,
                            new ConfirmMenu(plugin, this,
                                    ButtonClickAction.plain(((mv, e) -> {
                                        player.closeInventory();
                                        plugin.getNexusManager().deleteNexus(nexus);
                                        messageHandler.sendMessage(player, "NEXUS.DELETED", new Placeholder("%nexusName%", nexus.getId()));
                                    }))));

                })
        );
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern(nexus.getId() + " edit menu.");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(3);
    }

}
