package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class PingCommand {
    
    private final DiscordBot discord;

    public PingCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if(!event.getChannel().getName().contains("announcements")) {
            return;
        }
        
        if (!this.discord.getBackendManager().hasPermissionPower2(event.getMember())) {
            return;
        }
        
        message.delete().queue();
        switch(args[1].toLowerCase()) {
            case "everyone":
                event.getChannel().sendMessage("@everyone").queue((x) -> x.delete().queueAfter(5, TimeUnit.SECONDS));
                break;
            case "here":
                event.getChannel().sendMessage("@here").queue((x) -> x.delete().queueAfter(5, TimeUnit.SECONDS));
                break;
            default:
                event.getChannel().sendMessage("@everyone").queue((x) -> x.delete().queueAfter(5, TimeUnit.SECONDS));
                break;
        } 
        
    }
    
}
