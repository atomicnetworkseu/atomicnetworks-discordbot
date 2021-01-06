package eu.atomicnetworks.discordbot;

import com.google.gson.Gson;
import eu.atomicnetworks.discordbot.commands.ClearCommand;
import eu.atomicnetworks.discordbot.commands.CookieCommand;
import eu.atomicnetworks.discordbot.commands.HelpCommand;
import eu.atomicnetworks.discordbot.commands.InfoCommand;
import eu.atomicnetworks.discordbot.commands.LevelCommand;
import eu.atomicnetworks.discordbot.commands.MagicMusselCommand;
import eu.atomicnetworks.discordbot.commands.NewsCommand;
import eu.atomicnetworks.discordbot.commands.RankingCommand;
import eu.atomicnetworks.discordbot.commands.TicketCommand;
import eu.atomicnetworks.discordbot.commands.WhoisCommand;
import eu.atomicnetworks.discordbot.enums.TicketType;
import eu.atomicnetworks.discordbot.managers.BackendManager;
import eu.atomicnetworks.discordbot.managers.LoggerManager;
import eu.atomicnetworks.discordbot.managers.MongoManager;
import eu.atomicnetworks.discordbot.managers.TicketManager;
import eu.atomicnetworks.discordbot.managers.UserManager;
import eu.atomicnetworks.discordbot.objects.Ticket;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.AttachmentOption;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by atomicnetworks.eu to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class DiscordBot {
    
    private JDA jda;
    private Gson gson;
    private Random random;
    
    private LoggerManager loggerManager;
    private MongoManager mongoManager;
    private UserManager userManager;
    private TicketManager ticketManager;
    private BackendManager backendManager;
    
    private HelpCommand helpCommand;
    private InfoCommand infoCommand;
    private LevelCommand levelCommand;
    private MagicMusselCommand magicMusselCommand;
    private NewsCommand newsCommand;
    private RankingCommand rankingCommand;
    private CookieCommand cookieCommand;
    private ClearCommand clearCommand;
    private WhoisCommand whoisCommand;
    private TicketCommand ticketCommand;
    
    private String guildId;
    private String roleChannelId;
    private String welcomeChannelId;
    private String commandChannelId;
    private String teamlogChannelId;
    private String ticketChannelId;
    private String ticketLogChannelId;
   
    public static void main(String[] args) {
        new DiscordBot().loadBanner();
        new DiscordBot().init();
    }
    
    private void init() {
        this.gson = new Gson();
        this.random = new Random();
        
        this.loggerManager = new LoggerManager();
        this.mongoManager = new MongoManager(this);
        this.userManager = new UserManager(this);
        this.ticketManager = new TicketManager(this);
        this.backendManager = new BackendManager(this);
        
        this.helpCommand = new HelpCommand(this);
        this.infoCommand = new InfoCommand(this);
        this.levelCommand = new LevelCommand(this);
        this.magicMusselCommand = new MagicMusselCommand(this);
        this.newsCommand = new NewsCommand(this);
        this.rankingCommand = new RankingCommand(this);
        this.cookieCommand = new CookieCommand(this);
        this.clearCommand = new ClearCommand(this);
        this.whoisCommand = new WhoisCommand(this);
        this.ticketCommand = new TicketCommand(this);
        
        this.guildId = "667439121949523998";
        this.roleChannelId = "796130699991580752";
        this.welcomeChannelId = "796130699991580752";
        this.commandChannelId = "796130699991580752";
        this.teamlogChannelId = "796130699991580752";
        this.ticketChannelId = "796445916630482944";
        this.ticketLogChannelId = "796445916630482944";
        
        JDABuilder builder = JDABuilder.createDefault("Nzc3OTU0NTg0MDEzOTYzMjY1.X7K8qg.f7kbG0-yhaYy6WBfJiPrEf1DaO4");
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setActivity(Activity.watching("atnw.eu/discord"));
        builder.addEventListeners(new ListenerAdapter() {

            @Override
            public void onGuildMemberJoin(GuildMemberJoinEvent event) {
                Role role = event.getGuild().getRolesByName("💀 | Unwichtig", true).stream().findFirst().orElse(null);
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
                User user = backendManager.getUser(event.getMember().getId());
                backendManager.setUsername(user.getId(), event.getMember().getUser().getName());
                TextChannel welcomeChannel = (TextChannel) jda.getGuildById(guildId).getChannels().stream().filter(t -> t.getId().equals(welcomeChannelId)).findFirst().orElse(null);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setDescription("Welcome " + event.getMember().getAsMention() + ", we are pleased to welcome you on our server!");
                welcomeChannel.sendMessage(embed.build()).queue();
            }

            @Override
            public void onGuildReady(GuildReadyEvent event) {
                System.out.println("READY!");
            }

            @Override
            public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                if(event.getMember() == null) {
                    return;
                }
                if(event.getMember().getUser().getId().equals(jda.getSelfUser().getId())) {
                    return;
                }
                Message message = event.getMessage();
                
                User user = backendManager.getUser(String.valueOf(event.getMember().getIdLong()));
                int randomXp = random.nextInt((5-1)+1) + 1;
                backendManager.addXp(user.getId(), randomXp);
                backendManager.setUsername(user.getId(), event.getMember().getUser().getName());
                if(backendManager.getRemainingXp(user.getId()) <= randomXp) {
                    backendManager.addLevel(user.getId(), 1);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(149, 79, 180));
                    embed.setAuthor(event.getMember().getUser().getName(), null, event.getMember().getUser().getAvatarUrl());
                    embed.setDescription("**Congratulations**, you have now reached level **" + backendManager.getLevel(user.getId()) + "**! <a:blobgifrolling:771743022282440815>");
                    event.getChannel().sendMessage(embed.build()).queue();
                }
                
                if(event.getChannel().getName().startsWith("ticket-")) {
                    Ticket ticket = backendManager.getTicket(event.getChannel().getName());
                    backendManager.addTicketMessage(ticket.getId(), message);
                }
                
                if (!message.getContentRaw().toLowerCase().startsWith("!")) {
                    return;
                }
                consoleInfo(MessageFormat.format("{0} ({1}) ran command {2} in {3} (#{4})", event.getAuthor().getName(), event.getAuthor().getId(), message.getContentRaw().toLowerCase().split(" ")[0], event.getGuild().getName(), event.getChannel().getName()));

                if (message.getContentRaw().toLowerCase().startsWith("!help")) {
                    helpCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!info")) {
                    infoCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!invite")) {
                    infoCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!level")) {
                    levelCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!magicmussel")) {
                    magicMusselCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!ms")) {
                    magicMusselCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!news")) {
                    newsCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!ranking")) {
                    rankingCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!cookie")) {
                    cookieCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!clear")) {
                    clearCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!whois")) {
                    whoisCommand.execute(event);
                } else if (message.getContentRaw().toLowerCase().startsWith("!ticket")) {
                    ticketCommand.execute(event);
                }
            }

            @Override
            public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
                if(event.getMember().getUser().getId().equals(jda.getSelfUser().getId())) {
                    return;
                }
                if(event.getChannel().getId().equals(roleChannelId)) {
                    if(event.getReactionEmote().getId().equals("734613241581404271")) { // ATOMICRADIO ROLE
                        Role role = event.getGuild().getRolesByName("🔥 | Freund", true).stream().findFirst().orElse(null);
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                    } else if(event.getReactionEmote().getId().equals("734611793187700736")) { // ATOMICGAMING ROLE
                        Role role = event.getGuild().getRolesByName("🔨 | Test", true).stream().findFirst().orElse(null);
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                    }
                } else if(event.getChannel().getId().equals(ticketChannelId)) {
                    if(event.getReactionEmote().getId().equals("734611793187700736")) { // GAMING TICKET
                        ticketManager.createChannel(event, TicketType.GAMING);
                    } else if(event.getReactionEmote().getId().equals("734613241581404271")) { // RADIO TICKET
                        ticketManager.createChannel(event, TicketType.RADIO);
                    } else if(event.getReactionEmote().getId().equals("736627104992591883")) { // GENERAL TICKET
                        ticketManager.createChannel(event, TicketType.GENERAL);
                    }
                } else if(event.getChannel().getName().startsWith("ticket-")) {
                     if(event.getReactionEmote().getEmoji().equals("📪")) {
                         event.getChannel().delete().queue();
                         ticketManager.sendTicketInfoEmbed(backendManager.getTicket(event.getChannel().getName()));
                     }
                }
            }
            
        });
        
        try {
            this.jda = builder.build();
            Timer sendTimer = new Timer(1, (ActionEvent e) -> {
                TextChannel rolesChannel = (TextChannel) jda.getGuildById(guildId).getChannels().stream().filter(t -> t.getId().equals(roleChannelId)).findFirst().orElse(null);
                if(!(new MessageHistory(rolesChannel).retrievePast(1).complete()).isEmpty()) {
                    (new MessageHistory(rolesChannel).retrievePast(1).complete()).get(0).delete().queue();
                }
                rolesChannel.sendMessage("**Welcome to our Discord,**\nplease read #rules and choose one of the following groups:\n\n<:playatomic:734613241581404271> **Radio**\n"
                        + "This group gives you access to the radio channels and allows you to keep up to date about the latest features, contribute to the development and exchange with the community about our radio.\n\n"
                        + "<:gamingatomic:734611793187700736> **Gaming**\n\n"
                        + "With this group you get access to the channels of our gamingprojects and can decide together with the community how we develop our offer and find new persons to play with.\n\n"
                        + "» We wish you a nice stay on our Discord.").queue((message) -> {
                            long messageId = message.getIdLong();
                            rolesChannel.addReactionById(messageId, ":playatomic:734613241581404271").queue();
                            rolesChannel.addReactionById(messageId, ":gamingatomic:734611793187700736").queue();
                        });

                TextChannel supportChannel = (TextChannel) jda.getGuildById(guildId).getChannels().stream().filter(t -> t.getId().equals(ticketChannelId)).findFirst().orElse(null);
                if(!(new MessageHistory(supportChannel).retrievePast(1).complete()).isEmpty()) {
                    (new MessageHistory(supportChannel).retrievePast(1).complete()).get(0).delete().queue();
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor("Supportsystem", null, "https://images.discordapp.net/avatars/697517106287345737/07be164c270546a8c976063bc71939fc.png?size=512");
                embed.setDescription("Respond to one of the emojis listed below and create a support ticket with team members from that department.\n\n"
                        + "<:atomic:736627104992591883> atomicnetworks.eu\n"
                        + "<:playatomic:734613241581404271> atomicradio.eu\n"
                        + "<:gamingatomic:734611793187700736> atomicgaming.eu");
                supportChannel.sendMessage(embed.build()).queue((message) -> {
                    long messageId = message.getIdLong();
                    supportChannel.addReactionById(messageId, ":atomic:736627104992591883").queue();
                    supportChannel.addReactionById(messageId, ":playatomic:734613241581404271").queue();
                    supportChannel.addReactionById(messageId, ":gamingatomic:734611793187700736").queue();
                });
            });
            sendTimer.setInitialDelay(10000);
            sendTimer.setRepeats(false);
            sendTimer.start();
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
                + " atomicradio.eu discord\n 2020 Copyright (c) by atomicnetworks.eu to present.\n Author: Kacper Mura\n");
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
    
}
