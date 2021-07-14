package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.Discord;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.function.BiConsumer;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super.name = "discord";
        super.help = "Creates a link to the SimplestBot help server.";

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }
    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> event.getMessage().reply("https://discord.gg/7f5E3k5U6v").queue();
    }
}
