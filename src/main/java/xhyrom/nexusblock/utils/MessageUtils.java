package xhyrom.nexusblock.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class MessageUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static String translateColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> translateColor(String... strings) {
        return translateColor(Arrays.asList(strings));
    }

    public static List<String> translateColor(List<String> list) {
        list.forEach(MessageUtils::translateColor);
        return list;
    }

    public static String translateMiniMessage(String message) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(MINI_MESSAGE.deserialize(message));
    }

}
