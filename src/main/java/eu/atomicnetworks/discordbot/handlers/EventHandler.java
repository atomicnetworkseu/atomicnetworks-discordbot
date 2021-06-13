package eu.atomicnetworks.discordbot.handlers;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.commands.ClearCommand;
import eu.atomicnetworks.discordbot.commands.CookieCommand;
import eu.atomicnetworks.discordbot.commands.HelpCommand;
import eu.atomicnetworks.discordbot.commands.InfoCommand;
import eu.atomicnetworks.discordbot.commands.LevelCommand;
import eu.atomicnetworks.discordbot.commands.LevelWarnCommand;
import eu.atomicnetworks.discordbot.commands.MagicMusselCommand;
import eu.atomicnetworks.discordbot.commands.PingCommand;
import eu.atomicnetworks.discordbot.commands.RankingCommand;
import eu.atomicnetworks.discordbot.commands.TicketCommand;
import eu.atomicnetworks.discordbot.commands.VoteCommand;
import eu.atomicnetworks.discordbot.commands.WarnCommand;
import eu.atomicnetworks.discordbot.commands.WarnResetCommand;
import eu.atomicnetworks.discordbot.commands.WhoisCommand;
import eu.atomicnetworks.discordbot.objects.Ticket;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class EventHandler extends ListenerAdapter {

    private final DiscordBot discordBot;
    private final HelpCommand helpCommand;
    private final InfoCommand infoCommand;
    private final LevelCommand levelCommand;
    private final MagicMusselCommand magicMusselCommand;
    private final RankingCommand rankingCommand;
    private final CookieCommand cookieCommand;
    private final ClearCommand clearCommand;
    private final WhoisCommand whoisCommand;
    private final TicketCommand ticketCommand;
    private final WarnCommand warnCommand;
    private final VoteCommand voteCommand;
    private final LevelWarnCommand levelWarnCommand;
    private final WarnResetCommand warnResetCommand;
    private final PingCommand pingCommand;
    
    private final Random random;
    private ArrayList<String> welcomeMessage;
    
    public EventHandler(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.helpCommand = new HelpCommand(this.discordBot);
        this.infoCommand = new InfoCommand(this.discordBot);
        this.levelCommand = new LevelCommand(this.discordBot);
        this.magicMusselCommand = new MagicMusselCommand(this.discordBot);
        this.rankingCommand = new RankingCommand(this.discordBot);
        this.cookieCommand = new CookieCommand(this.discordBot);
        this.clearCommand = new ClearCommand(this.discordBot);
        this.whoisCommand = new WhoisCommand(this.discordBot);
        this.ticketCommand = new TicketCommand(this.discordBot);
        this.warnCommand = new WarnCommand(this.discordBot);
        this.voteCommand = new VoteCommand(this.discordBot);
        this.levelWarnCommand = new LevelWarnCommand(this.discordBot);
        this.warnResetCommand = new WarnResetCommand(this.discordBot);
        this.pingCommand = new PingCommand(this.discordBot);
        
        this.random = new Random();
        this.welcomeMessage = new ArrayList<>();
        this.welcomeMessage.add("Welcome {0}, we are happy to welcome you on our server and wish you a lot of fun with our community! 💝");
        this.welcomeMessage.add("Welcome {0}, we have already been waiting for you and are looking forward to getting to know you better! ✨");
        this.welcomeMessage.add("Finally {0} made it to our server, we were already waiting for you and hope you will have a nice time with us! 🎩");
        this.welcomeMessage.add("We hope you are ready to go {0}, feel free to introduce yourself in the <#734477712139223137> and start a nice conversation with our community! 🚀");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role role = event.getGuild().getRoleById("734477710319026220");
        event.getGuild().addRoleToMember(event.getMember(), role).queue();
        User user = this.discordBot.getBackendManager().getUser(event.getMember().getId());
        this.discordBot.getBackendManager().setUsername(user.getId(), event.getMember().getUser().getName());
        TextChannel welcomeChannel = (TextChannel) this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).getChannels().stream().filter(t -> t.getId().equals(this.discordBot.getWelcomeChannelId())).findFirst().orElse(null);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(149, 79, 180));
        
        int rnd = this.random.nextInt(welcomeMessage.size());
        embed.setDescription(MessageFormat.format(this.welcomeMessage.get(rnd), "**" + event.getMember().getUser().getName() + "**#" + event.getMember().getUser().getDiscriminator()));
        welcomeChannel.sendMessage(embed.build()).queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember() == null) {
            return;
        }
        if (event.getMember().getUser().getId().equals(this.discordBot.getJda().getSelfUser().getId())) {
            return;
        }
        if (event.getMember().getUser().getId().equals("697517106287345737")) {
            return;
        }
        Message message = event.getMessage();
        
        User user = this.discordBot.getBackendManager().getUser(String.valueOf(event.getMember().getIdLong()));
        int randomXp = this.random.nextInt((5 - 1) + 1) + 1;
        this.discordBot.getBackendManager().addXp(user.getId(), (randomXp*this.discordBot.getBackendManager().getXPBoost(event.getMember())));
        this.discordBot.getBackendManager().setUsername(user.getId(), event.getMember().getUser().getName());
        if (this.discordBot.getBackendManager().getRemainingXp(user.getId()) <= randomXp) {
            this.discordBot.getBackendManager().addLevel(user.getId(), 1);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(149, 79, 180));
            embed.setAuthor(event.getMember().getUser().getName(), null, event.getMember().getUser().getAvatarUrl());
            embed.setDescription("**Congratulations**, you have now reached level **" + this.discordBot.getBackendManager().getLevel(user.getId()) + "**! <a:blobgifrolling:771743022282440815>");
            TextChannel textChannel = (TextChannel) this.discordBot.getJda().getGuildById(this.discordBot.getGuildId()).getChannels().stream().filter(t -> t.getId().equals(this.discordBot.getAchievementChannelId())).findFirst().orElse(null);
            textChannel.sendMessage(embed.build()).queue();
        }

        if (event.getChannel().getName().startsWith("ticket-")) {
            Ticket ticket = this.discordBot.getBackendManager().getTicket(event.getChannel().getName());
            this.discordBot.getBackendManager().addTicketMessage(ticket.getId(), message);
        }

        if (!message.getContentRaw().toLowerCase().startsWith("!")) {
            if(event.getChannel().getName().contains("announcements")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(149, 79, 180));
                embed.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
                embed.setDescription(message.getContentRaw());
                message.delete().queue();
                event.getChannel().sendMessage(embed.build()).queue();
                return;
            }
            return;
        }
        this.discordBot.consoleInfo(MessageFormat.format("{0} ({1}) ran command {2} in {3} (#{4})", event.getAuthor().getName(), event.getAuthor().getId(), message.getContentRaw().toLowerCase().split(" ")[0], event.getGuild().getName(), event.getChannel().getName()));

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
        } else if (message.getContentRaw().toLowerCase().startsWith("!ranking")) {
            rankingCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!cookie")) {
            cookieCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!clear")) {
            clearCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!whois")) {
            event.getMessage().delete().queue();
            whoisCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!ticket")) {
            ticketCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!warnreset")) {
            warnResetCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!warn")) {
            event.getMessage().delete().queue();
            warnCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!vote")) {
            voteCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!levelwarn")) {
            levelWarnCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!ping")) {
            pingCommand.execute(event);
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getMember().getUser().getId().equals(this.discordBot.getJda().getSelfUser().getId())) {
            return;
        }
        if (event.getChannel().getId().equals(this.discordBot.getRoleChannelId())) {
            if (event.getReactionEmote().getId().equals("734613241581404271")) { // ATOMICRADIO ROLE
                Role role = event.getGuild().getRolesByName("#radio", true).stream().findFirst().orElse(null);
                
                if(event.getMember().getRoles().stream().filter(t -> t.getId().equals(role.getId())).findFirst().orElse(null) != null) {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                } else {
                    event.getGuild().addRoleToMember(event.getMember(), role).queue();
                }
            } else if (event.getReactionEmote().getId().equals("734611793187700736")) { // ATOMICGAMING ROLE
                Role role = event.getGuild().getRolesByName("#gaming", true).stream().findFirst().orElse(null);
                if(event.getMember().getRoles().stream().filter(t -> t.getId().equals(role.getId())).findFirst().orElse(null) != null) {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                } else {
                    event.getGuild().addRoleToMember(event.getMember(), role).queue();
                }
            }
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                message.removeReaction(event.getReactionEmote().getEmote(), event.getMember().getUser()).queue();
            });
        } else if (event.getChannel().getId().equals(this.discordBot.getTicketChannelId())) {
            this.discordBot.getTicketManager().createChannel(event);
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                message.removeReaction("📬", event.getMember().getUser()).queue();
            });
        } else if (event.getChannel().getName().startsWith("ticket-")) {
            if (event.getReactionEmote().getEmoji().equals("📪")) {
                event.getChannel().delete().queue();
                this.discordBot.getTicketManager().sendTicketInfoEmbed(this.discordBot.getBackendManager().getTicket(event.getChannel().getName()));
            }
        } else if (event.getChannel().getId().equals(this.discordBot.getTeamchatChannelId())) {
            if(event.getReactionEmote().getEmoji().equals("✅")) {
                event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                    if(message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                        message.clearReactions().queue();
                        
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(0, 142, 120));
                        embed.setDescription("*The affected system has been successfully serviced, the associated reason for the fault has been successfully resolved by " + event.getMember().getAsMention() + "*");
                        message.editMessage(embed.build()).queue();
                    }
                });
            }
        } else if (event.getChannel().getId().equals(this.discordBot.getCommandChannelId())) {
            if (event.getReactionEmote().getId().equals("802089372076867594")) {
                rankingCommand.switchPageForward(event.getChannel(), event.getMessageIdLong());
            } else if (event.getReactionEmote().getId().equals("802089371964407838")) {
                rankingCommand.switchPageBack(event.getChannel(), event.getMessageIdLong());
            }
        }
    }

}
