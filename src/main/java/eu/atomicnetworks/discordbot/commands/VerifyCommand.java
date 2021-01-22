package eu.atomicnetworks.discordbot.commands;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import eu.atomicnetworks.discordbot.objects.Verify;
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
public class VerifyCommand {

    private final DiscordBot discord;

    public VerifyCommand(DiscordBot discord) {
        this.discord = discord;
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");

        if (!event.getChannel().getId().equals(this.discord.getCommandChannelId())) {
            return;
        }

        User user = this.discord.getBackendManager().getUser(event.getMember().getUser().getId());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.setAuthor("TeamSpeak", null, "https://cdn.atomicnetworks.eu/discord/icon.png");

        if (args.length != 1) {
            if (args[1].equalsIgnoreCase("link")) {
                if (args.length != 3) {
                    Verify verify = this.discord.getBackendManager().getVerify(user.getId());
                    if (verify.getTeamspeakId().isEmpty()) {
                        embed.setDescription("You have not yet connected your account to TeamSpeak, to connect both use **!teamspeak link** and your **identity**.");
                        event.getChannel().sendMessage(embed.build()).queue();
                    } else {
                        embed.setDescription("Your account is already linked to the identity `" + verify.getTeamspeakId() + "` on our TeamSpeak.");
                        event.getChannel().sendMessage(embed.build()).queue();
                    }
                    return;
                }
                try {
                    this.discord.getVerifyManager().startVerification(args[2], event.getAuthor());
                    embed.setDescription("You have received a message on TeamSpeak which you only have to accept.");
                    event.getChannel().sendMessage(embed.build()).queue();
                } catch(TS3CommandFailedException ex) {
                    embed.setDescription("Unfortunately we could not find the identity on our TeamSpeak, check if the identity is correct and you are connected to our server.");
                    event.getChannel().sendMessage(embed.build()).queue();
                }
            }
        }
    }

}
