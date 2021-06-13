package eu.atomicnetworks.discordbot.managers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.enums.VotingProvider;
import eu.atomicnetworks.discordbot.objects.User;
import eu.atomicnetworks.discordbot.objects.Voting;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class HookManager {
    
    private final DiscordBot discordBot;
    private final String authorizationToken;
    private HttpServer httpServer;

    public HookManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.authorizationToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5NzUxNzEwNjI4NzM0NTczNyIsImJvdCI6ImF0b21pY3JhZGlvIiwiZGV2ZWxvcGVyIjoiS2FjcGVyIE11cmEgKFZvY2FsWmVybykifQ.QV2tL0UK9lmPIqA4iITraENv3LgCPwknEQz4sohFlVU";
        this.start();
        this.initContext();
    }
    
    private void start() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(6969), 0);
            this.httpServer.start();
        } catch (IOException ex) {
            Logger.getLogger(HookManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initContext() {
        this.httpServer.createContext("/webhook/topgg", (HttpExchange he) -> {
            if(he.getRequestMethod().equals("OPTIONS")) {
                he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                he.getResponseHeaders().add("Access-Control-Allow-Methods", "POST,OPTIONS");
                he.sendResponseHeaders(204, -1);
                return;
            }
            if(!he.getRequestMethod().equals("POST")) {
                he.getResponseBody().close();
                return;
            }
            if(!this.isAuthorized(he)) {
                return;
            }
            JSONObject body = this.getRequestBody(he);
            
            Voting voting = new Voting();
            voting.setVotingProvider(VotingProvider.TOPGG);
            String voter = body.getString("user");
            voting.setUserId(voter);
            he.sendResponseHeaders(201, -1);
            this.executeVote(voting);
        });
        this.httpServer.createContext("/webhook/dbl", (HttpExchange he) -> {
            if(he.getRequestMethod().equals("OPTIONS")) {
                he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                he.getResponseHeaders().add("Access-Control-Allow-Methods", "POST,OPTIONS");
                he.sendResponseHeaders(204, -1);
                return;
            }
            if(!he.getRequestMethod().equals("POST")) {
                he.getResponseBody().close();
                return;
            }
            if(!this.isAuthorized(he)) {
                return;
            }
            JSONObject body = this.getRequestBody(he);
            
            Voting voting = new Voting();
            voting.setVotingProvider(VotingProvider.DBL);
            String voter = body.getString("id");
            voting.setUserId(voter);
            he.sendResponseHeaders(201, -1);
            this.executeVote(voting);
        });
        this.httpServer.createContext("/webhook/boats", (HttpExchange he) -> {
            if(he.getRequestMethod().equals("OPTIONS")) {
                he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                he.getResponseHeaders().add("Access-Control-Allow-Methods", "POST,OPTIONS");
                he.sendResponseHeaders(204, -1);
                return;
            }
            if(!he.getRequestMethod().equals("POST")) {
                he.getResponseBody().close();
                return;
            }
            if(!this.isAuthorized(he)) {
                return;
            }
            JSONObject body = this.getRequestBody(he);
            
            Voting voting = new Voting();
            voting.setVotingProvider(VotingProvider.BOATS);
            String voter = body.getJSONObject("user").getString("id");
            voting.setUserId(voter);
            he.sendResponseHeaders(201, -1);
            this.executeVote(voting);
        });
        this.httpServer.createContext("/webhook/status", (HttpExchange he) -> {
            if(he.getRequestMethod().equals("OPTIONS")) {
                he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                he.getResponseHeaders().add("Access-Control-Allow-Methods", "POST,OPTIONS");
                he.sendResponseHeaders(204, -1);
                return;
            }
            if(!he.getRequestMethod().equals("POST")) {
                he.getResponseBody().close();
                return;
            }
            JSONObject body = this.getRequestBody(he);
            
            if(!body.getString("type").equals("changed")) {
                he.getResponseBody().close();
                return;
            }
            if(body.getString("status").equals("dead")) {
                TextChannel textChannel = (TextChannel) this.discordBot.getGuild().getTextChannelById(this.discordBot.getTeamchatChannelId());
                if(textChannel == null) return;
                
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(234, 44, 82));
                String description = "Some services are temporarily unavailable, at [status.atomicnetworks.eu](https://status.atomicnetworks.eu) you will find all further and current information on the current outage.\n"
                        + "During this period, parts of our infrastructure are not accessible or only accessible to a limited extent.\n\n"
                        + "**Technical information**:\n";
                
                JSONArray array = body.getJSONArray("replicas");
                description = array.toList().stream().map(x -> x.toString()).map(replica -> "• No answer from **" + replica.split(" ")[1].split(":")[0] + "** at `" + replica.split(" ")[1].split(":")[1] + "://" + replica.split(" ")[1].split("://")[1] + "`.\n").reduce(description, String::concat);
                embed.setDescription(description);
                
                textChannel.sendMessage("<@&789284548159471626>").queue((message) -> {
                    textChannel.sendMessage(embed.build()).queue((embedMessage) -> {
                        embedMessage.addReaction("✅").queue();
                        message.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                });
                he.sendResponseHeaders(201, -1);
            } else if(body.getString("status").equals("sick")) {
                TextChannel textChannel = (TextChannel) this.discordBot.getGuild().getTextChannelById(this.discordBot.getTeamchatChannelId());
                if(textChannel == null) return;
                
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(249, 164, 18));
                String description = "Some services may not be able to be accessed because they may be under attack or have to handle a heavy load.\n"
                        + "At [status.atomicnetworks.eu](https://status.atomicnetworks.eu) you will find all further and current information on the current disruption.\n\n"
                        + "**Technical information**:\n";
                
                JSONArray array = body.getJSONArray("replicas");
                description = array.toList().stream().map(x -> x.toString()).map(replica -> "• Missing reply from **" + replica.split(" ")[1].split(":")[0] + "**.\n").reduce(description, String::concat);
                embed.setDescription(description);
                
                textChannel.sendMessage("<@&789284548159471626>").queue((message) -> {
                    textChannel.sendMessage(embed.build()).queue((embedMessage) -> {
                        embedMessage.addReaction("✅").queue();
                        message.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                });
                he.sendResponseHeaders(201, -1);
            }
        });
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
    
    private void executeVote(Voting voting) {
        User user = this.discordBot.getBackendManager().getUser(voting.getUserId());

        if (user == null) {
            return;
        }
        Role role = this.discordBot.getGuild().getRoleById("780093467639414804");
        if(role == null) return;
        TextChannel textChannel = (TextChannel) this.discordBot.getGuild().getTextChannelById(this.discordBot.getUpvoteChannelId());
        if(textChannel == null) return;
        
        if (user.getVoting().getVoted_end() > System.currentTimeMillis()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(149, 79, 180));
            if(null != voting.getVotingProvider()) switch (voting.getVotingProvider()) {
                case TOPGG:
                    embed.setAuthor("top.gg", null, "https://cdn.atomicnetworks.eu/discord/voting/topgg.png");
                    break;
                case DBL:
                    embed.setAuthor("discordbotlist.com", null, "https://cdn.atomicnetworks.eu/discord/voting/dbl.png");
                    break;
                case BOATS:
                    embed.setAuthor("discord.boats", null, "https://cdn.atomicnetworks.eu/discord/voting/discordboats.png");
                    break;
                default:
                    break;
            }
            user.getVoting().setVoteCount(user.getVoting().getVoteCount() + 1);
            user.getVoting().setVoted_end(System.currentTimeMillis() + 86400000);
            user.setXp(user.getXp()+10);
            this.discordBot.getUserManager().saveUser(user);
            
            this.discordBot.getGuild().retrieveMemberById(voting.getUserId()).queue((t1) -> {
                if(t1 == null) {
                    return;
                }
                embed.setDescription("Thank you very much for your vote, **" + t1.getUser().getName() + "**#" + t1.getUser().getDiscriminator() + "!\nAs a gift, you get the `😵 Voted` rank for another 24 hours.");
                textChannel.sendMessage(embed.build()).queue();
                if(t1.getRoles().stream().filter((t2) -> t2.getId().equals(role.getId())).findFirst().orElse(null) == null) {
                    this.discordBot.getGuild().addRoleToMember(t1.getIdLong(), role).queue();
                } 
            });
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        if(null != voting.getVotingProvider()) switch (voting.getVotingProvider()) {
            case TOPGG:
                embed.setAuthor("top.gg", null, "https://cdn.atomicnetworks.eu/discord/voting/topgg.png");
                break;
            case DBL:
                embed.setAuthor("discordbotlist.com", null, "https://cdn.atomicnetworks.eu/discord/voting/dbl.png");
                break;
            case BOATS:
                embed.setAuthor("discord.boats", null, "https://cdn.atomicnetworks.eu/discord/voting/discordboats.png");
                break;
            default:
                break;
        }

        user.getVoting().setVoteCount(user.getVoting().getVoteCount() + 1);
        user.getVoting().setVoted_end(System.currentTimeMillis() + 86400000);
        user.setXp(user.getXp()+10);
        this.discordBot.getUserManager().saveUser(user);
        
        this.discordBot.getGuild().retrieveMemberById(voting.getUserId()).queue((t1) -> {
            if(t1 == null) {
                return;
            }
            embed.setDescription("Thank you very much for your vote, **" + t1.getUser().getName() + "**#" + t1.getUser().getDiscriminator() + "!\nAs a gift, you get the `😵 Voted` rank for 24 hours.");
            textChannel.sendMessage(embed.build()).queue();
            this.discordBot.getGuild().addRoleToMember(t1.getIdLong(), role).queue();
        });
    }
    
    public boolean isAuthorized(HttpExchange httpExchange) {
        String authorization = httpExchange.getRequestHeaders().getFirst("Authorization");
        JSONObject unauthorizedError = new JSONObject("{code: 401, message: 'Unauthorized. Provide a valid token and try again.'}");
        if (authorization == null) {
            try {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(401, unauthorizedError.toString().length());
                httpExchange.getResponseBody().write(unauthorizedError.toString().getBytes());
                httpExchange.getResponseBody().close();
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        if (!authorization.equals(authorizationToken)) {
            try {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(401, unauthorizedError.toString().length());
                httpExchange.getResponseBody().write(unauthorizedError.toString().getBytes());
                httpExchange.getResponseBody().close();
                return false;
            } catch (IOException ex) {
                return false;
            }
        } else {
            return true;
        }
    }
    
    public JSONObject getRequestBody(HttpExchange httpExchange) {
        StringBuilder requestBody = new StringBuilder();
        new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().forEach((t) -> requestBody.append(t));
        return new JSONObject(requestBody.toString());
    }
    
}
