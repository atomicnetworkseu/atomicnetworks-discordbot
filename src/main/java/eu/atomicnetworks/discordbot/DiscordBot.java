package eu.atomicnetworks.discordbot;

import com.google.gson.Gson;
import eu.atomicnetworks.discordbot.handlers.EventHandler;
import eu.atomicnetworks.discordbot.managers.BackendManager;
import eu.atomicnetworks.discordbot.managers.HookManager;
import eu.atomicnetworks.discordbot.managers.LoggerManager;
import eu.atomicnetworks.discordbot.managers.MongoManager;
import eu.atomicnetworks.discordbot.managers.TicketManager;
import eu.atomicnetworks.discordbot.managers.UserManager;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 *
 */
public class DiscordBot {

    private JDA jda;
    private Gson gson;

    private LoggerManager loggerManager;
    private MongoManager mongoManager;
    private UserManager userManager;
    private TicketManager ticketManager;
    private BackendManager backendManager;
    private HookManager hookManager;

    private String guildId;
    private String achievementChannelId;
    private String upvoteChannelId;
    private String roleChannelId;
    private String welcomeChannelId;
    private String commandChannelId;
    private String teamlogChannelId;
    private String ticketChannelId;
    private String ticketLogChannelId;
    private String teamchatChannelId;

    private String musicVoiceChannelId;

    public static void main(String[] args) {
        new DiscordBot().loadBanner();
        new DiscordBot().init();
    }

