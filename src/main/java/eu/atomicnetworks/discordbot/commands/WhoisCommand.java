package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.text.MessageFormat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author kacpe
 */
public class WhoisCommand {
    
    private final DiscordBot discord;

    public WhoisCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        String userId = "";
        if(message.getMentionedUsers().isEmpty()) {
            userId = args[1];
        } else {
            userId = (message.getMentionedMembers().stream().findFirst().orElse(null)).getId();
        }
        User user = this.discord.getBackendManager().getUser(userId);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Warnsystem", null, "https://images.discordapp.net/avatars/697517106287345737/07be164c270546a8c976063bc71939fc.png?size=512");
        embed.addField(new MessageEmbed.Field("User", "<@" + user.getId() + ">", true));
        embed.addField(new MessageEmbed.Field("Level", MessageFormat.format("Level {0}", user.getLevel()), true));
        embed.addField(new MessageEmbed.Field("Votes", "null", true));
        embed.addField(new MessageEmbed.Field("Cookies", String.valueOf(user.getCookies()), true));
        embed.addField(new MessageEmbed.Field("Warnpoints", String.valueOf(user.getWarnPoints()), true));
        embed.addBlankField(true);
        embed.addField(new MessageEmbed.Field("Warnlog", "**5.1.2020** • HappyFr1tz » Kick", false));
        
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
