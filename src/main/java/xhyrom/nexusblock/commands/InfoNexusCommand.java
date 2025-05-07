package xhyrom.nexusblock.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

import java.util.ArrayList;
import java.util.List;

@Command(name = "nexusblock info")
public class InfoNexusCommand {

    private final NexusBlock plugin;

    public InfoNexusCommand(NexusBlock plugin) {
        this.plugin = plugin;
    }

    @Execute
    @Permission("nexusblock.command.info")
    @Description("Check information about nexus settings.")
    public void infoCommand(@Context CommandSender sender, @Arg("nexusName") Nexus nexus) {
        MessageHandler messageHandler = plugin.getMessageHandler();

        List<Placeholder> placeholders = new ArrayList<>();
        String header = messageHandler.getRawMessage("NEXUS.INFO.HEADER");

        placeholders.add(new Placeholder("%header%", header));
        placeholders.add(new Placeholder("%id%", nexus.getId()));
        placeholders.add(new Placeholder("%state%", nexus.getState().name()));
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