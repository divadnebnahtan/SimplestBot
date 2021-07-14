package me.aloidia.simplestbot.listeners;

import me.aloidia.simplestbot.Utils;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        System.out.println(Utils.formatDateTime() + " (Console) : Left server \"" + event.getGuild().getName() + "\"");
    }
}
