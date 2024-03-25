package xhyrom.nexusblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static xhyrom.nexusblock.utils.MessageUtils.translateColorCodes;

public class NexusBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String infoMessage = translateColorCodes("Plugin by &cxHyroM#2851 &7available on github &c&nhttps://github.com/xHyroM/NexusBlock");

        if (!sender.hasPermission("nexusblock.admin")) {
            sender.sendMessage(infoMessage);
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
            sender.sendMessage(translateColorCodes(infoMessage));
            sender.sendMessage(translateColorCodes("&cAdmin Commands:\n\n&c/nexusblock reload &8- &7Reload plugin"));
            sender.sendMessage(translateColorCodes("&c/nexusblock reload &8- &7Reload plugin"));
            return true;
        }

        //TODO
        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(translateColorCodes("Please restart server."));
            // NexusBlock.getInstance().reloadConfig();
            // NexusBlock.getInstance().onReload();
            return true;
        }

        return true;
    }
}
