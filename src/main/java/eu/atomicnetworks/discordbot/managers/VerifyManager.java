package eu.atomicnetworks.discordbot.managers;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.mongodb.client.model.Filters;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.Verify;
import eu.atomicnetworks.discordbot.objects.VerifyRequest;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.function.Consumer;
import javax.swing.Timer;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class VerifyManager {
    
    private DiscordBot discordBot;
    private HashMap<String, VerifyRequest> waiting;

    public VerifyManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.waiting = new HashMap();
        Timer verifyEndTimer = new Timer(10000, (ActionEvent e) -> {
            this.waiting.entrySet().stream().forEach((t) -> {
                if(System.currentTimeMillis() >= t.getValue().getTimeout()) {
                    this.waiting.remove(t.getKey());
                }
            });
        });
        verifyEndTimer.setInitialDelay(10000);
        verifyEndTimer.setRepeats(true);
        verifyEndTimer.start();
    }
    
    public void getVerify(String id, Consumer<Verify> consumer) {
        discordBot.getMongoManager().getVerifys().find(Filters.eq("id", id)).first((Document t, Throwable thrwbl) -> {
            if(t == null) {
                Verify verify = new Verify();
                verify.setId(id);
                verify.setTeamspeakId("");
                t = discordBot.getGson().fromJson(discordBot.getGson().toJson(verify), Document.class);
                discordBot.getMongoManager().getVerifys().insertOne(t, (Void t1, Throwable thrwbl1) -> {
                    consumer.accept(verify);
                });
            } else {
                Verify verify = discordBot.getGson().fromJson(t.toJson(), Verify.class);
                consumer.accept(verify);
            }
        });
    }
    
    public void saveVerify(Verify verify) {
        Document document = discordBot.getGson().fromJson(discordBot.getGson().toJson(verify), Document.class);
        discordBot.getMongoManager().getVerifys().replaceOne(Filters.eq("id", verify.getId()), document, (result, t) -> {
        });
    }

    public HashMap<String, VerifyRequest> getWaiting() {
        return waiting;
    }
    
    public void startVerification(String teamspeakId, User user) throws TS3CommandFailedException {
        Client client = this.discordBot.getQueryManager().getApi().getClientByUId(teamspeakId).getUninterruptibly();
        ClientInfo clientInfo = this.discordBot.getQueryManager().getApi().getClientByUId(client.getUniqueIdentifier()).getUninterruptibly();
        this.discordBot.getQueryManager().getApi().sendPrivateMessage(clientInfo.getId(), "[B]Discord[/B] » Please confirm with [B]!accept[/B] that [B]" + user.getName() + "[/B] is allowed to connect to your account.");
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setUser(user);
        verifyRequest.setClient(client);
        verifyRequest.setClientInfo(clientInfo);
        verifyRequest.setTimeout(System.currentTimeMillis()+900000);
        this.waiting.put(teamspeakId, verifyRequest);
    }
    
    public void completeVerification(VerifyRequest verifyRequest) {
        Verify verify = this.discordBot.getBackendManager().getVerify(verifyRequest.getUser().getId());
        verify.setTeamspeakId(verifyRequest.getClient().getUniqueIdentifier());
        this.discordBot.getVerifyManager().saveVerify(verify);
        this.waiting.remove(verifyRequest.getClient().getUniqueIdentifier());
        this.discordBot.getQueryManager().getApi().sendPrivateMessage(verifyRequest.getClientInfo().getId(), "[B]Discord[/B] » Successful, you can now start using more features on our Discord.");
    }
    
}
