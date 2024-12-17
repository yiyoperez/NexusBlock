package xhyrom.nexusblock.commands.handlers;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

public class NexusMissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final MessageHandler messageHandler;

    public NexusMissingPermissionHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        String permissions = missingPermissions.asJoinedText();
        CommandSender sender = invocation.sender();

        messageHandler.sendMessage(sender, "COMMAND_MANAGER.TRANSLATIONS.NO_PERMISSION", new Placeholder("%permission%", permissions));
    }

}
