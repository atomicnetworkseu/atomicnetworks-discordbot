package eu.atomicnetworks.discordbot.managers;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.listeners.TSListeners;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class QueryManager {
    
    private final DiscordBot discordBot;
    private final TS3Query query;
    private final TS3ApiAsync api;

    public QueryManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        
        TS3Config config = new TS3Config();
        config.setHost("127.0.0.1");
        config.setQueryPort(10011);
        
        this.query = new TS3Query(config);
        this.query.connect();
        
        this.api = query.getAsyncApi();
        api.login("discord", "f9bcg+uj");
        api.selectVirtualServerById(1, "atomicnetworks.eu • discord");
        api.addTS3Listeners(new TSListeners(this.discordBot));
        api.registerEvent(TS3EventType.TEXT_PRIVATE);
    }

    public TS3Query getQuery() {
        return query;
    }

    public TS3ApiAsync getApi() {
        return api;
    }
    
}
