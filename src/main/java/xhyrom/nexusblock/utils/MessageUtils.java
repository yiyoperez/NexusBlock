package xhyrom.nexusblock.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String translateColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
