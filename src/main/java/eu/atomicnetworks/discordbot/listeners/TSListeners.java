package eu.atomicnetworks.discordbot.listeners;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import eu.atomicnetworks.discordbot.DiscordBot;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class TSListeners implements TS3Listener {
    
    private final DiscordBot discordBot;

    public TSListeners(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onTextMessage(TextMessageEvent event) {
        if (event.getTargetMode() != TextMessageTargetMode.CLIENT) {
            return;
        }
        String[] args = event.getMessage().split(" ");
        if(args[0].equalsIgnoreCase("!accept")) {
            if(this.discordBot.getVerifyManager().getWaiting().containsKey(event.getInvokerUniqueId())) {
                this.discordBot.getVerifyManager().completeVerification(this.discordBot.getVerifyManager().getWaiting().get(event.getInvokerUniqueId()));
            }
        }
    }

    @Override
    public void onClientJoin(ClientJoinEvent event) {
    }

    @Override
    public void onClientLeave(ClientLeaveEvent event) {
    }

    @Override
    public void onServerEdit(ServerEditedEvent event) {
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent event) {
    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent event) {
    }

    @Override
    public void onClientMoved(ClientMovedEvent event) {
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent event) {
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent event) {
    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent event) {
    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent event) {
    }
    
}
