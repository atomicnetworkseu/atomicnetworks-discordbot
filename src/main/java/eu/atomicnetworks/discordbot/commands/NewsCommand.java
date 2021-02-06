package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class NewsCommand {
    
    private final DiscordBot discord;

    public NewsCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(!this.discord.getBackendManager().hasPermissionPower1(event.getMember())) {
            return;
        }
        
        if(args.length < 3) {
            embed.setDescription("You need to specify a title, channel and the message.");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        if(event.getMessage().getMentionedChannels().isEmpty()) {
            return;
        }
        TextChannel targetChannel = message.getMentionedChannels().stream().findFirst().orElse(null);
        
        String news = "";
        for(int i = 3; i < args.length; i++) {
            news += args[i] + " ";
        }
        
        embed.setTitle(args[1]);
        embed.setDescription(news);
        if(!message.getAttachments().isEmpty()) {
            embed.setImage(message.getAttachments().stream().findFirst().orElse(null).getUrl());
        }
        
        targetChannel.sendMessage(embed.build()).queue();
    }
    
}
