package me.andrew.discordsupport.listeners;

import me.andrew.discordsupport.DiscordSupportBot;
import me.andrew.discordsupport.GuildInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ------------------------------
 * Copyright (c) AndrewAubury 2018
 * https://www.andrewa.pw
 * Project: RageBotSupport
 * ------------------------------
 */
public class ExtraCommands extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMember() == null) return;
        //if (event.getMember().isOwner()) return;
        if (e.getAuthor().isBot()) return;
        if (!DiscordSupportBot.isSetUp(e.getGuild())) return;

        GuildInfo guildInfo = DiscordSupportBot.getGuildInfo(e.getGuild());
        TextChannel supportChannel = guildInfo.getFirstMessageChannel();

        if(!e.getMessage().getContentStripped().startsWith("-")){
            return;
        }

        if(e.getMessage().getContentStripped().startsWith("-new")) {
                if(e.getMessage().getMentionedMembers().size() >0){
                    handleNewTicket(e.getGuild(),e.getMessage().getMentionedMembers().get(0),e.getMessage());
                    e.getMessage().delete().queue();
                    return;
                }else{
                    EmbedBuilder error = new EmbedBuilder();
                    error.setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl());
                    error.setColor(Color.RED);
                    error.setTitle(e.getAuthor().getName());
                    error.setDescription("You need to tag a member");
                    e.getChannel().sendMessage(error.build()).queue();
                    e.getMessage().delete().queue();

                    return;
                }
        }


        if(e.getMessage().getContentStripped().startsWith("-add")){
            if(e.getChannel().getName().toLowerCase().startsWith((supportChannel.getName() + "-").toLowerCase())){
                if(e.getMessage().getMentionedMembers().size() >0){
                    e.getChannel().createPermissionOverride(e.getMessage().getMentionedMembers().get(0)).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                    EmbedBuilder error = new EmbedBuilder();
                    error.setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl());
                    error.setColor(Color.GREEN);
                    error.setTitle("Added!");
                    error.setDescription(e.getMessage().getMentionedMembers().get(0).getAsMention()+" has been added to the ticket");
                    e.getChannel().sendMessage(error.build()).queue();
                    e.getMessage().delete().queue();

                    return;
                }else{
                    EmbedBuilder error = new EmbedBuilder();
                    error.setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl());
                    error.setColor(Color.RED);
                    error.setTitle(e.getAuthor().getName());
                    error.setDescription("You need to tag a member");
                    e.getChannel().sendMessage(error.build()).queue();
                    e.getMessage().delete().queue();

                    return;
                }
            }else{
                EmbedBuilder error = new EmbedBuilder();
                error.setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl());
                error.setColor(Color.RED);
                error.setTitle(e.getAuthor().getName());
                error.setDescription("This command can only be used in a ticket");
                e.getChannel().sendMessage(error.build()).queue();
                e.getMessage().delete().queue();

                return;
            }
        }


    }

    public boolean hasTicket(Guild g, User u){
        GuildInfo guildInfo = DiscordSupportBot.getGuildInfo(g);
        TextChannel supportChannel = guildInfo.getFirstMessageChannel();
        List<TextChannel> possiblePreExistingTicketChannels = g.getTextChannelsByName(supportChannel.getName() + "-" + u.getId(), true);
        return (possiblePreExistingTicketChannels.size() > 0);
    }

    private void handleNewTicket(Guild g,Member author, Message startMessage) {
        final String MESSAGE_TEMPLATE = "**__Author:__** {AUTHOR}\n" +
                "**__Message:__** {MESSAGE}\n" +
                "\n" +
                "*To close this ticket, the ticket author needs to react to this message. Doing so will mark the ticket as solved.*";

        GuildInfo guildInfo = DiscordSupportBot.getGuildInfo(g);
        TextChannel supportChannel = guildInfo.getFirstMessageChannel();

        // create the new channel, inside of a category if possible
        String channelName = supportChannel.getName() + "-" + author.getUser().getId();
        TextChannel newChannel;
        if (supportChannel.getParent() == null) {
            newChannel = (TextChannel) g.getController().createTextChannel(channelName).complete();
        } else {
            newChannel = (TextChannel) supportChannel.getParent().createTextChannel(channelName).complete();
        }
        g.getController().modifyTextChannelPositions().selectPosition(newChannel).moveTo(supportChannel.getPosition() + 1).queue();

        // make the author forcefully have message read/write permission
        newChannel.sendTyping().queue();
        //newChannel.getPermissionOverride(DiscordSupportBot.get().getJda().getGuildById(guildInfo.getGuildId()).getPublicRole());
        newChannel.createPermissionOverride(author).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        //newChannel.createPermissionOverride

        newChannel.sendMessage(MESSAGE_TEMPLATE
                .replace("{AUTHOR}", author.getAsMention())
                .replace("{MESSAGE}", startMessage.getContentRaw())
                .replace("{CLOSERS}", "`" + String.join(", ", guildInfo.getRolesAllowedToCloseTickets().stream().map(s -> g.getRoleById(s).getName()).collect(Collectors.toList())) + "`")
        ).queue(message -> message.addReaction(guildInfo.getDefaultReactionEmoji()).queue());

    }
}