    private void init() {
        this.gson = new Gson();

        this.loggerManager = new LoggerManager();
        this.mongoManager = new MongoManager(this);
        this.userManager = new UserManager(this);
        this.ticketManager = new TicketManager(this);
        this.backendManager = new BackendManager(this);
        this.hookManager = new HookManager(this);

        this.guildId = "734477710319026217";
        this.roleChannelId = "734477712139223133";
        this.achievementChannelId = "734477712844128373";
        this.upvoteChannelId = "824751418371735563";
        this.welcomeChannelId = "734477712139223132";
        this.commandChannelId = "734477712844128374";
        this.teamlogChannelId = "734477713028415566";
        this.ticketChannelId = "734477712592338981";
        this.ticketLogChannelId = "734477713028415565";
        this.teamchatChannelId = "734477713028415558";

        this.musicVoiceChannelId = "836332151728766986";

        JDABuilder builder = JDABuilder.createDefault("Nzk2ODQ5MDE5MzA4NDc0NDE5.X_d5eg.jf4MILv8PkXTZUYOxrfRMJ2Pb4E");
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setActivity(Activity.watching("atnw.eu/discord"));
        builder.addEventListeners(new EventHandler(this));

        try {
            this.jda = builder.build();
            Timer sendTimer = new Timer(1, (ActionEvent e) -> {
                TextChannel rolesChannel = (TextChannel) jda.getGuildById(guildId).getChannels().stream().filter(t -> t.getId().equals(roleChannelId)).findFirst().orElse(null);
                if (!(new MessageHistory(rolesChannel).retrievePast(1).complete()).isEmpty()) {
                    (new MessageHistory(rolesChannel).retrievePast(1).complete()).get(0).delete().queue();
                }
                rolesChannel.sendMessage("**Welcome on our Discord,**\nplease read <#734477712139223135> and choose one of the following groups:\n\n<:playatomic:734613241581404271> **Radio**\n"
                        + "This group gives you access to the radio channels and allows you to keep up to date about the latest features, contribute to the development and exchange with the community about our radio.\n\n"
                        + "<:gamingatomic:734611793187700736> **Gaming**\n"
                        + "With this group you get access to the channels of our gamingprojects and can decide together with the community how we develop the project further and find new persons to play with.\n\n"
                        + "» We wish you a nice stay on our Discord.").queue((message) -> {
                            long messageId = message.getIdLong();
                            rolesChannel.addReactionById(messageId, ":playatomic:734613241581404271").queue();
                            rolesChannel.addReactionById(messageId, ":gamingatomic:734611793187700736").queue();
                        });

                TextChannel supportChannel = (TextChannel) jda.getGuildById(guildId).getChannels().stream().filter(t -> t.getId().equals(ticketChannelId)).findFirst().orElse(null);
                if (!(new MessageHistory(supportChannel).retrievePast(1).complete()).isEmpty()) {
                    (new MessageHistory(supportChannel).retrievePast(1).complete()).get(0).delete().queue();
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Supportsystem", null, "https://cdn.atomicnetworks.eu/discord/icon.png");
                embed.setDescription("You have a problem, want to send us feedback or something else to our support?\nBy clicking on the emoji below, you can open a ticket where our team will be happy to help you. ✨");
                supportChannel.sendMessage(embed.build()).queue((message) -> {
                    long messageId = message.getIdLong();
                    supportChannel.addReactionById(messageId, "📬").queue();
                });
            });
            sendTimer.setInitialDelay(10000);
            sendTimer.setRepeats(false);
            //sendTimer.start();

            Timer warnEndTimer = new Timer(10000, (ActionEvent e) -> {
                this.userManager.getActiveMutedUsers((List<User> t) -> {
                    t.stream().forEach((user) -> {
                        if (System.currentTimeMillis() >= user.getWarn().getActiveWarnEnd()) {
                            this.backendManager.setMuted(user.getId(), false);
                            Role role = this.jda.getGuildById(this.guildId).getRoleById("769862174024925204");
                            this.jda.getGuildById(this.guildId).retrieveMemberById(user.getId()).queue((t1) -> {
                                if (t1 == null) {
                                    System.out.println("MEMBER IS NULL. (MUTED ROLLE WAS NOT REMOVED)");
                                    return;
                                }
                                if (t1.getRoles().stream().filter((t2) -> t2.getId().equals(role.getId())).findFirst().orElse(null) != null) {
                                    this.getJda().getGuildById(this.getGuildId()).removeRoleFromMember(t1, role).queue();
                                }
                            });
                        }
                    });
                });
                this.userManager.getAllUsers((List<User> t) -> {
                    t.stream().forEach((user) -> {
                        if (user.getVoting().getVoted_end() == 0) {
                            return;
                        }
                        if (System.currentTimeMillis() >= user.getVoting().getVoted_end()) {
                            user.getVoting().setVoted_end(0);
                            this.userManager.saveUser(user);
                            Role role = this.getJda().getGuildById(this.getGuildId()).getRoleById("780093467639414804");
                            this.getJda().getGuildById(this.getGuildId()).retrieveMemberById(user.getId()).queue((t1) -> {
                                if (t1 == null) {
                                    System.out.println("MEMBER IS NULL. (VOTED ROLLE WAS NOT REMOVED)");
                                    return;
                                }
                                if (t1.getRoles().stream().filter((t2) -> t2.getId().equals(role.getId())).findFirst().orElse(null) != null) {
                                    this.getJda().getGuildById(this.getGuildId()).removeRoleFromMember(t1, role).queue();
                                }
                            });
                        }
                    });
                });
            });
            warnEndTimer.setInitialDelay(10000);
            warnEndTimer.setRepeats(true);
            warnEndTimer.start();

        } catch (LoginException ex) {
            Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadBanner() {
        System.out.println("\n       _                  _                    _ _       \n"
                + "      | |                (_)                  | (_)      \n"
                + "  __ _| |_ ___  _ __ ___  _  ___ _ __ __ _  __| |_  ___  \n"
                + " / _` | __/ _ \\| '_ ` _ \\| |/ __| '__/ _` |/ _` | |/ _ \\ \n"
                + "| (_| | || (_) | | | | | | | (__| | | (_| | (_| | | (_) |\n"
                + " \\__,_|\\__\\___/|_| |_| |_|_|\\___|_|  \\__,_|\\__,_|_|\\___/ \n\n"
                + " atomicnetworks.eu discord\n 2020 Copyright (c) by atomicnetworks.eu to present.\n Author: Kacper Mura\n");
    }

    public Gson getGson() {
        return gson;
    }

    public JDA getJda() {
        return jda;
    }

    public LoggerManager getLoggerManager() {
        return loggerManager;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public void consoleInfo(String text) {
        this.loggerManager.sendInfo(text);
    }

    public void consoleWarning(String text) {
        this.loggerManager.sendWarning(text);
    }

    public void consoleError(String text) {
        this.loggerManager.sendError(text);
    }

    public void consoleDebug(String text) {
        this.loggerManager.sendDebug(text);
    }

    public String getGuildId() {
        return guildId;
    }

    public String getCommandChannelId() {
        return commandChannelId;
    }

    public String getTeamlogChannelId() {
        return teamlogChannelId;
    }

    public String getTicketLogChannelId() {
        return ticketLogChannelId;
    }

    public String getAchievementChannelId() {
        return achievementChannelId;
    }

    public String getUpvoteChannelId() {
        return upvoteChannelId;
    }

    public String getMusicVoiceChannelId() {
        return musicVoiceChannelId;
    }

    public String getWelcomeChannelId() {
        return welcomeChannelId;
    }

    public String getTicketChannelId() {
        return ticketChannelId;
    }

    public String getRoleChannelId() {
        return roleChannelId;
    }

    public String getTeamchatChannelId() {
        return teamchatChannelId;
    }

}
