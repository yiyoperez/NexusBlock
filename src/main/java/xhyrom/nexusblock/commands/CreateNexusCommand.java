package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

@Command(name = "nexusblock create", aliases = {"nb create", "nexusblock create"})
public class CreateNexusCommand {

    private final NexusBlock plugin;

    public CreateNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.create")
    @Description("Create a new nexus block.")
    public void createCommand(@Context CommandSender sender, @Arg("nexusName") String nexusName) {
        NexusManager nexusManager = plugin.getNexusManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        if (nexusManager.existsNexusBlock(nexusName)) {
            messageHandler.sendMessage(sender, "NEXUS.ALREADY_EXISTS", new Placeholder("%nexusName%", nexusName));
            return;
        }


        nexusManager.createNexusBlock(nexusName, Material.STONE);
        messageHandler.sendMessage(sender, "NEXUS.CREATED", new Placeholder("%nexusName%", nexusName));
    }

    @Execute
    @Permission("nexusblock.command.create")
    @Description("Create a new nexus block.")
    public void createCommand(@Context CommandSender sender, @Arg("nexusName") String nexusName, @Arg("material") @Key("block-materials") String materialName) {
        NexusManager nexusManager = plugin.getNexusManager();
        MessageHandler messageHandler = plugin.getMessageHandler();

        if (nexusManager.existsNexusBlock(nexusName)) {
            messageHandler.sendMessage(sender, "NEXUS.ALREADY_EXISTS", new Placeholder("%nexusName%", nexusName));
            return;
        }

        if (Material.matchMaterial(materialName) == null) {
            //TODO: Create message.
            messageHandler.sendManualMessage(sender, "Invalid material in nexus " + nexusName);
            return;
        }

        nexusManager.createNexusBlock(nexusName, Material.matchMaterial(materialName));
        messageHandler.sendMessage(sender, "NEXUS.CREATED", new Placeholder("%nexusName%", nexusName));
    }
}
