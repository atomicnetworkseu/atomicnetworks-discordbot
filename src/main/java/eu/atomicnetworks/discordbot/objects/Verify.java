package eu.atomicnetworks.discordbot.objects;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class Verify {
    
    private String id;
    private String teamspeakId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamspeakId() {
        return teamspeakId;
    }

    public void setTeamspeakId(String teamspeakId) {
        this.teamspeakId = teamspeakId;
    }
    
}
