package eu.atomicnetworks.discordbot.listeners;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import me.hopedev.topggwebhooks.Webhook;
import me.hopedev.topggwebhooks.WebhookEvent;
import me.hopedev.topggwebhooks.WebhookListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

/**
 *
 * @author kacpe
 */
public class TopGGListener {

    private final DiscordBot discord;
    private final Webhook webhook;

    public TopGGListener(DiscordBot discord) {
        this.discord = discord;
        this.webhook = new Webhook(6969, "topgg", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5NzUxNzEwNjI4NzM0NTczNyIsImJvdCI6ImF0b21pY3JhZGlvIiwiZGV2ZWxvcGVyIjoiS2FjcGVyIE11cmEgKFZvY2FsWmVybykifQ.QV2tL0UK9lmPIqA4iITraENv3LgCPwknEQz4sohFlVU", new WebhookListener() {
            
            @Override
            public void onWebhookRequest(WebhookEvent we) {
                JSONObject jsonObject = new JSONObject(we.getRequestString());
                if(jsonObject == null) {
                    return;
                }
                
                String voter = jsonObject.getString("user");
                User user = discord.getBackendManager().getUser(voter);
                
                if(user == null) {
                    return;
                }
                
                Role role = discord.getJda().getGuildById(discord.getGuildId()).getRolesByName("😵 Voted", true).stream().findFirst().orElse(null);
                TextChannel textChannel = (TextChannel) discord.getJda().getGuildById(discord.getGuildId()).getChannels().stream().filter(t -> t.getId().equals(discord.getAchievementChannelId())).findFirst().orElse(null);
                
                if(user.getVoting().getVoted_end() > System.currentTimeMillis()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(149, 79, 180));
                    embed.setAuthor("Voting", null, "https://top.gg/images/dblnew.png");
                    embed.setDescription("Thank you very much for your vote, <@" + user.getId() + ">!\nAs a gift, you get the " + role.getAsMention() + " rank for another 24 hours.");
                    textChannel.sendMessage(embed.build()).queue();
                    user.getVoting().setVoteCount(user.getVoting().getVoteCount()+1);
                    user.getVoting().setVoted_end(System.currentTimeMillis()+86400000);
                    discord.getUserManager().saveUser(user);
                    return;
                }
                
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Voting", null, "https://top.gg/images/dblnew.png");
                embed.setDescription("Thank you very much for your vote, <@" + user.getId() + ">!\nAs a gift, you get the " + role.getAsMention() + " rank for 24 hours.");
                textChannel.sendMessage(embed.build()).queue();
                
                user.getVoting().setVoteCount(user.getVoting().getVoteCount()+1);
                user.getVoting().setVoted_end(System.currentTimeMillis()+86400000);
                discord.getUserManager().saveUser(user);
                
                discord.getJda().getGuildById(discord.getGuildId()).addRoleToMember(voter, role).queue();
            }
            
        });
        this.webhook.start();
    }
    
    
    
}
