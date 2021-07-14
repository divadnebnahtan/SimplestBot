package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.aloidia.simplestbot.Leaderboard;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;
import me.aloidia.simplestbot.Website;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.function.BiConsumer;

public class UserCommand extends Command {

    public UserCommand() {
        super.name = "user";
        super.help = "Shows a user's scores in Simplest RPG";
        super.arguments = "<username>";

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> {
            String username = event.getArgs();
            if (username.isBlank()) {
                helpBiConsumer.accept(event, this);
                return;
            }

            Website site = Website.getWebsite();
            event.getMessage().reply(createEmbed(username, site.getLeaderboards())).queue();
        };
    }

    private MessageEmbed createEmbed(String username, List<Leaderboard> boards) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(username, SimplestBot.SRPG_URL);
        embedBuilder.setThumbnail(SimplestBot.EMBED_THUMBNAIL);

        for (Leaderboard board : boards) {

            String score = board.find(username, "Nickname", board.getTitle());
            String rank = board.find(username, "Nickname", "Position");

            String name = board.getTitle() + (score != null ? ": " + score : "");
            String value = rank != null ? ("Rank #" + rank) : "Unranked";
            embedBuilder.addField(
                    name,
                    value,
                    true
            );
            if (rank != null) {
                embedBuilder.setColor(Utils.getEmbedColour(Integer.parseInt(rank)));
            }
        }
        return embedBuilder.build();
    }


}
