package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class InfoCommand {
    
    private final DiscordBot discord;

    public InfoCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if(!event.getChannel().getId().equals(this.discord.getCommandChannelId())) {
            return;
        }
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Helpdesk » Information", null, "https://images.discordapp.net/avatars/697517106287345737/07be164c270546a8c976063bc71939fc.png?size=512");
        embed.setDescription("** **\nFirst of all, thank you for being on our Discord and being part of our community! :heart:\n\n** **");
        embed.addField("Support", "**Mail** I support@atomicnetworks.eu\n**TeamSpeak** I atomicnetworks.eu\n**Phone** I +49 40 228518870", true);
        embed.addField("Websites", "[atomicnetworks](https://go.atomicnetworks.eu/)\n[atomicradio](https://go.atomicnetworks.eu/radio)\n[atomicgaming](https://go.atomicnetworks.eu/gaming)", true);
        embed.addField("Twitter", "[atomicnetworks](https://go.atomicnetworks.eu/twitter)\n[atomicradio](https://go.atomicnetworks.eu/radio/twitter)\n[atomicgaming](https://go.atomicnetworks.eu/gaming/twitter)", true);
        embed.addField("Instagram", "[atomicnetworks](https://go.atomicnetworks.eu/instagram)", true);
        embed.addField("YouTube", "[atomicgaming](https://go.atomicnetworks.eu/youtube)", true);
        embed.addField("Invitelink", "https://go.atomicnetworks.eu/discord", true);
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
