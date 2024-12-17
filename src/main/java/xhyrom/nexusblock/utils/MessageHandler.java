package xhyrom.nexusblock.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageHandler {

    private final NexusBlock plugin;
    private final YamlDocument messages;

    public MessageHandler(NexusBlock plugin, YamlDocument messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public String intercept(CommandSender sender, String message, List<Placeholder> placeholders) {
        // Replace preset/local placeholders.
        if (!placeholders.isEmpty()) {
            message = StringUtils.replace(message, placeholders);
        }

        return intercept(sender, message);
    }

    public String intercept(CommandSender sender, String message) {
        // Replace PlaceholderAPI's placeholders if found.
        if (plugin.isEnabled("PlaceholderAPI")) {
            if (sender instanceof Player player) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
        }

        // Replace prefix if any.
        if (message.contains("%prefix%")) {
            String prefix = messages.getString("PREFIX");
            if (prefix != null) {
                if (!prefix.isEmpty()) {
                    message = StringUtils.replace(message, new Placeholder("%prefix%", prefix));
                }
            }
        }

        return MessageUtils.translateMiniMessage(message);
    }

    public String getRawMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return String.format("Message was not found in path %s", path);
        }

        return message;
    }

    public String getRawMessage(String path, Placeholder... placeholders) {
        return getRawMessage(path, Arrays.asList(placeholders));
    }

    public String getRawMessage(String path, Object... placeholders) {
        return String.format(getRawMessage(path), placeholders);
    }

    public String getRawMessage(String path, List<Placeholder> placeholders) {
        return StringUtils.replace(getRawMessage(path), placeholders);
    }

    public String getMessage(CommandSender sender, String path, Placeholder... placeholders) {
        return getMessage(sender, path, Arrays.asList(placeholders));
    }

    public String getMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        String message = getRawMessage(path, placeholders);
        return intercept(sender, message, placeholders);
    }

    public List<String> getMessages(CommandSender sender, String path, List<Placeholder> placeholders) {
        if (!messages.isList(path)) {
            return Collections.singletonList(getMessage(sender, path, placeholders));
        }

        List<String> list = new ArrayList<>();
        for (String message : messages.getStringList(path)) {
            list.add(intercept(sender, message, placeholders));
        }

        return list;
    }

    public void sendManualMessage(CommandSender sender, String message, Object... placeholders) {
        sender.sendMessage(String.format(intercept(sender, message), placeholders));
    }

    public void sendManualMessage(CommandSender sender, String message, Placeholder... placeholders) {
        this.sendManualMessage(sender, message, Arrays.asList(placeholders));
    }

    public void sendManualMessage(CommandSender sender, String message, List<Placeholder> placeholders) {
        sender.sendMessage(intercept(sender, message, placeholders));
    }

    public void sendMessage(CommandSender sender, String path, Placeholder... placeholders) {
        this.sendMessage(sender, path, Arrays.asList(placeholders));
    }

    public void sendMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        sender.sendMessage(getMessage(sender, path, placeholders));
    }

    public List<String> getRawStringList(String path, Placeholder... placeholders) {
        return getRawStringList(path, Arrays.asList(placeholders));
    }

    public List<String> getRawStringList(String path, List<Placeholder> placeholders) {
        if (!messages.isList(path)) {
            return Collections.singletonList(getRawMessage(path, placeholders));
        }

        List<String> list = new ArrayList<>();
        for (String message : messages.getStringList(path)) {
            list.add(StringUtils.replace(message, placeholders));
        }

        return list;
    }

    public void sendListMessage(CommandSender sender, String path, Placeholder... placeholders) {
        this.sendListMessage(sender, path, Arrays.asList(placeholders));
    }

    public void sendListMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        getMessages(sender, path, placeholders).forEach(sender::sendMessage);
    }
}