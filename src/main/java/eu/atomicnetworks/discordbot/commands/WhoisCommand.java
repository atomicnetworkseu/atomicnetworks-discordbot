package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class WhoisCommand {

    private final DiscordBot discord;

    public WhoisCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if(!this.discord.getBackendManager().hasPermissionPower2(event.getMember())) {
            return;
        }

        String userId = "";
        if (message.getMentionedUsers().isEmpty()) {
            userId = args[1];
        } else {
            userId = (message.getMentionedMembers().stream().findFirst().orElse(null)).getId();
        }
        User user = this.discord.getBackendManager().getUser(userId);
        
        if(user.getId().equals(this.discord.getJda().getSelfUser().getId())) {
            return;
        }
        if(user.getId().equals("697517106287345737")) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        embed.addField(new MessageEmbed.Field("User", "<@" + user.getId() + ">", true));
        embed.addField(new MessageEmbed.Field("Level", MessageFormat.format("{0}", user.getLevel()), true));
        embed.addField(new MessageEmbed.Field("Votes", String.valueOf(user.getVoting().getVoteCount()), true));
        embed.addField(new MessageEmbed.Field("Cookies", String.valueOf(user.getCookies()), true));
        embed.addField(new MessageEmbed.Field("Warnpoints", String.valueOf(user.getWarn().getWarnPoints()), true));
        embed.addBlankField(true);
        String warnlog = "";
        List<User.Warn.WarnLog> logs = user.getWarn().getWarnLog();
        Collections.reverse(logs);
        for(User.Warn.WarnLog log : logs) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm");
            if(null != log.getWarnType()) switch (log.getWarnType()) {
                case WARN:
                    warnlog += MessageFormat.format("**{0}** • {1} » Warn\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                    break;
                case MUTE:
                    warnlog += MessageFormat.format("**{0}** • {1} » Mute\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                    break;
                case BAN:
                    warnlog += MessageFormat.format("**{0}** • {1} » Ban\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                    break;
                default:
                    warnlog += MessageFormat.format("**{0}** • {1} » ???\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                    break;
            }
        }
        embed.addField(new MessageEmbed.Field("Warnlog", warnlog, false));

        event.getMember().getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(embed.build()).queue();
        });
    }

}
