package eu.atomicnetworks.discordbot.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import eu.atomicnetworks.discordbot.DiscordBot;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author kacpe
 */
public class ClearCommand {

    private final DiscordBot discord;
    private final WebhookClient webhookClient;

    public ClearCommand(DiscordBot discord) {
        this.discord = discord;
        this.webhookClient = WebhookClient.withUrl("https://discord.com/api/webhooks/796427560540045353/-55cLUrNDfQabMJH3ymJI0c9fbpymOAIOJsCXjmBTQ0SYY3XZ8ei3iHOBQfSTHQRkZMR");
    }

    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");

        MessageHistory messageHistory = new MessageHistory(event.getChannel());
        List<Message> messages = messageHistory.retrievePast(Integer.valueOf(args[1])).complete();
        event.getChannel().deleteMessages(messages).queue();
        
        if(!this.discord.getBackendManager().hasPermissionPower3(event.getMember())) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        if(args.length == 1) {
            embed.setDescription("You must specify how many messages you want to delete!");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        try {
            embed.setDescription("**Successful**, you have deleted a total of **" + Integer.valueOf(args[1]) + "** messages in " + event.getChannel().getAsMention() + ".");

            WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
            webhookEmbedBuilder.setColor(9785268);
            webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Action", "Clear"));
            webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(true, "Team member", event.getMember().getUser().getAsMention()));
            this.webhookClient.send(webhookEmbedBuilder.build());

            event.getMember().getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(embed.build()).queue();
            });
        } catch (NumberFormatException ex) {
            embed.setDescription("You must enter a valid number.");
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }

}
