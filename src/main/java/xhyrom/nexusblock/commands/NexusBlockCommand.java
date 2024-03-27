package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.utils.MessageHandler;

@Command(value = "nexusblock", alias = {"nb", "blocknexus"})
@Permission("nexusblock.admin")
@Description("NexusBlock plugin main command.")
public class NexusBlockCommand extends BaseCommand {

    private final MessageHandler messageHandler;

    public NexusBlockCommand(NexusBlock plugin) {
        this.messageHandler = plugin.getMessageHandler();
    }

    @Default
    public void command(CommandSender sender) {
        messageHandler.sendManualMessage(sender, "Plugin by &cxHyroM#2851 &7available on github &c&nhttps://github.com/xHyroM/NexusBlock");
        messageHandler.sendManualMessage(sender, "&cAdmin Commands:");
        messageHandler.sendManualMessage(sender, "");
        messageHandler.sendManualMessage(sender, "&c/nexusblock reload &8- &7Reload plugin");
    }

    //TODO
    @SubCommand(value = "reload")
    public void reload(CommandSender sender) {
        messageHandler.sendManualMessage(sender, "Please restart server.");
        // NexusBlock.getInstance().reloadConfig();
        // NexusBlock.getInstance().onReload();
    }

}
