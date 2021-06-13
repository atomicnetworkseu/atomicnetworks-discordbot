package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class LevelCommand {
    
    private final DiscordBot discord;

    public LevelCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        
        if(member == null) return;
        if(!event.getChannel().getId().equals(this.discord.getCommandChannelId())) return;

        User user = this.discord.getBackendManager().getUser(member.getUser().getId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor(member.getUser().getName(), null, member.getUser().getAvatarUrl());
        embed.setDescription("The last level you have reached is **" + this.discord.getBackendManager().getLevel(user.getId()) + "**,\n you are only missing **" + this.discord.getBackendManager().getRemainingXp(user.getId()) + " XP** to reach the next level!");
        
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
