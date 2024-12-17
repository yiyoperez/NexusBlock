package xhyrom.nexusblock.commands.handlers;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.utils.MessageHandler;

public class NexusInvalidUsageHandler implements InvalidUsageHandler<CommandSender> {

    private final MessageHandler messageHandler;

    public NexusInvalidUsageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        messageHandler.sendMessage(sender, "COMMAND_MANAGER.USAGE_HEADER");
        String prefix = messageHandler.getRawMessage("COMMAND_MANAGER.USAGE_PREFIX");

        if (schematic.isOnlyFirst()) {
            messageHandler.sendManualMessage(sender, prefix + schematic.first());
            return;
        }

        for (String scheme : schematic.all()) {
            messageHandler.sendManualMessage(sender, prefix + scheme);
        }
    }
}
