package xhyrom.nexusblock.menus;

import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.base.MenuView;
import io.github.mqzen.menus.base.iterator.Direction;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.Slot;
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
import xhyrom.nexusblock.NexusBlock;

public final class ConfirmMenu implements Menu {

    private final NexusBlock plugin;
    private final Menu lastMenu;
    private final ButtonClickAction confirmAction;

    public ConfirmMenu(NexusBlock plugin, Menu lastMenu, ButtonClickAction clickAction) {
        this.plugin = plugin;
        this.lastMenu = lastMenu;
        this.confirmAction = clickAction;
    }

    @Override
    public String getName() {
        return "Confirm Menu";
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        return Content.builder(capacity)
                .iterate(Slot.of(0), Slot.of(2), Direction.RIGHT, ((content, slot) -> content.setButton(slot, denyButton(player))))
                .iterate(Slot.of(9), Slot.of(11), Direction.RIGHT, ((content, slot) -> content.setButton(slot, denyButton(player))))
                .iterate(Slot.of(18), Slot.of(20), Direction.RIGHT, ((content, slot) -> content.setButton(slot, denyButton(player))))
                .iterate(Slot.of(6), Slot.of(8), Direction.RIGHT, ((content, slot) -> content.setButton(slot, confirmButton())))
                .iterate(Slot.of(15), Slot.of(17), Direction.RIGHT, ((content, slot) -> content.setButton(slot, confirmButton())))
                .iterate(Slot.of(24), Slot.of(26), Direction.RIGHT, ((content, slot) -> content.setButton(slot, confirmButton())))
                .iterate(Slot.of(4), Direction.DOWNWARDS, ((content, slot) -> content.setButton(slot, Button.empty(ItemBuilder.modern(Material.GRAY_STAINED_GLASS_PANE).setDisplay(Component.text("&r")).build()))))
                .build();
    }

    private Button denyButton(Player player) {
        return Button.clickable(
                ItemBuilder.modern(Material.FEATHER).setDisplay(Component.text("DENY")).build(),
                ButtonClickAction.plain((menuView, event) -> {
                    event.setCancelled(true);
                    plugin.getLotus().openMenu(player, lastMenu);
                })
        );
    }

    private Button confirmButton() {
        return Button.clickable(
                ItemBuilder.modern(Material.STONE)
                        .setDisplay(Component.text("CONFIRM"))
                        .build(),
                confirmAction
        );
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern("CONFIRM");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(3);
    }

    @Override
    public void onPostClick(MenuView<?> playerMenuView, InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
