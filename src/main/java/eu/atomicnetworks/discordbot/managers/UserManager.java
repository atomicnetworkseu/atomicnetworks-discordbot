package eu.atomicnetworks.discordbot.managers;

import com.mongodb.client.model.Filters;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.User;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bson.Document;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class UserManager {
    
    private DiscordBot discordBot;

    public UserManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }
    
    public void getUser(String id, Consumer<User> consumer) {
        discordBot.getMongoManager().getUsers().find(Filters.eq("id", id)).first((Document t, Throwable thrwbl) -> {
            if(t == null) {
                User user = new User();
                user.setId(id);
                user.setUsername("???");
                user.setLevel(1);
                user.setXp(0);
                user.setWarnPoints(0);
                user.setCookies(0);
                
                User.Voting voting = new User.Voting();
                voting.setVoted_at(0);
                voting.setVoted_end(0);
                
                user.setVoting(voting);
                
                t = discordBot.getGson().fromJson(discordBot.getGson().toJson(user), Document.class);
                discordBot.getMongoManager().getUsers().insertOne(t, (Void t1, Throwable thrwbl1) -> {
                    consumer.accept(user);
                });
            } else {
                User user = discordBot.getGson().fromJson(t.toJson(), User.class);
                consumer.accept(user);
            }
        });
    }
    
    public void getTopUser(int limit, Consumer<List<User>> consumer) {
        List<User> list = new ArrayList<>();
        discordBot.getMongoManager().getUsers().find().limit(limit).sort(Filters.eq("xp", -1)).forEach(document -> {
            User user = discordBot.getGson().fromJson(document.toJson(), User.class);
            list.add(user);
        }, (Void t, Throwable thrwbl) -> {
            consumer.accept(list);
        }); 
    }
    
    public void saveUser(User user) {
        Document document = discordBot.getGson().fromJson(discordBot.getGson().toJson(user), Document.class);
        discordBot.getMongoManager().getUsers().replaceOne(Filters.eq("id", user.getId()), document, (result, t) -> {
        });
    }
    
}
