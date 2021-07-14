package me.aloidia.simplestbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

public class SimplestBot {
    public static final Color TOP_500 = Color.ORANGE;
    public static final Color TOP_400 = Color.GREEN;
    public static final Color TOP_300 = Color.BLUE;
    public static final Color TOP_200 = Color.RED;
    public static final Color TOP_100 = Color.MAGENTA;
    public static final Color TOP_20 = Color.YELLOW;
    public static final Color TOP_1 = Color.CYAN;

    public static final String INVITE_LINK = "https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s";
    public static final String SRPG_URL = "https://simplestrpg.com/";
    public static final String EMBED_IMAGE = "https://simplestrpg.com/img/bg.jpg";
    public static final String EMBED_THUMBNAIL = "https://play-lh.googleusercontent.com/VMJmw-SuMCLzZ6qu10KJxXjbfSMwteU0AqxtyCrWxKOAVDx4eirLDkBELnIO7LbfoqJi%3Dw1024-h500";

    public static final BiConsumer<CommandEvent, Command> helpBiConsumer = (event, cmd) -> {
        String[] botPerms = Arrays.stream(cmd.getBotPermissions()).map(Permission::getName).toArray(String[]::new);
        String[] userPerms = Arrays.stream(cmd.getUserPermissions()).map(Permission::getName).toArray(String[]::new);
        String args = cmd.getArguments() != null ? " " + cmd.getArguments() : "";

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("-- " + cmd.getName() + " --");
        embedBuilder.setThumbnail(EMBED_THUMBNAIL);
        if (!cmd.getHelp().isBlank())
            embedBuilder.addField("Description", "```\n" + cmd.getHelp() + "\n```", false);
        embedBuilder.addField("Usage", "```\n" + event.getClient().getPrefix() + cmd.getName() + args + "\n```", false);
        if (cmd.getAliases().length != 0)
            embedBuilder.addField("Alias(es)", "```\n" + String.join(", ", cmd.getAliases()) + "\n```", false);
        if (botPerms.length != 0)
            embedBuilder.addField("Bot Perms", "```\n" + String.join(", ", botPerms) + "\n```", false);
        if (userPerms.length != 0)
            embedBuilder.addField("User Perms", "```\n" + String.join(", ", userPerms) + "\n```", false);
        if (cmd.getCooldown() != 0)
            embedBuilder.addField("Cooldown", "```\n" + cmd.getCooldown() + " seconds.\n```", false);

        event.getMessage().reply(embedBuilder.build()).queue();
    };

    private final Discord bot;

    public SimplestBot(String token) {
        long ping = System.currentTimeMillis();
        bot = new Discord(token);
        bot.start();
        ping = System.currentTimeMillis() - ping;
        String botUsername = bot.jda.getSelfUser().getName() + "#" + bot.jda.getSelfUser().getDiscriminator();
        System.out.println(Utils.formatDateTime() + " (Console) : JDA initialized: " + botUsername + " [" + (ping) + "ms]");
        processInput();
        websiteThread();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(Utils.formatDateTime() + " (Console) : [Error] Bot token required to start.");
            return;
        }
        new SimplestBot(args[0]);
    }

    private void processInput() {
        Thread thread = new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                if (message != null && !message.isEmpty()) {
                    if (message.startsWith("exit")) {
                        exit();
                    } else if (message.startsWith("list")) {
                        System.out.println(Utils.formatDateTime() + " (Console) : Server list = " + Utils.listServers(bot.jda));
                    } else if (message.startsWith("broadcast") && message.split(" ").length > 0) {
                        Utils.broadcastMessage(bot.jda, String.join(" ", Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length)));
                    } else {
                        System.out.println(Utils.formatDateTime() + " (Console) : Unknown Command.");
                    }
                }
            }
        });
        thread.start();
    }

    private void websiteThread() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long ping = System.currentTimeMillis();
                Website.updateWebsite();
                ping = System.currentTimeMillis() - ping;
//                System.out.println(Utils.formatDateTime() + " (Console) : Updated leaderboard data [" + (ping) + "ms]");

            }
        }, 0L, 120_000L);
    }

    private void exit() {
        System.out.println(Utils.formatDateTime() + " (Console) : Shutting down...");
        Utils.exit(bot.jda);
    }
}
