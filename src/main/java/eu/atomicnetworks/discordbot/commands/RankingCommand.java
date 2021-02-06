package eu.atomicnetworks.discordbot.commands;

import com.google.common.collect.Lists;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class RankingCommand {

    private final DiscordBot discord;

    public RankingCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");

        if (!event.getChannel().getId().equals(this.discord.getCommandChannelId())) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Ranking", null, "https://cdn.atomicnetworks.eu/discord/icon.png");

        this.discord.getUserManager().getTopUser((List<User> t) -> {
            List<List<User>> lists = Lists.partition(t, 10);
            embed.setFooter("Page 1/" + lists.size() + " • " + t.size() + " Members");
            String description = "";
            int count = 1;

            for (User user : lists.get(0)) {
                description += count + ". **" + user.getUsername() + "** — Level " + user.getLevel() + "\n";
                if (count == lists.get(0).size()) {
                    embed.setDescription(description);
                    event.getChannel().sendMessage(embed.build()).queue((sendMessage) -> {
                        long messageId = sendMessage.getIdLong();
                        if(lists.size() == 1) {
                            return;
                        }
                        event.getChannel().addReactionById(messageId, ":rightarrow:802089372076867594").queue();
                    });
                }
                count++;
            }
        });

    }

    public void switchPageForward(TextChannel channel, long messageId) {
        channel.retrieveMessageById(messageId).queue((message) -> {
            MessageEmbed messageEmbed = message.getEmbeds().stream().findFirst().orElse(null);
            int page = Integer.valueOf((messageEmbed.getFooter().getText().split(" ")[1]).split("/")[0]);

            this.discord.getUserManager().getTopUser((List<User> t) -> {
                List<List<User>> lists = Lists.partition(t, 10);
                if (lists.size() == 1) {
                    return;
                }
                if ((page + 1) > lists.size()) {
                    return;
                }
                String description = "";
                int count = (10 * page) + 1;

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Ranking", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
                embed.setFooter("Page " + (page + 1) + "/" + lists.size() + " • " + t.size() + " Members");

                Iterator<User> iterator = lists.get((page - 1) + 1).iterator();
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    description += count + ". **" + user.getUsername() + "** — Level " + user.getLevel() + "\n";
                    count++;
                    if (!iterator.hasNext()) {
                        embed.setDescription(description);
                        message.editMessage(embed.build()).queue((sendMessage) -> {
                            long sendMessageId = sendMessage.getIdLong();
                            channel.clearReactionsById(sendMessageId).queue((t1) -> {
                                if ((page + 1) == lists.size()) {
                                    channel.addReactionById(sendMessageId, ":leftarrow:802089371964407838").queue();
                                    return;
                                }
                                if ((page + 1) >= 2) {
                                    channel.addReactionById(sendMessageId, ":leftarrow:802089371964407838").queue();
                                    channel.addReactionById(sendMessageId, ":rightarrow:802089372076867594").queue();
                                } else {
                                    channel.addReactionById(sendMessageId, ":rightarrow:802089372076867594").queue();
                                }
                            });
                        });
                    }
                }
            });
        });
    }

    public void switchPageBack(TextChannel channel, long messageId) {
        channel.retrieveMessageById(messageId).queue((message) -> {
            MessageEmbed messageEmbed = message.getEmbeds().stream().findFirst().orElse(null);
            int page = Integer.valueOf((messageEmbed.getFooter().getText().split(" ")[1]).split("/")[0]);

            this.discord.getUserManager().getTopUser((List<User> t) -> {
                List<List<User>> lists = Lists.partition(t, 10);
                if ((page - 1) > lists.size()) {
                    return;
                }
                String description = "";
                int count = (10 * (page - 2)) + 1;
                if ((page - 1) == 1) {
                    count = 1;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Ranking", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
                embed.setFooter("Page " + (page - 1) + "/" + lists.size() + " • " + t.size() + " Members");

                if(((page - 1) - 1) == -1) {
                    return;
                }
                Iterator<User> iterator = lists.get((page - 1) - 1).iterator();
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    description += count + ". **" + user.getUsername() + "** — Level " + user.getLevel() + "\n";
                    count++;
                    if (!iterator.hasNext()) {
                        embed.setDescription(description);
                        message.editMessage(embed.build()).queue((sendMessage) -> {
                            long sendMessageId = sendMessage.getIdLong();
                            channel.clearReactionsById(sendMessageId).queue((t1) -> {
                                if ((page - 1) == lists.size()) {
                                    channel.addReactionById(sendMessageId, ":leftarrow:802089371964407838").queue();
                                    return;
                                }
                                if ((page - 1) >= 2) {
                                    channel.addReactionById(sendMessageId, ":leftarrow:802089371964407838").queue();
                                    channel.addReactionById(sendMessageId, ":rightarrow:802089372076867594").queue();
                                } else {
                                    channel.addReactionById(sendMessageId, ":rightarrow:802089372076867594").queue();
                                }
                            });
                        });
                    }
                }
            });
        });
    }

}
