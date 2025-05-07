package xhyrom.nexusblock.commands.handlers;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import xhyrom.nexusblock.NexusBlock;
import xhyrom.nexusblock.structures.Nexus;
import xhyrom.nexusblock.structures.nexus.NexusManager;
import xhyrom.nexusblock.utils.MessageHandler;
import xhyrom.nexusblock.utils.Placeholder;

public class NexusArgument extends ArgumentResolver<CommandSender, Nexus> {

    private final NexusManager nexusManager;
    private final MessageHandler messageHandler;

    public NexusArgument(NexusBlock plugin) {
        this.nexusManager = plugin.getNexusManager();
        this.messageHandler = plugin.getMessageHandler();
    }

    @Override
    protected ParseResult<Nexus> parse(Invocation<CommandSender> invocation, Argument<Nexus> context, String argument) {
        if (!nexusManager.existsNexusBlock(argument)) {
            return ParseResult.failure(messageHandler.getMessage(invocation.sender(), "NEXUS.DOES_NOT_EXIST", new Placeholder("%nexusName%", argument)));
        }

        return ParseResult.success(nexusManager.getNexus(argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Nexus> argument, SuggestionContext context) {
        return nexusManager.getAvailableBlocks().stream().map(Nexus::getId).collect(SuggestionResult.collector());
    }
}