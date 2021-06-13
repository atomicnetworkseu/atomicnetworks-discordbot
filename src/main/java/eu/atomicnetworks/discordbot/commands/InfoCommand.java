package eu.atomicnetworks.discordbot.commands;

import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class InfoCommand {
    
    private final DiscordBot discord;

    public InfoCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        if(!event.getChannel().getId().equals(this.discord.getCommandChannelId())) return;
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("Helpdesk » Information", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
        embed.setDescription("First of all, thank you for being part of this great community on our Discord ✨.\nHere you can find some contact options and more information about our projects.\n\n** **");
        embed.addField("Support", "**Mail**\nsupport@atomicnetworks.eu\n**TeamSpeak**\natomicnetworks.eu", true);
		embed.addField("Instagram", "[Klick](https://go.atomicnetworks.eu/instagram)", true);
        embed.addField("YouTube", "[Klick](https://go.atomicnetworks.eu/youtube)", true);
        embed.addField("Websites", "[atomicnetworks](https://go.atomicnetworks.eu/)\n[atomicradio](https://go.atomicnetworks.eu/radio)\n[atomicgaming](https://go.atomicnetworks.eu/gaming)", true);
        embed.addField("Twitter", "[atomicnetworks](https://go.atomicnetworks.eu/twitter)\n[atomicradio](https://go.atomicnetworks.eu/radio/twitter)\n[atomicgaming](https://go.atomicnetworks.eu/gaming/twitter)", true);
        embed.addField("Invitelink", "https://go.atomicnetworks.eu/discord", true);
        event.getChannel().sendMessage(embed.build()).queue();
    }
    
}
