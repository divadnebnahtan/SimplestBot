package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.function.BiConsumer;

public class InviteCommand extends Command {

    public InviteCommand() {
        super.name = "invite";
        super.help = "Creates a link to add SimplestBot to a server.";

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> {
            String inviteLink = String.format(SimplestBot.INVITE_LINK, event.getSelfUser().getId(), 93248);
            event.getMessage().reply(inviteLink).queue();
        };
    }
}
