package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;

public class BroadcastCommand extends Command {
    public BroadcastCommand() {
        super.name = "broadcast";
        super.help = "Send a message to channels with SimplestBot in the topic.";
        super.arguments = "<message>";
        super.aliases = new String[]{"bc"};

        super.ownerCommand = true;
        super.hidden = true;
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, (command, commandEvent) -> Utils.broadcastMessage(commandEvent.getJDA(), commandEvent.getArgs()));
    }
}
