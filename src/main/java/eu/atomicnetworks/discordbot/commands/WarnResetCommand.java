package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.enums.WarnReason;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class WarnResetCommand {
    
    private final DiscordBot discord;

    public WarnResetCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        
        if(!this.discord.getBackendManager().hasPermissionPower1(event.getMember())) {
            return;
        }
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        
        Member target = message.getMentionedMembers().stream().findFirst().orElse(null);
        if(target == null) {
            embed.setDescription("Member is not on this guild.");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        User userTarget = this.discord.getBackendManager().getUser(target.getUser().getId());
        if(userTarget == null) {
            embed.setDescription("Member was not found in the database.");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        User.Warn.WarnLog warnLog = new User.Warn.WarnLog();
        warnLog.setId(WarnReason.WARN_RESET.getId());
        warnLog.setReason(WarnReason.WARN_RESET.getReason());
        warnLog.setStart_at(System.currentTimeMillis());
        warnLog.setEnd_at(System.currentTimeMillis());
        warnLog.setCreator(event.getAuthor().getId());
        warnLog.setWarnType(WarnReason.WarnTypes.RESET);
        userTarget.getWarn().setWarnPoints(0);
        userTarget.getWarn().getWarnLog().clear();
        userTarget.getWarn().getWarnLog().add(warnLog);
        this.discord.getUserManager().saveUser(userTarget);
        
        embed.setDescription("**Successful**, the entries of the user " + target.getUser().getAsMention() + " were successfully reset.");
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
