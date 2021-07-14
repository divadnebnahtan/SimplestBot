package me.aloidia.simplestbot.listeners;

import me.aloidia.simplestbot.Utils;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        System.out.println(Utils.formatDateTime() + " (Console) : Joined server \"" + event.getGuild().getName() + "\"");
    }
}
