package me.andrew.discordsupport.listeners.commands;

import me.andrew.discordsupport.DiscordSupportBot;
import me.andrew.discordsupport.DiscordUtil;
import me.andrew.discordsupport.objects.QuestionRunnable;
import me.andrew.discordsupport.objects.YesNoRunnable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ------------------------------
 * Copyright (c) AndrewAubury 2018
 * https://www.andrewa.pw
 * Project: RageBotSupport
 * ------------------------------
 */
public class addAlphaCmd extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMember() == null) return;
        //if (event.getMember().isOwner()) return;
        if (e.getAuthor().isBot()) return;
        if (!DiscordSupportBot.isSetUp(e.getGuild())) return;

        if(!e.getMessage().getContentStripped().startsWith("?addbeta")){
            return;
        }
        if(!e.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)){
            return;
        }

        if(e.getMessage().getMentionedMembers().size() != 1){
            e.getChannel().sendMessage(new EmbedBuilder().setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl())
                    .setColor(Color.RED)
                    .setDescription("Usage: ?addalpha {USER}")
                    .build()).complete();
        }
        Member target = e.getMessage().getMentionedMembers().get(0);
        TextChannel c = e.getChannel();

        List<Message> messages = new ArrayList<Message>();
        messages.add(e.getMessage());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message tmpMSG = c.sendMessage("Are you sure you want to add "+target.getAsMention()+ " to MinePoS Alpha Testing?").complete();
                DiscordUtil.pullYesOrNo(tmpMSG, e.getAuthor(), new YesNoRunnable() {
                    @Override
                    public void run(Boolean b) {
                        if(b){
                            e.getChannel().sendMessage(new EmbedBuilder().setAuthor(target.getUser().getName(),null,target.getUser().getAvatarUrl())
                                    .setColor(Color.GREEN)
                                    .setDescription("Welcome to MinePoS Alpha!")
                                    .build()).complete();
                            e.getGuild().getController().addRolesToMember(target,e.getGuild().getRolesByName("alpha",true)).complete();
                        }else{
                            e.getChannel().sendMessage(new EmbedBuilder().setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl())
                                    .setColor(Color.RED)
                                    .setDescription("Canceled!")
                                    .build()).complete();
                        }
                    }
                });

            }
        });
        thread.run();


    }
}
