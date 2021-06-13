package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class ClearCommand {

    private final DiscordBot discord;

    public ClearCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();
        String[] args = message.getContentRaw().split(" ");
        
        if(member == null) return;
        if(!this.discord.getBackendManager().hasPermissionPower3(member)) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(args.length == 1) {
            embed.setDescription("You must specify how many messages you want to delete!");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        try {
            MessageHistory messageHistory = new MessageHistory(event.getChannel());
            List<Message> messages = messageHistory.retrievePast(Integer.valueOf(args[1])).complete();
            event.getChannel().deleteMessages(messages).queue();
            embed.setDescription("**Successful**, you have deleted a total of **" + Integer.valueOf(args[1]) + "** messages in " + event.getChannel().getAsMention() + ".");

            EmbedBuilder logEmbed = new EmbedBuilder();
            embed.setColor(9785268);
            embed.addField("Action", "Clear", true);
            embed.addField("Team member", member.getAsMention(), true);
            TextChannel teamlog = this.discord.getGuild().getTextChannelById(this.discord.getTeamlogChannelId());
            if(teamlog != null) {
                teamlog.sendMessage(logEmbed.toString()).queue();
            }

            member.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(embed.build()).queue();
            });
        } catch (NumberFormatException ex) {
            embed.setDescription("You must enter a valid number.");
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }

}
