package eu.atomicnetworks.discordbot.managers;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.Filters;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.enums.TicketType;
import eu.atomicnetworks.discordbot.objects.Ticket;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.bson.Document;

/**
 *
 * @author kacpe
 */
public class TicketManager {

    private DiscordBot discord;

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

    public void createChannel(GuildMessageReactionAddEvent event, TicketType ticketType) {
        Role everyoneRole = this.discord.getJda().getRolesByName("🪐 Community", true).stream().findFirst().orElse(null);
        Role supportRole = this.discord.getJda().getRolesByName("Supporter", true).stream().findFirst().orElse(null);
        Role moderatorRole = this.discord.getJda().getRolesByName("Moderator", true).stream().findFirst().orElse(null);
        event.getChannel().retrieveMessageById(event.getMessageId()).queue((message) -> {
            event.getChannel().removeReactionById(message.getId(), ":atomic:736627104992591883", event.getUser()).queue();
            event.getChannel().removeReactionById(message.getId(), ":playatomic:734613241581404271", event.getUser()).queue();
            event.getChannel().removeReactionById(message.getId(), ":gamingatomic:734611793187700736", event.getUser()).queue();
        });
        this.countTickets((Integer t) -> {
            Ticket ticket = new Ticket();
            ticket.setId("ticket-" + (t + 1));
            
            Ticket.TicketUser ticketUser = new Ticket.TicketUser();
            ticketUser.setId(event.getMember().getUser().getId());
            ticketUser.setUsername(event.getMember().getUser().getName());
            ticket.setCreatedBy(ticketUser);
            
            ticket.setTicketType(ticketType);
            ticket.setMessages(new ArrayList<>());
            event.getGuild().createTextChannel(ticket.getId(), event.getChannel().getParent()).queue((channel) -> {
                channel.createPermissionOverride(everyoneRole).setDeny(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(supportRole).setAllow(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(moderatorRole).setAllow(Permission.VIEW_CHANNEL).queue();
                channel.createPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue();
                ticket.setChannelId(channel.getId());
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Supportsystem", null, "https://images.discordapp.net/avatars/697517106287345737/07be164c270546a8c976063bc71939fc.png?size=512");
                embed.setDescription("Welcome to the support " + event.getMember().getUser().getAsMention() + ",\nplease describe your problem while waiting for an answer from our team members. The next free supporter will help you then. 👾");
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
        for (Ticket.TicketMessage message : ticket.getMessages()) {
            if(!contributorIDs.contains(message.getUserId())) {
                contributorIDs.add(message.getUserId());
            }
            messages += MessageFormat.format("   {0} » {2}\n", message.getUserName(), message.getUserId(), message.getMessage());
        }
        for(String id : contributorIDs) {
            contributors += "<@" + id + ">\n";
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        embed.addField("Ticketcreator", "<@" + ticket.getCreatedBy().getId() + ">", true);
        embed.addField("Ticket-ID", ticket.getId(), true);
        embed.addField("Contributors", contributors, true);
        TextChannel ticketLogChannel = (TextChannel) this.discord.getJda().getGuildById(this.discord.getGuildId()).getChannels().stream().filter(t -> t.getId().equals(this.discord.getTicketLogChannelId())).findFirst().orElse(null);
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
