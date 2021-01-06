package eu.atomicnetworks.discordbot.objects;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class User {
    
    private String id;
    private String username;
    private int level;
    private int xp;
    private int warnPoints;
    private int cookies;
    private Voting voting;
    
    public static class Voting {
        
        private long voted_at;
        private long voted_end;

        public long getVoted_at() {
            return voted_at;
        }

        public void setVoted_at(long voted_at) {
            this.voted_at = voted_at;
        }

        public long getVoted_end() {
            return voted_end;
        }

        public void setVoted_end(long voted_end) {
            this.voted_end = voted_end;
        }
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getWarnPoints() {
        return warnPoints;
    }

    public void setWarnPoints(int warnPoints) {
        this.warnPoints = warnPoints;
    }

    public int getCookies() {
        return cookies;
    }

    public void setCookies(int cookies) {
        this.cookies = cookies;
    }

    public Voting getVoting() {
        return voting;
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }
    
}
