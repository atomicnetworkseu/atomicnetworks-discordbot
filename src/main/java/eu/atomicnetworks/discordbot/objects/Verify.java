package eu.atomicnetworks.discordbot.objects;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
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
