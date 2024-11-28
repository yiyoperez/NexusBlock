package xhyrom.nexusblock.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.ArrayList;
import java.util.List;

public class InfoNexusCommand extends NexusBlockCommand {

    public InfoNexusCommand(NexusBlock plugin) {
        super(plugin);
    }

    @SubCommand(value = "info")
    @Permission("nexusblock.command.info")
    @Description("Check information about nexus settings.")
    public void infoCommand(CommandSender sender, @Suggestion("available-blocks") String nexusName) {
        MessageHandler messageHandler = getMessageHandler();
        NexusManager nexusManager = getNexusManager();
        Nexus nexus = nexusManager.getNexus(nexusName);
        if (nexus == null) {
            getMessageHandler().sendMessage(sender, "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", nexusName));
            return;
        }

        List<Placeholder> placeholders = new ArrayList<>();
        String header = messageHandler.getRawMessage("NEXUS.INFO.HEADER");

        placeholders.add(new Placeholder("%header%", header));
        placeholders.add(new Placeholder("%id%", nexus.getId()));
        placeholders.add(new Placeholder("%status%", String.valueOf(nexus.isEnabled())));
        placeholders.add(new Placeholder("%material%", nexus.getMaterial().name()));
        placeholders.add(new Placeholder("%damage%", nexus.getHealthStatus().getDamage()));
        placeholders.add(new Placeholder("%max_health%", nexus.getHealthStatus().getMaximumHealth()));
        placeholders.add(new Placeholder("%respawn_interval%", String.valueOf(nexus.getRespawnDelay())));
        //TODO
        placeholders.add(new Placeholder("%destroyer_rewards%", nexus.getRewardsConfig().getRewards().toString()));
        placeholders.add(new Placeholder("%destroyers_rewards%", header));
        placeholders.add(new Placeholder("%hologram%", nexus.getHologramConfig().getHologramStrings().toString()));

        messageHandler.sendListMessage(sender, "NEXUS.INFO.DISPLAY", placeholders);
    }
}