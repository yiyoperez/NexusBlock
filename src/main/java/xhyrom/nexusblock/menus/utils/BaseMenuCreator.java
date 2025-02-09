package xhyrom.nexusblock.menus.utils;

import org.bukkit.entity.Player;

public interface BaseMenuCreator {

    /**
     * @param player Player to open the menu to.
     */
    default void open(Player player) {

    }

    default void open(Player player, Object... objects) {
        
    }

}
