package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import java.text.MessageFormat;
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
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class CookieCommand {
    
    private final DiscordBot discord;
    private Random random;

    public CookieCommand(DiscordBot discord) {
        this.discord = discord;
        this.random = new Random();
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));

        if(message.getMentionedMembers().isEmpty()) {
            embed.setDescription("You also have to tell me who gets the cookie! 😋");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        Member target = message.getMentionedMembers().stream().findFirst().orElse(null);
        if(target.getId().equals(event.getMember().getUser().getId())) {
            embed.setDescription("You can't give yourself a cookie, unfortunately. 🍪");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        if(target.getId().equals(this.discord.getJda().getSelfUser().getId())) {
            embed.setDescription("I don't need a cookie, thanks anyway! 🍪");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        if(target.getId().equals("697517106287345737")) {
            embed.setDescription("I don't need a cookie, thanks anyway! 🍪");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        List<String> gifUrls = Arrays.asList(
                "https://cdn.atomicnetworks.eu/discord/cookies/one.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/two.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/three.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/four.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/five.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/six.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/seven.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/eight.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/nine.gif",
                "https://cdn.atomicnetworks.eu/discord/cookies/ten.gif"
        );
        this.discord.getBackendManager().addCookies(target.getId(), 1);
        
        embed.setAuthor("Cookies", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        embed.setDescription(MessageFormat.format("Congratulations {0},\nyou have been given a cookie! <:blobnomnom:771739615538184193>", target.getAsMention()));
        embed.setImage(gifUrls.get(this.random.nextInt(10)));
        
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
