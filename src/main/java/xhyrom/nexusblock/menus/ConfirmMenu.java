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

public final class ConfirmMenu implements BaseMenuCreator {

    private final NexusBlock plugin;

    public ConfirmMenu(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void open(Player player, Object... objects) {
        Gui gui = Gui.gui()
                .title(Component.text("Confirm Menu"))
                .rows(3)
                .disableAllInteractions()
                .create();

        GuiItem confirmButton = ItemBuilder.from(Material.STONE)
                .name(Component.text("CONFIRM"))
                .asGuiItem();

        GuiItem cancelButton = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("DENY"))
                .asGuiItem();

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(Component.text(""))
                .asGuiItem());

        for (Object object : objects) {
            if (object instanceof Nexus nexus) {
                confirmButton.setAction(event -> plugin.getNexusManager().deleteNexus(nexus));
            } else if (object instanceof BaseMenuCreator lastMenu) {
                cancelButton.setAction(event -> lastMenu.open(player));
            }
        }

        gui.setItem(11, confirmButton);
        gui.setItem(15, cancelButton);

        gui.open(player);
    }
}
