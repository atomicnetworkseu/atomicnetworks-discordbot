package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
public class TicketCommand {
    
    private final DiscordBot discord;

    public TicketCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(!this.discord.getBackendManager().hasPermissionPower3(event.getMember())) {
            return;
        }
        
        if(!event.getChannel().getName().startsWith("ticket-")) {
            return;
        }
        
        switch(args[1]) {
            case "add":
                if(message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                    embed.setDescription("You have to specify a user or a group which should be added!");
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;
                }
                
                if(!message.getMentionedMembers().isEmpty()) {
                    Member target = message.getMentionedMembers().stream().findFirst().orElse(null);
                    embed.setDescription("**Successful**, the user " + target.getAsMention() + " was added to the ticket.");
                    event.getChannel().sendMessage(embed.build()).queue();
                    event.getChannel().createPermissionOverride(target).setAllow(Permission.VIEW_CHANNEL).queue();
                } else if(!message.getMentionedRoles().isEmpty()) {
                    Role target = message.getMentionedRoles().stream().findFirst().orElse(null);
                    embed.setDescription("**Successful**, the role " + target.getAsMention() + " was added to the ticket.");
                    event.getChannel().sendMessage(embed.build()).queue();
                    event.getChannel().createPermissionOverride(target).setAllow(Permission.VIEW_CHANNEL).queue();
                }
                break;
            case "close":
                this.discord.getTicketManager().sendTicketInfoEmbed(this.discord.getBackendManager().getTicket(event.getChannel().getName()));
                event.getChannel().delete().queue();
                break;
        }
    }
    
}
