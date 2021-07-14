package me.aloidia.simplestbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class Utils {
    public static final String ZERO = ":zero:";
    public static final String ONE = ":one:";
    public static final String TWO = ":two:";
    public static final String THREE = ":three:";
    public static final String FOUR = ":four:";
    public static final String FIVE = ":five:";
    public static final String SIX = ":six:";
    public static final String SEVEN = ":seven:";
    public static final String EIGHT = ":eight:";
    public static final String NINE = ":nine:";

    public static final String BIG_LEFT = "\u23EA";
    public static final String LEFT = "\u25C0";
    public static final String STOP = "\u23F9";
    public static final String RIGHT = "\u25B6";
    public static final String BIG_RIGHT = "\u23E9";

    public static Color getEmbedColour(int rank) {
        if (rank == 1) {
            return SimplestBot.TOP_1;
        } else if (rank <= 20) {
            return SimplestBot.TOP_20;
        } else if (rank <= 100) {
            return SimplestBot.TOP_100;
        } else if (rank <= 200) {
            return SimplestBot.TOP_200;
        } else if (rank <= 300) {
            return SimplestBot.TOP_300;
        } else if (rank <= 400) {
            return SimplestBot.TOP_400;
        } else {
            return SimplestBot.TOP_500;
        }
    }

    public static void runCommand(Command cmd, CommandEvent event, BiConsumer<Command, CommandEvent> action) {
        String guildName = event.getGuild().getName();
        String channelName = event.getChannel().getName();
        String username = event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator();
        String message = event.getMessage().getContentRaw();

        long ping = System.currentTimeMillis();
        action.accept(cmd, event);
        ping = System.currentTimeMillis() - ping;
        System.out.println(formatDateTime() + " (" + guildName + " | " + channelName + ") < " + username + " > : " + message + " [" + (ping) + "ms]");
    }

    public static String formatDateTime() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss a")) + "]";
    }

    public static boolean isInteger(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Leaderboard get(List<Leaderboard> leaderboards, String title) {
        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard.getTitle().equalsIgnoreCase(title))
                return leaderboard;
        }
        return null;
    }

    public static String numberToEmote(int number) {
        return String.valueOf(number)
                .replace("0", ZERO)
                .replace("1", ONE)
                .replace("2", TWO)
                .replace("3", THREE)
                .replace("4", FOUR)
                .replace("5", FIVE)
                .replace("6", SIX)
                .replace("7", SEVEN)
                .replace("8", EIGHT)
                .replace("9", NINE);
    }

    public static void broadcastMessage(JDA jda, String message) {
        jda.getGuilds().forEach(guild -> guild.getTextChannels().stream()
                .filter(textChannel -> textChannel.getTopic() != null && textChannel.getTopic().contains("SimplestBot"))
                .forEach(textChannel -> {
                    textChannel.sendMessage(new EmbedBuilder()
                            .setAuthor("Developer Announcement", null, jda.getSelfUser().getAvatarUrl())
                            .setDescription("```\n" + message + "\n```")
                            .setFooter("Put 'SimplestBot' anywhere in the channel topic to receive announcements")
                            .build()).queue();
                    System.out.println("- Broadcast message to: #" + textChannel.getName() + " (" + guild.getName() + ")");
                }));
    }

    public static String listServers(JDA jda) {
        String[] str = jda.getGuilds().stream().map(Guild::getName).toArray(String[]::new);
        return Arrays.toString(str);
    }

    public static void exit(JDA jda) {
        jda.shutdown();
        System.exit(0);
    }
}
