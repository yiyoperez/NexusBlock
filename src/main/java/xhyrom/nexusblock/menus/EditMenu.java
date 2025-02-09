package xhyrom.nexusblock.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.menus.utils.BaseMenuCreator;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;

public final class EditMenu implements BaseMenuCreator {

    private final Nexus nexus;
    private final NexusBlock plugin;
    private final MessageHandler messageHandler;

    private static final Material ENABLED_MATERIAL = Material.LIME_STAINED_GLASS_PANE;
    private static final Material DISABLED_MATERIAL = Material.RED_STAINED_GLASS_PANE;

    public EditMenu(Nexus nexus, NexusBlock plugin) {
        this.plugin = plugin;
        this.nexus = nexus;
        this.messageHandler = plugin.getMessageHandler();
    }

    @Override
    public void open(Player player) {
        //TODO: Improve GUI design, displayname, lore...
        // Create nexus block update items. (Rewards)

        Gui gui = Gui.gui()
                .title(Component.text(nexus.getId() + " edit menu."))
                .rows(3)
                .disableAllInteractions()
                .create();

        GuiItem statusItem = ItemBuilder.from(nexus.isEnabled() ? ENABLED_MATERIAL : DISABLED_MATERIAL)
                .name(Component.text("Nexus Status: " + (nexus.isEnabled() ? "Enabled" : "Disabled"))).asGuiItem();
        statusItem.setAction(event -> {
            boolean status = nexus.isEnabled();
            nexus.setEnabled(!status);

            ItemStack item = ItemBuilder.from(nexus.isEnabled() ? ENABLED_MATERIAL : DISABLED_MATERIAL)
                    .name(Component.text("Nexus Status: " + (nexus.isEnabled() ? "Enabled" : "Disabled")))
                    .build();

            messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status set to " + (nexus.isEnabled() ? "enabled" : "disabled"));
            statusItem.setItemStack(item);
            gui.update();
        });
        gui.setItem(10, statusItem);

        GuiItem respawnItem = ItemBuilder.from(Material.BLAZE_POWDER)
                .name(Component.text("Respawn Delay " + nexus.getRespawnDelay()))
                .asGuiItem();

        respawnItem.setAction(event -> {
            long delay = nexus.getRespawnDelay();

            if (event.isLeftClick()) {
                delay++;
            } else if (event.isRightClick() && delay >= 1) {
                delay--;
            }

            respawnItem.setItemStack(ItemBuilder.from(respawnItem.getItemStack())
                    .name(Component.text("Respawn Delay " + delay))
                    .build());
            messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " respawn delay set to " + delay);
            nexus.setRespawnDelay(delay);
            gui.update();
        });
        gui.setItem(11, respawnItem);

        GuiItem hologramItem = ItemBuilder.from(Material.BOOK)
                .name(Component.text("Hologram"))
                .asGuiItem(event -> new HologramMenu(nexus, plugin).open(player, this));
        gui.setItem(12, hologramItem);
        //                .setButton(13, getDisplayItem(player))
        //                .setButton(14, getMaxHealthButton(player))
        GuiItem resetCurrentHealth = ItemBuilder.from(Material.DEAD_BUSH)
                .name(Component.text("Reset Current Health"))
                .asGuiItem();

        resetCurrentHealth.setAction(event -> {
            event.setCancelled(true);
            nexus.getHealthStatus().setDamage(0);
            nexus.getDestroyers().clear();
            plugin.getHologramManager().updateHologram(nexus, true);
            player.closeInventory();

            messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " health has been reset.");
        });
        gui.setItem(15, resetCurrentHealth);

        GuiItem deleteItem = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Delete Nexus " + nexus.getId()))
                .asGuiItem(event -> new ConfirmMenu(plugin).open(player, nexus, this));
        gui.setItem(16, deleteItem);

        gui.open(player);
    }

//
//    private Button getMaxHealthButton(Player player) {
//        return Button.clickable(
//                ItemBuilder.modern(Material.APPLE, 1)
//                        .setDisplay(Component.text("Max Health: " + nexus.getHealthStatus().getMaximumHealth()))
//                        .build(),
//                ButtonClickAction.plain((menuView, event) -> {
//                    event.setCancelled(true);
//
//                    new AnvilGUI.Builder()
//                            .onClose(stateSnapshot -> {
//                                player.closeInventory();
//                            })
//                            .onClick((slot, stateSnapshot) -> {
//                                if (slot != AnvilGUI.Slot.OUTPUT) {
//                                    return Collections.emptyList();
//                                }
//
//                                try {
//                                    int maxHealth = Integer.parseInt(stateSnapshot.getText());
//                                    if (maxHealth <= 0) {
//                                        throw new NumberFormatException();
//                                    }
//                                    nexus.getHealthStatus().setMaximumHealth(maxHealth);
//                                    plugin.getHologramManager().updateHologram(nexus);
//
//                                    stateSnapshot.getPlayer().sendMessage("Max health set to " + maxHealth);
//                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
//                                } catch (NumberFormatException e) {
//                                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Value is not number, try again."));
//                                }
//                            })
//                            .text("Enter new max health")
//                            .title("Set Max Health")
//                            .plugin(plugin)
//                            .open(player);
//                })
//        );
//    }
//
//    private Button getDisplayItem(Player player) {
//        return Button.clickable(
//                ItemBuilder.modern(nexus.getMaterial(), 1)
//                        .setDisplay(Component.text("Display item"))
//                        .build(),
//                ButtonClickAction.plain((menuView, event) -> {
//                    event.setCancelled(true);
//
//                    new AnvilGUI.Builder()
//                            .onClose(stateSnapshot -> {
//                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
//                                    plugin.getLotus().openMenu(player, this);
//                                }, 5L);
//                            })
//                            .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
//                                if (slot != AnvilGUI.Slot.OUTPUT) {
//                                    return Collections.emptyList();
//                                }
//
//                                try {
//                                    Material mat = Material.matchMaterial(stateSnapshot.getText());
//                                    stateSnapshot.getPlayer().sendMessage("Material is " + mat.name());
//                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
//                                } catch (Exception e) {
//                                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material not found, try again."));
//                                }
//                            })
//                            .text("Which material?")                              //sets the text the GUI should start with
//                            .title("Type the new material value.")                                       //set the title of the GUI (only works in 1.14+)
//                            .plugin(plugin)                                          //set the plugin instance
//                            .open(player);                                                   //opens the GUI for the player provided
//                    messageHandler.sendManualMessage(player, "You clicked the example item!");
//                })
//        );
//    }
}
