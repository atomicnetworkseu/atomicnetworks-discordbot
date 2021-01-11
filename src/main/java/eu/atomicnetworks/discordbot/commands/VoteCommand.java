package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author kacpe
 */
public class VoteCommand {
    
    private final DiscordBot discord;

    public VoteCommand(DiscordBot discord) {
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
        embed.setAuthor("Voting", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        embed.setDescription("We are happy about every new vote for <@796849019308474419> because this way more people get involved in our projects and become part of our community.\n" +
            "So if you want to support us you can do so at [top.gg](https://top.gg/bot/697517106287345737/vote) and at [discordbotlist.com](https://discordbotlist.com/bots/atomicradio/upvote).");
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
    
}
