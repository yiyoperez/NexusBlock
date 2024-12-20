package xhyrom.nexusblock.menus;

import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.base.MenuView;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.button.actions.ButtonClickAction;
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.Arrays;
import java.util.Collections;

public final class EditMenu implements Menu {

    private final NexusBlock plugin;
    private final Nexus nexus;
    private final MessageHandler messageHandler;

    private static final Material ENABLED_MATERIAL = Material.LIME_STAINED_GLASS_PANE;
    private static final Material DISABLED_MATERIAL = Material.RED_STAINED_GLASS_PANE;

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
        //TODO: Improve GUI design, displayname, lore...
        // Create nexus block update items. (Rewards)
        // Instead of using AnvilGUI modify values by -1, +1, 5 or 10 when using shift.??

        return Content.builder(capacity)
                .setButton(10, getStatusButton(player))
                .setButton(11, getRespawnButton(player))
                .setButton(12, getHologramButton(player))
                .setButton(13, getDisplayItem(player))
                .setButton(14, getMaxHealthButton(player))
                .setButton(15, getResetHealthButton(player))
                .setButton(16, getDeleteButton(player))
                .build();
    }

    private Button getResetHealthButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.DEAD_BUSH)
                        .setDisplay(Component.text("Reset Current Health"))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    nexus.getHealthStatus().setDamage(0);
                    nexus.getDestroyers().clear();
                    plugin.getHologramManager().updateHologram(nexus, true);
                    player.closeInventory();

                    messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " health has been reset.");
                })
        );
    }

    private Button getMaxHealthButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.APPLE, 1)
                        .setDisplay(Component.text("Max Health: " + nexus.getHealthStatus().getMaximumHealth()))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);

                    new AnvilGUI.Builder()
                            .onClose(stateSnapshot -> {
                                player.closeInventory();
                            })
                            .onClick((slot, stateSnapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }

                                try {
                                    int maxHealth = Integer.parseInt(stateSnapshot.getText());
                                    if (maxHealth <= 0) {
                                        throw new NumberFormatException();
                                    }
                                    nexus.getHealthStatus().setMaximumHealth(maxHealth);
                                    plugin.getHologramManager().updateHologram(nexus);

                                    stateSnapshot.getPlayer().sendMessage("Max health set to " + maxHealth);
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                } catch (NumberFormatException e) {
                                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Value is not number, try again."));
                                }
                            })
                            .text("Enter new max health")
                            .title("Set Max Health")
                            .plugin(plugin)
                            .open(player);
                })
        );
    }

    private Button getDisplayItem(Player player) {
        return Button.clickable(
                ItemBuilder.modern(nexus.getMaterial(), 1)
                        .setDisplay(Component.text("Display item"))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);

                    new AnvilGUI.Builder()
                            .onClose(stateSnapshot -> {
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    plugin.getLotus().openMenu(player, this);
                                }, 5L);
                            })
                            .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }

                                try {
                                    Material mat = Material.matchMaterial(stateSnapshot.getText());
                                    stateSnapshot.getPlayer().sendMessage("Material is " + mat.name());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                } catch (Exception e) {
                                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material not found, try again."));
                                }
                            })
                            .text("Which material?")                              //sets the text the GUI should start with
                            .title("Type the new material value.")                                       //set the title of the GUI (only works in 1.14+)
                            .plugin(plugin)                                          //set the plugin instance
                            .open(player);                                                   //opens the GUI for the player provided
                    messageHandler.sendManualMessage(player, "You clicked the example item!");
                })
        );
    }

    private Button getRespawnButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.BLAZE_POWDER)
                        .setDisplay(Component.text("Respawn Delay " + nexus.getRespawnDelay()))
                        .build(),
                ButtonClickAction.plain((view, event) -> {
                    event.setCancelled(true);
                    view.updateButton(event.getSlot(), button -> {
                        long delay = button.getNamedData("delay");

                        if (event.isLeftClick()) {
                            delay++;
                        } else if (event.isRightClick() && delay > 0) {
                            delay--;
                        }

                        button.setNamedData("delay", delay);
                        button.setItem(ItemBuilder.modern(Material.BLAZE_POWDER)
                                .setDisplay(Component.text("Respawn Delay " + delay))
                                .build());

                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " respawn delay set to " + delay);
                        nexus.setRespawnDelay(delay);
                    });
                })
        ).setNamedData("delay", nexus.getRespawnDelay());
    }

    private Button getHologramButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.BOOK)
                        .setDisplay(Component.text("Hologram"))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    plugin.getLotus().openMenu(player, new HologramMenu(nexus, plugin, this));
                })
        );
    }

    private Button getStatusButton(Player player) {
        boolean isEnabled = nexus.isEnabled();
        return Button.clickable(
                ItemBuilder.modern(isEnabled ? ENABLED_MATERIAL : DISABLED_MATERIAL)
                        .setDisplay(Component.text("Nexus Status: " + (isEnabled ? "Enabled" : "Disabled")))
                        .build(),
                ButtonClickAction.plain((view, event) -> {
                    event.setCancelled(true);
                    view.updateButton(event.getSlot(), button -> {
                        boolean status = button.getNamedData("status");
                        status = !status;

                        button.setNamedData("status", status);
                        nexus.setEnabled(status);

                        button.setItem(ItemBuilder.modern(status ? ENABLED_MATERIAL : DISABLED_MATERIAL)
                                .setDisplay(Component.text("Nexus Status: " + (status ? "Enabled" : "Disabled")))
                                .build());

                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status set to " + (status ? "enabled" : "disabled"));
                    });
                })
        ).setNamedData("status", isEnabled);
    }

    private Button getDeleteButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.BARRIER)
                        .setDisplay(Component.text("Delete Nexus " + nexus.getId()))
                        .setLore()
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    plugin.getLotus().openMenu(player, new ConfirmMenu(plugin, this, ButtonClickAction.plain((view, e) -> {
                        player.closeInventory();
                        plugin.getNexusManager().deleteNexus(nexus);
                        messageHandler.sendMessage(player, "NEXUS.DELETED", new Placeholder("%nexusName%", nexus.getId()));
                    })));
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

    @Override
    public void onDrag(MenuView<?> playerMenuView, InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
