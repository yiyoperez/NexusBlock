package xhyrom.nexusblock.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.holograms.HologramManager;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;

@Command(value = "nexusblock", alias = {"nb", "blocknexus"})
@Description("NexusBlock plugin main command.")
public class NexusBlockCommand extends BaseCommand {

    //TODO:
    // Subcommands: Hologram, Rewards, Edit, Set(offset, material etc..), Enable/Disable.

    private final NexusManager nexusManager;
    private final YamlDocument configuration;
    private final MessageHandler messageHandler;
    private final HologramManager hologramManager;

    public NexusBlockCommand(NexusBlock plugin) {
        this.configuration = plugin.getConfiguration();
        this.nexusManager = plugin.getNexusManager();
        this.messageHandler = plugin.getMessageHandler();
        this.hologramManager = plugin.getHologramManager();
    }

    @Default
    public void command(CommandSender sender) {
        sendHelpMessage(sender);
    }

    @SubCommand(value = "help")
    public void help(CommandSender sender) {
        sendHelpMessage(sender);
    }

    @SubCommand(value = "reload")
    @Permission("nexusblock.command.reload")
    public void reload(CommandSender sender) {
        messageHandler.sendManualMessage(sender, "Please restart server.");
        //TODO: proper reload.
        // NexusBlock.getInstance().reloadConfig();
        // NexusBlock.getInstance().onReload();
        messageHandler.sendMessage(sender, "RELOADED");
    }

    private void sendHelpMessage(CommandSender sender) {
        messageHandler.sendManualMessage(sender, "%prefix% &7Plugin available on github &a&nhttps://github.com/yiyoperez/NexusBlock/releases");
        if (sender instanceof Player) {
            if (!sender.hasPermission("nexusblock.admin")) return;
        }

        messageHandler.sendMessage(sender, "COMMAND_MANAGER.USAGE_HEADER");
        messageHandler.sendListMessage(sender, "MAIN-COMMAND");
    }

    public NexusManager getNexusManager() {
        return nexusManager;
    }

    public YamlDocument getConfiguration() {
        return configuration;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
