package me.aloidia.simplestbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.aloidia.simplestbot.commands.*;
import me.aloidia.simplestbot.listeners.GuildJoin;
import me.aloidia.simplestbot.listeners.GuildLeave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Discord {
    private final EventWaiter waiter = new EventWaiter();
    private final String token;
    private final ListenerAdapter[] listenerAdapters = new ListenerAdapter[]{
            new GuildJoin(),
            new GuildLeave()
    };
    private final Command[] commands = new Command[]{
            new HelpCommand(),
            new InviteCommand(),
            new RankCommand(),
            new UserCommand(),
            new LeaderboardCommand(waiter),
            new BroadcastCommand(),
            new ExitCommand(),
            new DiscordCommand()
    };
    public JDA jda;

    public Discord(String token) {
        this.token = token;
    }

    public void start() {
        CommandClientBuilder commandClientBuilder = new CommandClientBuilder()
                .addCommands(commands)
                .setEmojis("\u2705", "\u26A0", "\u274C")
                .setOwnerId("285314742044721153")
                .setPrefix("d.")
                .setActivity(Activity.playing("d.help"))
                .useHelpBuilder(false);

        JDABuilder jdaBuilder = JDABuilder.createDefault(token)
                .addEventListeners(waiter, commandClientBuilder.build())
                .addEventListeners(listenerAdapters);

        try {
            jda = jdaBuilder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
