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

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam");
        
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
