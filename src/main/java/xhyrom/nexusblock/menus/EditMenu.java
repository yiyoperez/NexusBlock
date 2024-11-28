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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xhyrom.nexusblock.commands.EditNexusCommand;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;

public final class EditMenu implements Menu {

    private final Nexus nexus;
    private final MessageHandler messageHandler;

    private final Material enabledMaterial = Material.LIME_STAINED_GLASS_PANE;
    private final Material disabledMaterial = Material.RED_STAINED_GLASS_PANE;

    public EditMenu(Nexus nexus, EditNexusCommand command) {
        this.nexus = nexus;
        this.messageHandler = command.getMessageHandler();
    }

    @Override
    public String getName() {
        return "Update Menu";
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        //TODO: Create nexus block update items. (Material, Health, MaxHealth, RespawnInterval ...)

        Button statusButton = getStatusButton(player);

        return Content.builder(capacity)
                .setButton(10, statusButton)
                .build();
    }

    private Button getStatusButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(nexus.isEnabled() ? enabledMaterial : disabledMaterial, 1)
                        .setDisplay(Component.text("Nexus Status " + (nexus.isEnabled() ? "enabled" : "disabled")))
                        .build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    menuView.updateButton(event.getSlot(), (button) -> {
                        boolean currentStatus = button.getNamedData("status");

                        button.setNamedData("status", !currentStatus);
                        nexus.setEnabled(!currentStatus);

                        //TODO: Create actual message.
                        messageHandler.sendManualMessage(player, "Nexus " + nexus.getId() + " status has been set to " + !currentStatus);
                        button.setItem(ItemBuilder.modern(!currentStatus ? enabledMaterial : disabledMaterial)
                                .setDisplay(Component.text("Nexus Status " + (!currentStatus ? "enabled" : "disabled")))
                                .build());
                    });

                })
        ).setNamedData("status", nexus.isEnabled());
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern(nexus.getId() + " edit menu.");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(3);
    }

    //TODO:
    // Not sure if needed.
    @Override
    public void onPostClick(MenuView<?> playerMenuView, InventoryClickEvent event) {
        //happens after button click is executed
        event.setCancelled(true);
    }
}
