package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;

public class ExitCommand extends Command {
    public ExitCommand() {
        super.name = "exit";
        super.help = "Shuts down the bot.";

        super.ownerCommand = true;
        super.hidden = true;
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Utils.runCommand(this, commandEvent, (command, event) -> Utils.exit(commandEvent.getJDA()));
    }
}
