package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author kacpe
 */
public class LevelWarnCommand {
    
    private final DiscordBot discord;

    public LevelWarnCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if(!this.discord.getBackendManager().hasPermissionPower1(event.getMember())) {
            return;
        }
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(message.getMentionedMembers().isEmpty()) {
            return;
        }
        
        Member target = message.getMentionedMembers().stream().findFirst().orElse(null);
        embed.setDescription("**Successful**, the user <@" + target.getId() + "> was excluded from the music ranking for 24 hours!");
        this.discord.getBackendManager().getLevelTimeout().put(target.getId(), System.currentTimeMillis()+86400000);
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
