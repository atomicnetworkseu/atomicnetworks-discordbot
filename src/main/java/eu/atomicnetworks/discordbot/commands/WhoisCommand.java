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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
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

        if (!this.discord.getBackendManager().hasPermissionPower2(event.getMember())) return;
        event.getMessage().delete().queue();

        String userId;
        if (message.getMentionedUsers().isEmpty()) {
            userId = args[1];
        } else {
            userId = (message.getMentionedMembers().stream().findFirst().orElse(null)).getId();
        }
        User user = this.discord.getBackendManager().getUser(userId);

        if (user.getId().equals(this.discord.getJda().getSelfUser().getId())) return;
        if (user.getId().equals("697517106287345737")) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        this.discord.getJda().retrieveUserById(user.getId()).queue((t1) -> {
            if(t1 == null) return;
            embed.setAuthor(t1.getName(), null, t1.getAvatarUrl());
            embed.setDescription(MessageFormat.format("The user {0} is currently **level {1}** and has **{2} warning points**.\n", "<@" + user.getId() + ">", user.getLevel(), String.valueOf(user.getWarn().getWarnPoints()))
                    + MessageFormat.format("He has already listened **{0} minutes** to music and has a total of **{1} votes**.", String.valueOf(user.getStreamTimeMin()), String.valueOf(user.getVoting().getVoteCount())));
            String warnlog = "";
            List<User.Warn.WarnLog> logs = user.getWarn().getWarnLog();
            if (!logs.isEmpty()) {
                Collections.reverse(logs);
                for (User.Warn.WarnLog log : logs) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm");
                    if (null != log.getWarnType()) {
                        switch (log.getWarnType()) {
                            case WARN:
                                warnlog += MessageFormat.format("{0} I {1} » has issued a **warn**.\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                                break;
                            case MUTE:
                                warnlog += MessageFormat.format("{0} I {1} » has issued a **mute**.\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                                break;
                            case BAN:
                                warnlog += MessageFormat.format("{0} I {1} » has issued a **ban**.\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                                break;
                            default:
                                warnlog += MessageFormat.format("{0} I {1} » has issued a **???**.\n", simpleDateFormat.format(new Date(log.getStart_at())), "<@" + log.getCreator() + ">");
                                break;
                        }
                    }
                }
                embed.addField(new MessageEmbed.Field("**Warnlog:**", warnlog, false));
            }

            Member member = event.getMember();
            if(member == null) return;
            member.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(embed.build()).queue();
            });
        });
    }

}
