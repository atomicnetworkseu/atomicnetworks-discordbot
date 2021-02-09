package eu.atomicnetworks.discordbot.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.enums.WarnReason;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class WarnCommand {
    
    private final DiscordBot discord;
    private final WebhookClient webhookClient;

    public WarnCommand(DiscordBot discord) {
        this.discord = discord;
        this.webhookClient = WebhookClient.withUrl("https://discord.com/api/webhooks/796848169920888852/kQpYbCZOiMedIZqFqDcHxzwBxOYbxxYqOFa000OP7U0nNKHQuDWOs9Zz3bmedzBHksWE");
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if(!this.discord.getBackendManager().hasPermissionPower3(event.getMember())) {
            return;
        }
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        
        if(args.length == 1 || message.getMentionedMembers().isEmpty()) {
            embed.setDescription("Hey 👋, you need to enter one of the following numbers so that your warning can be processed.\n" +
                "1. **Provocation**\n" +
                "2. **Single advertisement**\n" +
                "3. **Multiple advertising**\n" +
                "4. **Support exploitation**\n" +
                "5. **Behavior**\n" +
                "6. **Insult & inappropriate behavior**\n" +
                "7. **Severe insult**\n" +
                "8. **Spamming**\n" +
                "9. **Ghost pinging**\n" +
                "10. **Pornography**\n" +
                "11. **Publishing private data of others**\n" +
                "12. **Racism**\n" +
                "13. **Impersonating a team member**");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        
        Member target = message.getMentionedMembers().stream().findFirst().orElse(null);
        if(this.discord.getBackendManager().isTeamMember(target)) {
            embed.setDescription("I'm sorry, you can't warn your colleagues.");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        int warnId = Integer.valueOf(args[2]);
        
        switch(warnId) {
            case 1:
                this.warnUser(event.getMember(), target, WarnReason.PROVOCATION);
                break;
            case 2:
                this.warnUser(event.getMember(), target, WarnReason.SINGLE_ADVERTISEMENT);
                break;
            case 3:
                this.muteUser(event.getMember(), target, WarnReason.MULTIPLE_ADVERTISING, false);
                break;
            case 4:
                this.warnUser(event.getMember(), target, WarnReason.SUPPORT_EXPLOITATION);
                break;
            case 5:
                this.warnUser(event.getMember(), target, WarnReason.BEHAVIOR);
                break;
            case 6:
                this.muteUser(event.getMember(), target, WarnReason.INSULT_INAPPROPRIATE_BEHAVIOR, false);
                break;
            case 7:
                this.banUser(event.getMember(), target, WarnReason.SEVERE_INSULT);
                break;
            case 8:
                this.warnUser(event.getMember(), target, WarnReason.SPAMMING);
                break;
            case 9:
                this.muteUser(event.getMember(), target, WarnReason.GHOST_PINGING, false);
                break;
            case 10:
                this.banUser(event.getMember(), target, WarnReason.PORNOGRAPHY);
                break;
            case 11:
                this.muteUser(event.getMember(), target, WarnReason.PUBLISHING_PRIVATE_DATA_OF_OTHERS, false);
                break;
            case 12:
                this.banUser(event.getMember(), target, WarnReason.RACISM);
                break;
            case 13:
                this.muteUser(event.getMember(), target, WarnReason.IMPERSONATING_A_TEAM_MEMBER, false);
                break;
        }
    }
    
    public void warnUser(Member member, Member target, WarnReason warnReason) {
        User userTarget = this.discord.getBackendManager().getUser(target.getUser().getId());
        userTarget.getWarn().setWarnPoints(userTarget.getWarn().getWarnPoints()+1);
        
        if(userTarget.getWarn().getWarnPoints() >= 3) {
            this.muteUser(target, target, WarnReason.REPEATED_MISCONDUCT, true);
            return;
        }
        
        User.Warn.WarnLog warnLog = new User.Warn.WarnLog();
        warnLog.setId(warnReason.getId());
        warnLog.setReason(warnReason.getReason());
        warnLog.setStart_at(System.currentTimeMillis());
        warnLog.setEnd_at(System.currentTimeMillis());
        warnLog.setCreator(member.getUser().getId());
        warnLog.setWarnType(WarnReason.WarnTypes.WARN);
        userTarget.getWarn().getWarnLog().add(warnLog);
        this.discord.getUserManager().saveUser(userTarget);
        
        EmbedBuilder teamEmbed = new EmbedBuilder();
        teamEmbed.setColor(new Color(149, 79, 180));
        teamEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        teamEmbed.setDescription("**Successful**, you have warned " + target.getAsMention() + " for **" + warnReason.getReason() + "**.");
        
        EmbedBuilder userEmbed = new EmbedBuilder();
        userEmbed.setColor(new Color(149, 79, 180));
        userEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        userEmbed.setDescription("You have been **warned** on our server because of **" + warnReason.getReason() + "**.");
        
        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(teamEmbed.build()).queue();
        });
        target.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(userEmbed.build()).queue();
        });
        
        WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        webhookEmbedBuilder.setColor(9785268);
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Action", "Warn " + target.getAsMention() + " because of " + warnReason.getReason() + "."));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Punishment", "Warn"));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Teammember", member.getAsMention()));
        this.webhookClient.send(webhookEmbedBuilder.build());
    }
    
    public void muteUser(Member member, Member target, WarnReason warnReason, boolean warnPointMute) {
        User userTarget = this.discord.getBackendManager().getUser(target.getUser().getId());
        if(warnPointMute) {
            userTarget.getWarn().setWarnPoints(0);
        } else {
            userTarget.getWarn().setWarnPoints(userTarget.getWarn().getWarnPoints()+1);
        }
        userTarget.setMuted(true);
        userTarget.getWarn().setActiveWarnReason(warnReason.getReason());
        userTarget.getWarn().setActiveWarnEnd(System.currentTimeMillis()+(3600000*warnReason.getHours()));
        userTarget.getWarn().setActiveWarnCreator(member.getUser().getId());
        
        User.Warn.WarnLog warnLog = new User.Warn.WarnLog();
        warnLog.setId(warnReason.getId());
        warnLog.setReason(warnReason.getReason());
        warnLog.setStart_at(System.currentTimeMillis());
        warnLog.setEnd_at(System.currentTimeMillis()+(3600000*warnReason.getHours()));
        warnLog.setCreator(member.getUser().getId());
        warnLog.setWarnType(WarnReason.WarnTypes.MUTE);
        userTarget.getWarn().getWarnLog().add(warnLog);
        this.discord.getUserManager().saveUser(userTarget);
        
        EmbedBuilder teamEmbed = new EmbedBuilder();
        teamEmbed.setColor(new Color(149, 79, 180));
        teamEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        teamEmbed.setDescription("**Successful**, you have muted " + target.getAsMention() + " for **" + warnReason.getReason() + "**.");
        
        EmbedBuilder userEmbed = new EmbedBuilder();
        userEmbed.setColor(new Color(149, 79, 180));
        userEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        userEmbed.setDescription("You have been **muted** on our server because of **" + warnReason.getReason() + "** for **" + warnReason.getHours() + " hours**.");
        
        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(teamEmbed.build()).queue();
        });
        target.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(userEmbed.build()).queue();
        });
        
        Role role = this.discord.getJda().getGuildById(this.discord.getGuildId()).getRoleById("769862174024925204");
        this.discord.getJda().getGuildById(this.discord.getGuildId()).addRoleToMember(target, role).queue();
        
        WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        webhookEmbedBuilder.setColor(9785268);
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Action", "Warn " + target.getAsMention() + " because of " + warnReason.getReason() + "."));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Punishment", "Mute"));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Teammember", member.getAsMention()));
        this.webhookClient.send(webhookEmbedBuilder.build());
    }
    
    public void banUser(Member member, Member target, WarnReason warnReason) {
        User userTarget = this.discord.getBackendManager().getUser(target.getUser().getId());
        userTarget.getWarn().setWarnPoints(userTarget.getWarn().getWarnPoints()+1);
        userTarget.setMuted(false);
        userTarget.getWarn().setActiveWarnReason(warnReason.getReason());
        userTarget.getWarn().setActiveWarnEnd(System.currentTimeMillis());
        userTarget.getWarn().setActiveWarnCreator(member.getUser().getId());
        
        User.Warn.WarnLog warnLog = new User.Warn.WarnLog();
        warnLog.setId(warnReason.getId());
        warnLog.setReason(warnReason.getReason());
        warnLog.setStart_at(System.currentTimeMillis());
        warnLog.setEnd_at(System.currentTimeMillis());
        warnLog.setCreator(member.getUser().getId());
        warnLog.setWarnType(WarnReason.WarnTypes.BAN);
        userTarget.getWarn().getWarnLog().add(warnLog);
        this.discord.getUserManager().saveUser(userTarget);
        
        EmbedBuilder teamEmbed = new EmbedBuilder();
        teamEmbed.setColor(new Color(149, 79, 180));
        teamEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        teamEmbed.setDescription("**Successful**, you have banned " + target.getAsMention() + " for **" + warnReason.getReason() + "**.");
        
        EmbedBuilder userEmbed = new EmbedBuilder();
        userEmbed.setColor(new Color(149, 79, 180));
        userEmbed.setAuthor("Warnsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        userEmbed.setDescription("You have been **banned** on our server because of **" + warnReason.getReason() + "**.\nIf you think that this was an accident or if you want to change your behavior in the future you can contact us by mail at `support@atomicnetworks.eu`.");
        
        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(teamEmbed.build()).queue();
        });
        target.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(userEmbed.build()).queue();
        });
        
        this.discord.getJda().getGuildById(this.discord.getGuildId()).ban(target, 0, warnReason.getReason()).queue();
        
        WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        webhookEmbedBuilder.setColor(9785268);
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Action", "Warn " + target.getAsMention() + " because of " + warnReason.getReason() + "."));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Punishment", "Ban"));
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Teammember", member.getAsMention()));
        this.webhookClient.send(webhookEmbedBuilder.build());
    }
    
}
