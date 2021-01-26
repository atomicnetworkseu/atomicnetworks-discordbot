package eu.atomicnetworks.discordbot.objects;

import eu.atomicnetworks.discordbot.enums.WarnReason;
import java.util.List;

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
    private int streamTime;
    private Warn warn;
    private boolean muted;
    private int cookies;
    private Voting voting;
    
    public static class Warn {
        
        private int warnPoints;
        private String activeWarnReason;
        private long activeWarnEnd;
        private String activeWarnCreator;
        private List<WarnLog> warnLog;
        
        public static class WarnLog {
            
            private int id;
            private String reason;
            private String creator;
            private long start_at;
            private long end_at;
            private WarnReason.WarnTypes warnTypes;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
            }

            public String getCreator() {
                return creator;
            }

            public void setCreator(String creator) {
                this.creator = creator;
            }

            public long getStart_at() {
                return start_at;
            }

            public void setStart_at(long start_at) {
                this.start_at = start_at;
            }

            public long getEnd_at() {
                return end_at;
            }

            public void setEnd_at(long end_at) {
                this.end_at = end_at;
            }

            public WarnReason.WarnTypes getWarnType() {
                return warnTypes;
            }

            public void setWarnType(WarnReason.WarnTypes warnTypes) {
                this.warnTypes = warnTypes;
            }
            
        }

        public String getActiveWarnCreator() {
            return activeWarnCreator;
        }

        public void setActiveWarnCreator(String acitveWarnCreator) {
            this.activeWarnCreator = acitveWarnCreator;
        }

        public int getWarnPoints() {
            return warnPoints;
        }

        public void setWarnPoints(int warnPoints) {
            this.warnPoints = warnPoints;
        }

        public String getActiveWarnReason() {
            return activeWarnReason;
        }

        public void setActiveWarnReason(String activeWarnReason) {
            this.activeWarnReason = activeWarnReason;
        }

        public long getActiveWarnEnd() {
            return activeWarnEnd;
        }

        public void setActiveWarnEnd(long activeWarnEnd) {
            this.activeWarnEnd = activeWarnEnd;
        }

        public List<WarnLog> getWarnLog() {
            return warnLog;
        }

        public void setWarnLog(List<WarnLog> warnLog) {
            this.warnLog = warnLog;
        }
        
    }
    
    public static class Voting {
        
        private long voteCount;
        private long voted_end;

        public long getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(long voteCount) {
            this.voteCount = voteCount;
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

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
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

    public Warn getWarn() {
        return warn;
    }

    public void setWarn(Warn warn) {
        this.warn = warn;
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

    public void setStreamTime(int streamTime) {
        this.streamTime = streamTime;
    }

    public int getStreamTimeMin() {
        return streamTime;
    }
    
}
