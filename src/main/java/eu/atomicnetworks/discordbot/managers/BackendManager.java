package eu.atomicnetworks.discordbot.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    
    public int getWarnPoints(String id) {
        return this.getUser(id).getWarnPoints();
    }
    
    public void setWarnPoints(String id, int points) {
        this.getUser(id).setWarnPoints(points);
        this.discordBot.getUserManager().saveUser(this.getUser(id));
    }
    
    public void addWarnPoints(String id, int points) {
        this.setWarnPoints(id, this.getWarnPoints(id)+points);
    }
    
}
