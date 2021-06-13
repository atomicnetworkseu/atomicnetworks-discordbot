package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class ClearCommand {

    private final DiscordBot discord;

    public ClearCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");

        MessageHistory messageHistory = new MessageHistory(event.getChannel());
        List<Message> messages = messageHistory.retrievePast(Integer.valueOf(args[1])).complete();
        event.getChannel().deleteMessages(messages).queue();
        
        if(!this.discord.getBackendManager().hasPermissionPower3(event.getMember())) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(args.length == 1) {
            embed.setDescription("You must specify how many messages you want to delete!");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        try {
            embed.setDescription("**Successful**, you have deleted a total of **" + Integer.valueOf(args[1]) + "** messages in " + event.getChannel().getAsMention() + ".");

            EmbedBuilder logEmbed = new EmbedBuilder();
            embed.setColor(9785268);
            embed.addField("Action", "Clear", true);
            embed.addField("Team member", event.getMember().getUser().getAsMention(), true);
            TextChannel teamlog = this.discord.getJda().getGuildById(this.discord.getGuildId()).getTextChannelById(this.discord.getTeamlogChannelId());
            if(teamlog != null) {
                teamlog.sendMessage(logEmbed.toString()).queue();
            }

            event.getMember().getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(embed.build()).queue();
            });
        } catch (NumberFormatException ex) {
            embed.setDescription("You must enter a valid number.");
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }

}
