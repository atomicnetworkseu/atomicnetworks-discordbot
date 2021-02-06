package eu.atomicnetworks.discordbot.objects;

import eu.atomicnetworks.discordbot.enums.VotingProvider;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
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
