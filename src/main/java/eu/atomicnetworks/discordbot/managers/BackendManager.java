package eu.atomicnetworks.discordbot.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.Ticket;
import eu.atomicnetworks.discordbot.objects.User;
import eu.atomicnetworks.discordbot.objects.Verify;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class BackendManager {
    
    private DiscordBot discordBot;
    private LoadingCache<String, User> userCache;
    private LoadingCache<String, Ticket> ticketCache;
    private LoadingCache<String, Verify> verifyCache;

    public BackendManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        initCache();
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
        this.verifyCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(10L, TimeUnit.MINUTES).build((CacheLoader) new CacheLoader<String, Verify>() {
            @Override
            public Verify load(String id) throws Exception {
                CompletableFuture<Verify> completableFuture = new CompletableFuture<>();
                discordBot.getVerifyManager().getVerify(id, result -> {
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
        this.discordBot.getTicketManager().createTicket(ticket, (Ticket t) -> {
        });
    }
    
    public void addTicketMessage(String id, Message message) {
        Ticket.TicketMessage ticketMessage = new Ticket.TicketMessage();
        ticketMessage.setUserId(message.getAuthor().getId());
        ticketMessage.setUserName(message.getAuthor().getName());
        ticketMessage.setUserAvatar(message.getAuthor().getAvatarUrl());
        ticketMessage.setMessage(message.getContentRaw());
        this.getTicket(id).getMessages().add(ticketMessage);
        this.discordBot.getTicketManager().saveTicket(this.getTicket(id));
    }

    public LoadingCache<String, Verify> getVerifyCache() {
        return verifyCache;
    }
    
    public Verify getVerify(String id) {
        try {
            return this.verifyCache.get(id);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public boolean hasRole(Member member, String name) {
        List<Role> roles = member.getRoles();
        Role targetRole = roles.stream().filter(role -> role.getName().equals(name)).findFirst().orElse(null);
        if(targetRole == null) {
            return false;
        }
        return true;
    }
    
    public boolean hasPermissionPower3(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer") || hasRole(member, "Moderator") || hasRole(member, "Supporter");
    }
    
    public boolean hasPermissionPower2(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer") || hasRole(member, "» Competence I Teamleading");
    }
    
    public boolean hasPermissionPower1(Member member) {
        return hasRole(member, "Manager") || hasRole(member, "Administrator") || hasRole(member, "Developer");
    }
    
}
