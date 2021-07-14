package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.function.BiConsumer;

public class HelpCommand extends Command {

    public HelpCommand() {
        super.name = "help";
        super.help = "Shows a list of available commands";

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> {
            CommandClient commandClient = event.getClient();

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(":gear: Command help :gear:")
                    .setDescription("Use `" + commandClient.getPrefix() + "<command> " + getName() + "` for further information!");

            commandClient.getCommands().stream().filter(
                    cmd -> !cmd.isHidden()
            ).forEach(
                    cmd -> builder.addField(
                            "`" + cmd.getName() + "` " + cmd.getHelp(),
                            "Usage: `" + commandClient.getPrefix() + cmd.getName()
                                    + (cmd.getArguments() != null ? " " + cmd.getArguments() : "")
                                    + "`", false)
            );

            event.getMessage().reply(builder.build()).queue();
        };
    }
}
