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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
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
        Role role = this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).getRoleById("780093467639414804");
        TextChannel textChannel = (TextChannel) this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).getChannels().stream().filter(t -> t.getId().equals(this.discordBot.getUpvoteChannelId())).findFirst().orElse(null);
        
        if (user.getVoting().getVoted_end() > System.currentTimeMillis()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(149, 79, 180));
            if(voting.getVotingProvider() == VotingProvider.TOPGG) {
                embed.setAuthor("top.gg", null, "https://cdn.atomicnetworks.eu/discord/voting/topgg.png");
            } else if(voting.getVotingProvider() == VotingProvider.DBL) {
                embed.setAuthor("discordbotlist.com", null, "https://cdn.atomicnetworks.eu/discord/voting/dbl.png");
            } else if(voting.getVotingProvider() == VotingProvider.BOATS) {
                embed.setAuthor("discord.boats", null, "https://cdn.atomicnetworks.eu/discord/voting/discordboats.png");
            }
            embed.setDescription("Thank you very much for your vote, <@" + user.getId() + ">!\nAs a gift, you get the `😵 Voted` rank for another 24 hours.");
            textChannel.sendMessage(embed.build()).queue();
            user.getVoting().setVoteCount(user.getVoting().getVoteCount() + 1);
            user.getVoting().setVoted_end(System.currentTimeMillis() + 86400000);
            user.setXp(user.getXp()+10);
            this.discordBot.getUserManager().saveUser(user);
            
            this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).retrieveMemberById(voting.getUserId()).queue((t1) -> {
                if(t1 == null) {
                    System.out.println("MEMBER IS NULL. (VOTED ROLLE WAS NOT ADDED)");
                    return;
                }
                if(t1.getRoles().stream().filter((t2) -> t2.getId().equals(role.getId())).findFirst().orElse(null) == null) {
                    this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).addRoleToMember(t1.getIdLong(), role).queue();
                } 
            });
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        if(voting.getVotingProvider() == VotingProvider.TOPGG) {
            embed.setAuthor("top.gg", null, "https://cdn.atomicnetworks.eu/discord/voting/topgg.png");
        } else if(voting.getVotingProvider() == VotingProvider.DBL) {
            embed.setAuthor("discordbotlist.com", null, "https://cdn.atomicnetworks.eu/discord/voting/dbl.png");
        } else if(voting.getVotingProvider() == VotingProvider.BOATS) {
            embed.setAuthor("discord.boats", null, "https://cdn.atomicnetworks.eu/discord/voting/discordboats.png");
        }
        embed.setDescription("Thank you very much for your vote, <@" + user.getId() + ">!\nAs a gift, you get the `😵 Voted` rank for 24 hours.");
        textChannel.sendMessage(embed.build()).queue();

        user.getVoting().setVoteCount(user.getVoting().getVoteCount() + 1);
        user.getVoting().setVoted_end(System.currentTimeMillis() + 86400000);
        user.setXp(user.getXp()+10);
        this.discordBot.getUserManager().saveUser(user);
        
        this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).retrieveMemberById(voting.getUserId()).queue((t1) -> {
            if(t1 == null) {
                System.out.println("MEMBER IS NULL. (VOTED ROLLE WAS NOT ADDED)");
                return;
            }
            this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).addRoleToMember(t1.getIdLong(), role).queue();
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
