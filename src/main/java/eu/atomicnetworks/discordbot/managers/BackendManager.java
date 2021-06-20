package eu.atomicnetworks.discordbot.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.Ticket;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class BackendManager {
    
    private DiscordBot discordBot;
    private final Timer timer;
    private LoadingCache<String, User> userCache;
    private LoadingCache<String, Ticket> ticketCache;
    private HashMap<String, Long> levelTimeout;
    
    public BackendManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        initCache();
        this.timer = new Timer(60000, (ActionEvent e) -> {
            if(this.discordBot.getGuild() == null) return;
            VoiceChannel voiceChannel = this.discordBot.getGuild().getVoiceChannelById(this.discordBot.getMusicVoiceChannelId());
            if(voiceChannel == null) return;
            voiceChannel.getMembers().stream().forEach(t -> {
                if(t.getId().equals("697517106287345737")) return;
                User user = this.getUser(t.getId());
                if(user == null) return;
                if(t.getVoiceState().isDeafened()) return;
                if(levelTimeout.containsKey(t.getId())) {
                    if(System.currentTimeMillis() >= levelTimeout.get(t.getId())) {
                        levelTimeout.remove(t.getId());
                    }
                    return;
                }
                user.setStreamTime(user.getStreamTimeMin()+1);
                this.addXp(t.getId(), 1*this.getXPBoost(t));
                if(this.getRemainingXp(user.getId()) <= 1) {
                    this.addLevel(user.getId(), 1);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(149, 79, 180));
                    embed.setAuthor(t.getUser().getName(), null, t.getUser().getAvatarUrl());
                    embed.setDescription("**Congratulations**, you have now reached level **" + this.getLevel(user.getId()) + "**! <a:blobgifrolling:771743022282440815>");
                    TextChannel textChannel = (TextChannel) this.discordBot.getGuild().getChannels().stream().filter(t1 -> t1.getId().equals(this.discordBot.getAchievementChannelId())).findFirst().orElse(null);
                    textChannel.sendMessage(embed.build()).queue();
                }
            });
        });
        this.timer.setRepeats(true);
        this.timer.setInitialDelay(5000);
        this.timer.start();
        this.levelTimeout = new HashMap<>();
    }
    
    private void initCache() {
        this.userCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(10L, TimeUnit.MINUTES).build((CacheLoader) new CacheLoader<String, User>() {
            @Override
            public User load(String id) throws Exception {
                CompletableFuture<User> completableFuture = new CompletableFuture<>();
                discordBot.getUserManager().getUser(id, result -> {
                    completableFuture.complete(result);
                });
                return completableFuture.get();
            }
        });
        this.ticketCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(10L, TimeUnit.MINUTES).build((CacheLoader) new CacheLoader<String, Ticket>() {
            @Override
            public Ticket load(String id) throws Exception {
                CompletableFuture<Ticket> completableFuture = new CompletableFuture<>();
                discordBot.getTicketManager().getTicket(id, result -> {
                    if(result.getId().equals("NOT FOUND!")) {
                        completableFuture.complete(null);
                    } else {
                        completableFuture.complete(result);
                    }
                });
                return completableFuture.get();
            }
        });
    }

    public LoadingCache<String, User> getUserCache() {
        return userCache;
    }
    
    public User getUser(String id) {
        try {
            return this.userCache.get(id);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public String getUsername(String id) {
        return this.getUser(id).getUsername();
    }
    
    public void setUsername(String id, String name) {
        this.getUser(id).setUsername(name);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public int getLevel(String id) {
        return this.getUser(id).getLevel();
    }
    
    public void setLevel(String id, int level) {
        this.getUser(id).setLevel(level);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public void addLevel(String id, int level) {
        this.setLevel(id, this.getLevel(id)+level);
    }
    
    public int getXp(String id) {
        return this.getUser(id).getXp();
    }
    
    public void setXp(String id, int xp) {
        this.getUser(id).setXp(xp);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public void addXp(String id, int xp) {
        this.setXp(id, this.getXp(id)+xp);
    }
    
    public int getCurrentXp(String id) {
        return (this.getXp(id)-(150*(this.getLevel(id)-1)));
    }
    
    public int getRemainingXp(String id) {
        return (this.getMaxXp(id)-this.getCurrentXp(id));
    }
    
    public int getMaxXp(String id) {
        return (150*this.getLevel(id));
    }
    
    public boolean isMuted(String id) {
        return this.getUser(id).isMuted();
    }
    
    public void setMuted(String id, boolean muted) {
        this.getUser(id).setMuted(muted);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public User.Warn getWarn(String id) {
        return this.getUser(id).getWarn();
    }
    
    public int getCookies(String id) {
        return this.getUser(id).getCookies();
    }
    
    public void setCookies(String id, int cookies) {
        this.getUser(id).setCookies(cookies);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public void addCookies(String id, int cookies) {
        this.setCookies(id, this.getCookies(id)+cookies);
    }

    public LoadingCache<String, Ticket> getTicketCache() {
        return ticketCache;
    }
    
    public Ticket getTicket(String id) {
        try {
            return this.ticketCache.get(id);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public void createTicket(Ticket ticket) {
        this.discordBot.getTicketManager().createTicket(ticket, (Ticket t) -> {});
    }
    
    public void addTicketMessage(String id, Message message) {
        Ticket.TicketMessage ticketMessage = new Ticket.TicketMessage();
        ticketMessage.setUserId(message.getAuthor().getId());
        ticketMessage.setUserName(message.getAuthor().getName());
        ticketMessage.setUserDiscriminator(message.getAuthor().getDiscriminator());
        ticketMessage.setUserAvatar(message.getAuthor().getAvatarUrl());
        ticketMessage.setTimestamp(System.currentTimeMillis());
        ticketMessage.setMessage(message.getContentRaw());
        this.getTicket(id).getMessages().add(ticketMessage);
        this.discordBot.getTicketManager().saveTicket(this.getTicket(id));
    }
    
    public boolean hasRole(Member member, String name) {
        List<Role> roles = member.getRoles();
        Role targetRole = roles.stream().filter(role -> role.getName().equals(name)).findFirst().orElse(null);
        return targetRole != null;
    }
    
    public boolean hasRoleById(Member member, String id) {
        List<Role> roles = member.getRoles();
        Role targetRole = roles.stream().filter(role -> role.getId().equals(id)).findFirst().orElse(null);
        return targetRole != null;
    }
    
    public int getXPBoost(Member member) {
        if(hasRoleById(member, "780093467639414804") || isTeamMember(member)) {
            return 3;
        } else if(hasRoleById(member, "740318497883553986") || hasRoleById(member, "734477710319026224")) {
            return 2;
        } else {
            return 1;
        }
    }
    
    public boolean hasPermissionPower3(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer") || hasRole(member, "Supporter");
    }
    
    public boolean hasPermissionPower2(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer") || hasRole(member, "Teamleading");
    }
    
    public boolean hasPermissionPower1(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer");
    }
    
    public boolean isTeamMember(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Teamleading") || hasRole(member, "Developer") || hasRole(member, "Technician") || hasRole(member, "Supporter") || hasRole(member, "Streamer") || hasRole(member, "Content") || hasRole(member, "Designer") || hasRole(member, "Builder");
    }

    public HashMap<String, Long> getLevelTimeout() {
        return levelTimeout;
    }
    
}
