package eu.atomicnetworks.discordbot.managers;

import com.mongodb.client.model.Filters;
import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.objects.Verify;
import java.util.function.Consumer;
import org.bson.Document;

/**
 *
 * @author kacpe
 */
public class VerifyManager {
    
    private DiscordBot discordBot;

    public VerifyManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }
    
    public void getVerify(String id, Consumer<Verify> consumer) {
        discordBot.getMongoManager().getVerifys().find(Filters.eq("id", id)).first((Document t, Throwable thrwbl) -> {
            if(t == null) {
                consumer.accept(null);
            } else {
                Verify verify = discordBot.getGson().fromJson(t.toJson(), Verify.class);
                consumer.accept(verify);
            }
        });
    }
    
    public void createVerify(String id, String teamspeakId, Consumer<Verify> consumer) {
        discordBot.getMongoManager().getVerifys().find(Filters.eq("id", id)).first((Document t, Throwable thrwbl) -> {
            if(t == null) {
                Verify verify = new Verify();
                verify.setId(id);
                verify.setTeamspeakId(teamspeakId);
                t = discordBot.getGson().fromJson(discordBot.getGson().toJson(verify), Document.class);
                discordBot.getMongoManager().getUsers().insertOne(t, (Void t1, Throwable thrwbl1) -> {
                    consumer.accept(verify);
                });
            }
        });
    }
    
}
