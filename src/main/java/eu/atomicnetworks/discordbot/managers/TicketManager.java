package eu.atomicnetworks.discordbot.managers;

import com.mongodb.client.model.Filters;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.Ticket;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.bson.Document;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class TicketManager {

    private final DiscordBot discord;

    public TicketManager(DiscordBot discord) {
        this.discord = discord;
    }

    public void getTicket(String id, Consumer<Ticket> consumer) {
        this.discord.getMongoManager().getTickets().find(Filters.eq("id", id)).first((Document t, Throwable thrwbl) -> {
            if (t != null) {
                Ticket ticket = discord.getGson().fromJson(t.toJson(), Ticket.class);
                consumer.accept(ticket);
            } else {
                Ticket ticket = new Ticket();
                ticket.setId("NOT FOUND!");
                consumer.accept(ticket);
            }
        });
    }

    public void countTickets(Consumer<Integer> consumer) {
        this.discord.getMongoManager().getTickets().countDocuments((Long t, Throwable thrwbl) -> {
            consumer.accept(Integer.valueOf(t.toString()));
        });
    }

    public void createTicket(Ticket ticket, Consumer<Ticket> consumer) {
        this.discord.getMongoManager().getTickets().find(Filters.eq("id", ticket.getId())).first((Document t, Throwable thrwbl) -> {
            if (t == null) {
                t = discord.getGson().fromJson(discord.getGson().toJson(ticket), Document.class);
                discord.getMongoManager().getTickets().insertOne(t, (Void t1, Throwable thrwbl1) -> {
                    consumer.accept(ticket);
                });
            }
        });
    }

    public void saveTicket(Ticket ticket) {
        Document document = discord.getGson().fromJson(discord.getGson().toJson(ticket), Document.class);
        discord.getMongoManager().getTickets().replaceOne(Filters.eq("id", ticket.getId()), document, (result, t) -> {
        });
    }

    public void createChannel(GuildMessageReactionAddEvent event) {
        Role everyoneRole = this.discord.getJda().getRolesByName("🪐 Community", true).stream().findFirst().orElse(null);
        Role supportRole = this.discord.getJda().getRolesByName("Supporter", true).stream().findFirst().orElse(null);
        Member member = event.getGuild().retrieveMemberById("223891083724193792").complete();
        event.getChannel().retrieveMessageById(event.getMessageId()).queue((message) -> {
            event.getChannel().removeReactionById(message.getId(), ":atomic:736627104992591883", event.getUser()).queue();
            event.getChannel().removeReactionById(message.getId(), ":playatomic:734613241581404271", event.getUser()).queue();
            event.getChannel().removeReactionById(message.getId(), ":gamingatomic:734611793187700736", event.getUser()).queue();
        });
        this.countTickets((Integer t) -> {
            Ticket ticket = new Ticket();
            ticket.setId("ticket-0" + (t + 1));
            
            Ticket.TicketUser ticketUser = new Ticket.TicketUser();
            ticketUser.setId(event.getMember().getUser().getId());
            ticketUser.setUsername(event.getMember().getUser().getName());
            ticket.setCreatedBy(ticketUser);
            
            ticket.setMessages(new ArrayList<>());
            event.getGuild().createTextChannel(ticket.getId(), event.getChannel().getParent()).queue((channel) -> {
                channel.createPermissionOverride(everyoneRole).setDeny(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(supportRole).setAllow(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();
                ticket.setChannelId(channel.getId());
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Supportsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
                embed.setDescription("Welcome in our support " + event.getMember().getUser().getAsMention() + ",\nplease describe your problem while waiting for an answer from our team members. The next free supporter will help you then. 👾");
                MessageEmbed messageEmbed = embed.build();
                channel.sendMessage(messageEmbed).queue((message) -> {
                    channel.pinMessageById(message.getId()).queue();
                    message.addReaction("📪").queue();
                });
                channel.sendMessage("@everyone").queue((tagMessage) -> {
                    tagMessage.delete().queue();
                });
                this.discord.getBackendManager().createTicket(ticket);
            });
        });
    }

    public void sendTicketInfoEmbed(Ticket ticket) {
        String contributors = "";
        String messages = "";
        ArrayList<String> contributorIDs = new ArrayList<>();
        messages = ticket.getMessages().stream().map(message -> {
            if(!contributorIDs.contains(message.getUserId())) {
                contributorIDs.add(message.getUserId());
            }
            return message;
        }).map(message -> MessageFormat.format("[{0}] {1} » {2}\n", this.discord.getLoggerManager().getTimestamp(message.getTimestamp()), message.getUserName() + "#" + message.getUserDiscriminator(), message.getMessage())).reduce(messages, String::concat);
        contributors = contributorIDs.stream().map(id -> "<@" + id + ">\n").reduce(contributors, String::concat);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.addField("Ticketcreator", "<@" + ticket.getCreatedBy().getId() + ">", true);
        embed.addField("Ticket-ID", ticket.getId(), true);
        embed.addField("Contributors", contributors, true);
        TextChannel ticketLogChannel = (TextChannel) this.discord.getGuild().getChannels().stream().filter(t -> t.getId().equals(this.discord.getTicketLogChannelId())).findFirst().orElse(null);
        File file = new File("./tickets/" + ticket.getId() + ".txt");
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter("./tickets/" + ticket.getId() + ".txt");
            fileWriter.write(messages);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(TicketManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ticketLogChannel.sendFile(new File("./tickets/" + ticket.getId() + ".txt"), new AttachmentOption[0]).queue((message) -> {ticketLogChannel.sendMessage(embed.build()).queue();});
    }
}
