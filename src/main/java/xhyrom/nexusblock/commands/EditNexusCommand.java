package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.menus.EditMenu;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;

@Command(name = "nexusblock edit")
public class EditNexusCommand {

    private final NexusBlock plugin;

    public EditNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute()
    @Permission("nexusblock.command.edit")
    @Description("Edit nexus settings in-game from a GUI.")
    public void editCommand(@Context Player sender, @Arg("nexusName") Nexus nexus) {
        MessageHandler messageHandler = plugin.getMessageHandler();

        //TODO: create message.
        messageHandler.sendManualMessage(sender, "Opening nexus " + nexus.getId() + " edit menu.");
        new EditMenu(nexus, plugin).open(sender);
    }
}