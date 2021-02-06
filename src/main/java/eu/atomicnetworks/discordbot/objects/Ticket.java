package eu.atomicnetworks.discordbot.objects;

import eu.atomicnetworks.discordbot.enums.TicketStatus;
import eu.atomicnetworks.discordbot.enums.TicketType;
import java.util.List;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class Ticket {
    
    private String id;
    private TicketUser createdBy;
    private String channelId;
    private TicketType ticketType;
    private TicketStatus ticketStatus;
    private List<TicketMessage> messages;
    
    public static class TicketUser {
        
        private String id;
        private String username;

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
        
    }
    
    public static class TicketMessage {
        
        private String userName;
        private String userId;
        private String userAvatar;
        private String message;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TicketUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(TicketUser createdBy) {
        this.createdBy = createdBy;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public List<TicketMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TicketMessage> messages) {
        this.messages = messages;
    }
    
}
