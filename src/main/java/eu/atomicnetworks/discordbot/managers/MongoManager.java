package eu.atomicnetworks.discordbot.managers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import eu.atomicnetworks.discordbot.DiscordBot;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class MongoManager {
    
    private final DiscordBot discordBot;
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> users;
    private MongoCollection<Document> tickets;

    public MongoManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); 
        try {
            this.client = MongoClients.create(new ConnectionString("mongodb://127.0.0.1"));
            this.database = client.getDatabase("discordbot");
            this.users = this.database.getCollection("users");
            this.tickets = this.database.getCollection("tickets");
            this.discordBot.consoleInfo("The connection to the MongoDB database has been established.");
        } catch(MongoException ex) {
            discordBot.consoleError("The connection to the MongoDB database could not be established.");
            Logger.getLogger(MongoManager.class.getName()).log(Level.SEVERE, null, ex);
            Runtime.getRuntime().exit(0);
        }
    }
    
    public MongoCollection<Document> getCollection(String name) {
        return this.database.getCollection(name);
    }
    
    public MongoClient getClient() {
        return client;
    }
    
    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getUsers() {
        return users;
    }

    public MongoCollection<Document> getTickets() {
        return tickets;
    }
    
}
