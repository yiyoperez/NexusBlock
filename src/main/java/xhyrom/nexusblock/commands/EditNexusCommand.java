package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.menus.EditMenu;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

public class EditNexusCommand extends NexusBlockCommand {

    public EditNexusCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "edit")
    @Permission("nexusblock.command.edit")
    @Description("Edit nexus settings in-game from a GUI.")
    public void editCommand(CommandSender sender, @Suggestion("available-blocks") String nexusName) {
        MessageHandler messageHandler = getMessageHandler();
        NexusManager nexusManager = getNexusManager();
        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            getMessageHandler().sendMessage(sender, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
            return;
        }

        //TODO: create message.
        messageHandler.sendManualMessage(sender, "Opening nexus " + nexus.getId() + " edit menu.");
        getPlugin().getLotus().openMenu((Player) sender, new EditMenu(nexus, this));
    }
}