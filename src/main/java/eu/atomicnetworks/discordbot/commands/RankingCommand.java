package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
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
        
        if(!event.getChannel().getId().equals(this.discord.getCommandChannelId())) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("TOP10 - RANKING", null, "https://images.discordapp.net/avatars/697517106287345737/07be164c270546a8c976063bc71939fc.png?size=512");

        this.discord.getUserManager().getTopUser(10, (List<User> t) -> {
            String description = "";
            int count = 1;

            for (User user : t) {
                description += count + ". **" + user.getUsername() + "** — Level " + user.getLevel() + "\n";
                if (count == t.size()) {
                    embed.setDescription(description);
                    event.getChannel().sendMessage(embed.build()).queue();
                }
                count++;
            }
        });

    }

}
