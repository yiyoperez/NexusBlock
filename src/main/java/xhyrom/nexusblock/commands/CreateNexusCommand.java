package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.utils.MaterialUtils;
import xhyrom.nexusblock.utils.Placeholder;

public class CreateNexusCommand extends NexusBlockCommand {
    public CreateNexusCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "create")
    @Permission("nexusblock.command.create")
    @Description("Create a new nexus block.")
    public void createCommand(CommandSender sender, String nexusName, @Suggestion("block-materials") String materialName) {
        if (getNexusManager().existsNexusBlock(nexusName)) {
            getMessageHandler().sendMessage(sender, "NEXUS.ALREADY_EXISTS", new Placeholder("%nexusName%", nexusName));
            return;
        }

        if (!MaterialUtils.isValidMaterial(materialName)) {
            getMessageHandler().sendManualMessage(sender, "Invalid material in nexus " + nexusName);
            return;
        }

        getNexusManager().createNexusBlock(nexusName, Material.matchMaterial(materialName));
        getMessageHandler().sendMessage(sender, "NEXUS.CREATED", new Placeholder("%nexusName%", nexusName));
    }
}
