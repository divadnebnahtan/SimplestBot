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

public class RankCommand extends Command {

    public RankCommand() {
        super.name = "rank";
        super.help = "Shows the users at a certain rank in Simplest RPG.";
        super.arguments = "<1-500>";

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> {
            String arg = event.getArgs();
            if (arg.isBlank() || !Utils.isInteger(arg)) {
                helpBiConsumer.accept(event, this);
                return;
            }
            int rank = Integer.parseInt(arg);
            if (rank > 500 || rank < 1) {
                helpBiConsumer.accept(event, this);
                return;
            }

            Website site = Website.getWebsite();
            event.getMessage().reply(createEmbed(rank, site.getLeaderboards())).queue();
        };
    }

    private MessageEmbed createEmbed(int rank, List<Leaderboard> boards) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Rank #" + rank, SimplestBot.SRPG_URL);
        embedBuilder.setThumbnail(SimplestBot.EMBED_THUMBNAIL);
        embedBuilder.setColor(Utils.getEmbedColour(rank));

        for (Leaderboard board : boards) {
            String nickname = board.find(String.valueOf(rank), "Position", "Nickname");
            String value = board.find(String.valueOf(rank), "Position", board.getTitle());

            embedBuilder.addField(
                    board.getTitle(),
                    nickname + ": " + value,
                    true
            );
        }

        return embedBuilder.build();
    }


}
