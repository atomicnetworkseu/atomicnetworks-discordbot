package eu.atomicnetworks.discordbot.handlers;

import eu.atomicnetworks.discordbot.DiscordBot;
import eu.atomicnetworks.discordbot.commands.ClearCommand;
import eu.atomicnetworks.discordbot.commands.CookieCommand;
import eu.atomicnetworks.discordbot.commands.HelpCommand;
import eu.atomicnetworks.discordbot.commands.InfoCommand;
import eu.atomicnetworks.discordbot.commands.LevelCommand;
import eu.atomicnetworks.discordbot.commands.LevelWarnCommand;
import eu.atomicnetworks.discordbot.commands.MagicMusselCommand;
import eu.atomicnetworks.discordbot.commands.NewsCommand;
import eu.atomicnetworks.discordbot.commands.RankingCommand;
import eu.atomicnetworks.discordbot.commands.TicketCommand;
import eu.atomicnetworks.discordbot.commands.VerifyCommand;
import eu.atomicnetworks.discordbot.commands.VoteCommand;
import eu.atomicnetworks.discordbot.commands.WarnCommand;
import eu.atomicnetworks.discordbot.commands.WhoisCommand;
import eu.atomicnetworks.discordbot.enums.TicketType;
import eu.atomicnetworks.discordbot.objects.Ticket;
import eu.atomicnetworks.discordbot.objects.User;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
    private final NewsCommand newsCommand;
    private final RankingCommand rankingCommand;
    private final CookieCommand cookieCommand;
    private final ClearCommand clearCommand;
    private final WhoisCommand whoisCommand;
    private final TicketCommand ticketCommand;
    private final WarnCommand warnCommand;
    private final VoteCommand voteCommand;
    private final VerifyCommand verifyCommand;
    private final LevelWarnCommand levelWarnCommand;
    private final Random random;

    public EventHandler(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.helpCommand = new HelpCommand(this.discordBot);
        this.infoCommand = new InfoCommand(this.discordBot);
        this.levelCommand = new LevelCommand(this.discordBot);
        this.magicMusselCommand = new MagicMusselCommand(this.discordBot);
        this.newsCommand = new NewsCommand(this.discordBot);
        this.rankingCommand = new RankingCommand(this.discordBot);
        this.cookieCommand = new CookieCommand(this.discordBot);
        this.clearCommand = new ClearCommand(this.discordBot);
        this.whoisCommand = new WhoisCommand(this.discordBot);
        this.ticketCommand = new TicketCommand(this.discordBot);
        this.warnCommand = new WarnCommand(this.discordBot);
        this.voteCommand = new VoteCommand(this.discordBot);
        this.verifyCommand = new VerifyCommand(this.discordBot);
        this.levelWarnCommand = new LevelWarnCommand(this.discordBot);
        this.random = new Random();
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
        embed.setDescription("Welcome " + event.getMember().getAsMention() + ", we are happy to welcome you on our server and wish you a lot of fun with our community! 💝");
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
        this.discordBot.getBackendManager().addXp(user.getId(), randomXp);
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
        } else if (message.getContentRaw().toLowerCase().startsWith("!news")) {
            newsCommand.execute(event);
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
        } else if (message.getContentRaw().toLowerCase().startsWith("!warn")) {
            event.getMessage().delete().queue();
            warnCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!vote")) {
            voteCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!teamspeak")) {
            verifyCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith("!levelwarn")) {
            levelWarnCommand.execute(event);
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
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
            } else if (event.getReactionEmote().getId().equals("734611793187700736")) { // ATOMICGAMING ROLE
                Role role = event.getGuild().getRolesByName("#gaming", true).stream().findFirst().orElse(null);
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
            }
        } else if (event.getChannel().getId().equals(this.discordBot.getTicketChannelId())) {
            if (event.getReactionEmote().getId().equals("734611793187700736")) { // GAMING TICKET
                this.discordBot.getTicketManager().createChannel(event, TicketType.GAMING);
            } else if (event.getReactionEmote().getId().equals("734613241581404271")) { // RADIO TICKET
                this.discordBot.getTicketManager().createChannel(event, TicketType.RADIO);
            } else if (event.getReactionEmote().getId().equals("736627104992591883")) { // GENERAL TICKET
                this.discordBot.getTicketManager().createChannel(event, TicketType.GENERAL);
            }
        } else if (event.getChannel().getName().startsWith("ticket-")) {
            if (event.getReactionEmote().getEmoji().equals("📪")) {
                event.getChannel().delete().queue();
                this.discordBot.getTicketManager().sendTicketInfoEmbed(this.discordBot.getBackendManager().getTicket(event.getChannel().getName()));
            }
        }
        if (event.getChannel().getId().equals(this.discordBot.getCommandChannelId())) {
            if (event.getReactionEmote().getId().equals("802089372076867594")) {
                rankingCommand.switchPageForward(event.getChannel(), event.getMessageIdLong());
            } else if (event.getReactionEmote().getId().equals("802089371964407838")) {
                rankingCommand.switchPageBack(event.getChannel(), event.getMessageIdLong());
            }
        }
    }

}
