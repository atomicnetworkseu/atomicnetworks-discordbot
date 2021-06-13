package eu.atomicnetworks.discordbot.objects;

import eu.atomicnetworks.discordbot.enums.VotingProvider;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicradio.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class Voting {
    
    private VotingProvider votingProvider;
    private String userId;

    public VotingProvider getVotingProvider() {
        return votingProvider;
    }

    public void setVotingProvider(VotingProvider votingProvider) {
        this.votingProvider = votingProvider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
