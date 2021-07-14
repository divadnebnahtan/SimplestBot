package me.aloidia.simplestbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import me.aloidia.simplestbot.Leaderboard;
import me.aloidia.simplestbot.SimplestBot;
import me.aloidia.simplestbot.Utils;
import me.aloidia.simplestbot.Website;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LeaderboardCommand extends Command {
    private final EventWaiter waiter;

    public LeaderboardCommand(EventWaiter waiter) {
        super.name = "leaderboard";
        super.help = "Shows the top players in Simplest RPG.";
        super.arguments = "[" + (String.join(" | ", Website.getTables().values())) + "]";
        super.aliases = new String[]{"lb", "top"};

        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION};
        super.helpBiConsumer = SimplestBot.helpBiConsumer;
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        Utils.runCommand(this, event, execution());
    }

    private BiConsumer<Command, CommandEvent> execution() {
        return (command, event) -> {
            Website website = Website.getWebsite();
            Leaderboard leaderboard = Utils.get(website.getLeaderboards(), "Level");
            Optional<Leaderboard> first = website
                    .getLeaderboards()
                    .stream()
                    .filter(board -> board.getTitle().equalsIgnoreCase(event.getArgs()))
                    .findFirst();
            new LeaderboardPaginator(waiter, event, first.orElse(leaderboard)).displayReply(event.getMessage());
        };
    }
}

class LeaderboardPaginator extends Menu {
    private final String title;
    private final String titleUrl;
    private final String image;
    private final String thumbnail;
    private final int itemsPerPage = 10;
    private final java.util.List<MessageEmbed.Field> fields;
    private final int pages;
    private final Consumer<Message> finalAction = message -> message.clearReactions().queue();

    public LeaderboardPaginator(EventWaiter waiter, CommandEvent event, Leaderboard leaderboard) {
        super(waiter, new HashSet<>(Collections.singleton(event.getAuthor())), new HashSet<>(), 1L, TimeUnit.MINUTES);
        this.fields = formatLeaderboard(leaderboard);
        pages = (int) Math.ceil((double) this.fields.size() / (double) itemsPerPage);
        title = leaderboard.getTitle() + " Leaderboard";
        titleUrl = SimplestBot.SRPG_URL;
        image = SimplestBot.EMBED_IMAGE;
        thumbnail = SimplestBot.EMBED_THUMBNAIL;
    }

    private static java.util.List<MessageEmbed.Field> formatLeaderboard(Leaderboard leaderboard) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        for (int position = 1; position <= 500; position++) {
            String nickname = leaderboard.find(String.valueOf(position), "Position", "Nickname");
            String score = leaderboard.find(String.valueOf(position), "Position", leaderboard.getTitle());

            fields.add(new MessageEmbed.Field(
                    Utils.numberToEmote(position) + " " + nickname,
                    leaderboard.getTitle() + ": " + score,
                    false
            ));
        }
        return fields;
    }

    @Override
    public void display(MessageChannel channel) {
        paginate(channel, 1);
    }

    @Override
    public void display(Message message) {
        paginate(message, 1);
    }

    public void displayReply(Message message) {
        paginateReply(message, 1);
    }

    public void paginate(MessageChannel channel, int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        else if (pageNum > pages)
            pageNum = pages;
        Message msg = renderPage(pageNum);
        initialize(channel.sendMessage(msg), pageNum);
    }

    public void paginate(Message message, int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        else if (pageNum > pages)
            pageNum = pages;
        Message msg = renderPage(pageNum);
        initialize(message.editMessage(msg), pageNum);
    }

    public void paginateReply(Message message, int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        else if (pageNum > pages)
            pageNum = pages;
        Message msg = renderPage(pageNum);
        initialize(message.reply(msg), pageNum);
    }

    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue(m -> {
            if (pages > 1) {
                m.addReaction(Utils.BIG_LEFT).queue();
                m.addReaction(Utils.LEFT).queue();
                m.addReaction(Utils.STOP).queue();
                m.addReaction(Utils.RIGHT).queue();
                m.addReaction(Utils.BIG_RIGHT)
                        .queue(v -> pagination(m, pageNum), t -> pagination(m, pageNum));
            } else {
                finalAction.accept(m);
            }
        });
    }

    private void pagination(Message message, int pageNum) {
        paginationWithoutTextInput(message, pageNum);
    }

    private void paginationWithoutTextInput(Message message, int pageNum) {
        waiter.waitForEvent(MessageReactionAddEvent.class,
                event -> checkReaction(event, message.getIdLong()), // Check Reaction
                event -> handleMessageReactionAddAction(event, message, pageNum), // Handle Reaction
                timeout, unit, () -> finalAction.accept(message));
    }

    // Private method that checks MessageReactionAddEvents
    private boolean checkReaction(MessageReactionAddEvent event, long messageId) {
        if (event.getMessageIdLong() != messageId)
            return false;
        switch (event.getReactionEmote().getName()) {
            // LEFT, STOP, RIGHT, BIG_LEFT, BIG_RIGHT all fall-through to
            // return if the User is valid or not. If none trip, this defaults
            // and returns false.
            case Utils.LEFT:
            case Utils.STOP:
            case Utils.RIGHT:
            case Utils.BIG_LEFT:
            case Utils.BIG_RIGHT:
                return isValidUser(event.getUser(), event.isFromGuild() ? event.getGuild() : null);
            default:
                return false;
        }
    }

    // Private method that handles MessageReactionAddEvents
    private void handleMessageReactionAddAction(MessageReactionAddEvent event, Message message, int pageNum) {
        int newPageNum = pageNum;
        int bulkSkipNumber = 6;
        switch (event.getReaction().getReactionEmote().getName()) {
            case Utils.LEFT:
                if (newPageNum > 1)
                    newPageNum--;
                break;
            case Utils.RIGHT:
                if (newPageNum < pages)
                    newPageNum++;
                break;
            case Utils.BIG_LEFT:
                if (newPageNum > 1) {
                    for (int i = 1; newPageNum > 1 && i < bulkSkipNumber; i++) {
                        newPageNum--;
                    }
                }
                break;
            case Utils.BIG_RIGHT:
                if (newPageNum < pages) {
                    for (int i = 1; newPageNum < pages && i < bulkSkipNumber; i++) {
                        newPageNum++;
                    }
                }
                break;
            case Utils.STOP:
                finalAction.accept(message);
                return;
        }

        event.getReaction().removeReaction(event.getUser()).queue();

        int n = newPageNum;

        message.editMessage(renderPage(newPageNum)).queue(m -> pagination(m, n));
    }

    private Message renderPage(int pageNum) {
        Color color;
        if (pageNum == 1)
            color = SimplestBot.TOP_1;
        else if (pageNum <= 2)
            color = SimplestBot.TOP_20;
        else if (pageNum <= 10)
            color = SimplestBot.TOP_100;
        else if (pageNum <= 20)
            color = SimplestBot.TOP_200;
        else if (pageNum <= 30)
            color = SimplestBot.TOP_300;
        else if (pageNum <= 40)
            color = SimplestBot.TOP_400;
        else
            color = SimplestBot.TOP_500;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(color)
                .setTitle(title, titleUrl)
                .setImage(image)
                .setThumbnail(thumbnail);
        int start = (pageNum - 1) * itemsPerPage;
        int end = Math.min(fields.size(), pageNum * itemsPerPage);

        for (int i = start; i < end; i++) {
            embedBuilder.addField(fields.get(i));
        }
        embedBuilder.setFooter("Page " + pageNum + "/" + pages);

        return new MessageBuilder(embedBuilder.build()).build();
    }
}