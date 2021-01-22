package eu.atomicnetworks.discordbot.enums;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public enum WarnReason {
    
    PROVOCATION(1, "Provocation", -1, WarnTypes.WARN),
    SINGLE_ADVERTISEMENT(2, "Single advertisement", -1, WarnTypes.WARN),
    MULTIPLE_ADVERTISING(3, "Multiple advertising", 6, WarnTypes.MUTE),
    SUPPORT_EXPLOITATION(4, "Support exploitation", -1, WarnTypes.WARN),
    BEHAVIOR(5, "Behavior", -1, WarnTypes.WARN),
    INSULT_INAPPROPRIATE_BEHAVIOR(6, "Insult & inappropriate behavior", 12, WarnTypes.MUTE),
    SEVERE_INSULT(7, "Severe insult", -1, WarnTypes.BAN),
    SPAMMING(8, "Spamming", -1, WarnTypes.WARN),
    GHOST_PINGING(9, "Ghost pinging", 24, WarnTypes.MUTE),
    PORNOGRAPHY(10, "Pornography", -1, WarnTypes.BAN),
    PUBLISHING_PRIVATE_DATA_OF_OTHERS(11, "Publishing private data of others", 168, WarnTypes.MUTE),
    RACISM(12, "Racism", -1, WarnTypes.BAN),
    IMPERSONATING_A_TEAM_MEMBER(13, "Impersonating a team member", 48, WarnTypes.MUTE),
    REPEATED_MISCONDUCT(1010, "repeated misconduct", 24, WarnTypes.MUTE);
    
    private int id;
    private String reason;
    private int hours;
    private WarnTypes warnTypes;

    private WarnReason(int id, String reason, int hours, WarnTypes warnTypes) {
        this.id = id;
        this.reason = reason;
        this.hours = hours;
        this.warnTypes = warnTypes;
    }

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

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public WarnTypes getWarnTypes() {
        return warnTypes;
    }

    public void setWarnTypes(WarnTypes warnTypes) {
        this.warnTypes = warnTypes;
    }
    
    public static enum WarnTypes {
        WARN,MUTE,BAN;
    }
    
}
