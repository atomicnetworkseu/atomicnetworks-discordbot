package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class MagicMusselCommand {
    
    private final DiscordBot discord;
    private final Random random;

    public MagicMusselCommand(DiscordBot discord) {
        this.discord = discord;
        this.random = new Random();
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();
        String[] args = message.getContentRaw().split(" ");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(member == null) return;
        if(args.length == 1) {
            embed.setDescription("I need a question to be able to answer you.");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        String question = "";
        List<String> answers = Arrays.asList("Yes", "Maybe", "No");
        for(int i = 1; i < args.length; i++) {
            question += args[i] + " ";
        }
        
        embed.setAuthor(member.getUser().getName(), null, member.getUser().getAvatarUrl());
        embed.setDescription("**Question:** " + question + "\n**Answer:** " + answers.get(this.random.nextInt(3)));
        
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
