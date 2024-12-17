package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.menus.EditMenu;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

@Command(name = "nexusblock edit")
public class EditNexusCommand {

    private final NexusBlock plugin;

    public EditNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute()
    @Permission("nexusblock.command.edit")
    @Description("Edit nexus settings in-game from a GUI.")
    public void editCommand(@Context CommandSender sender, @Arg("nexusName") @Key("available-blocks") String nexusName) {
        NexusManager nexusManager = plugin.getNexusManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            messageHandler.sendMessage(sender, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
            return;
        }

        //TODO: create message.
        messageHandler.sendManualMessage(sender, "Opening nexus " + nexus.getId() + " edit menu.");
        plugin.getLotus().openMenu((Player) sender, new EditMenu(nexus, plugin));
    }
}