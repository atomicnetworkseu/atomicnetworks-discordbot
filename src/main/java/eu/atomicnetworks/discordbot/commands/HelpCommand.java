package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
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
public class HelpCommand {
    
    private final DiscordBot discord;

    public HelpCommand(DiscordBot discord) {
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
        embed.setAuthor("Helpdesk", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        embed.setDescription("**!help** • Shows you all the commands you can use.\n"
                + "**!info** • All information and contact options summarized for you.\n"
                + "**!level** • You want to know which level you have just reached? Here you can find out!\n"
                + "**!ranking** • A small list of the most active users on our discord server.\n"
                + "**!cookie** • Give your friends a little cookie as a thank you!\n"
                + "**!magicmussel** • Ask the magic mussel for an answer to your question.\n");
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
