package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.utils.MessageHandler;

@Command(name = "nexusblock", aliases = {"nb", "blocknexus"})
@Description("NexusBlock plugin main command.")
public class NexusBlockCommand {

    //TODO:
    // Subcommands: Hologram, Rewards, Set(offset, material etc..), Enable/Disable.

    private final MessageHandler messageHandler;

    public NexusBlockCommand(NexusBlock plugin) {
        this.messageHandler = plugin.getMessageHandler();
    }

    @Execute
    public void command(@Context CommandSender sender) {
        sendHelpMessage(sender);
    }

    @Execute(name = "help")
    public void help(@Context CommandSender sender) {
        sendHelpMessage(sender);
    }

    @Execute(name = "reload")
    @Permission("nexusblock.command.reload")
    public void reload(@Context CommandSender sender) {
        messageHandler.sendManualMessage(sender, "Please restart server.");
        //TODO: proper reload.
        // NexusBlock.getInstance().reloadConfig();
        // NexusBlock.getInstance().onReload();
        messageHandler.sendMessage(sender, "RELOADED");
    }

    private void sendHelpMessage(CommandSender sender) {
        messageHandler.sendManualMessage(sender, "%prefix% <gray>Plugin available on github <green><u>https://github.com/yiyoperez/NexusBlock/releases");

        if (sender instanceof Player) {
            if (!sender.hasPermission("nexusblock.admin")) {
                messageHandler.sendMessage(sender, "NO_PERMISSION");
                return;
            }
        }

        messageHandler.sendMessage(sender, "COMMAND_MANAGER.USAGE_HEADER");
        messageHandler.sendListMessage(sender, "MAIN-COMMAND");
    }
}
