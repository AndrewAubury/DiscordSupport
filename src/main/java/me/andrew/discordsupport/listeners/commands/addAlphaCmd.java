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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if(!e.getMessage().getContentStripped().startsWith("?addalpha")){
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

        Thread thread = new Thread(() -> {
            Map<String, String> info = new HashMap<String, String>();
            info.put("name",target.getEffectiveName());
            c.sendMessage("Server IP?").complete();

            DiscordUtil.pullString(c,e.getAuthor(),sip->{
                info.put("server-ip", sip);
                c.sendMessage("What will "+target.getAsMention()+ " be hosting MinePoS On?").complete();
                DiscordUtil.pullString(c, e.getAuthor(), host -> {
                    info.put("host-type", host);
                    c.sendMessage("What will be the link for MinePoS").complete();
                    DiscordUtil.pullString(c, e.getAuthor(), link -> {
                        info.put("link", link);

                        DiscordUtil.executeDB(b -> {
                            if(b){
                                e.getChannel().sendMessage(new EmbedBuilder().setAuthor(target.getUser().getName(),null,target.getUser().getAvatarUrl())
                                        .setColor(Color.GREEN)
                                        .setDescription("Welcome to MinePoS Alpha!```"
                                                +"Name: "+info.get("name")+System.lineSeparator()
                                                +"Server IP: "+info.get("server-ip")+System.lineSeparator()
                                                +"Hosting Method: "+info.get("host-type")+System.lineSeparator()
                                                +"MinePoS Link: "+info.get("link")
                                                +"```")
                                        .build()).complete();
                                e.getGuild().getController().addRolesToMember(target,e.getGuild().getRolesByName("alpha",true)).complete();
                            }else{
                                e.getChannel().sendMessage(new EmbedBuilder().setAuthor(e.getAuthor().getName(),null,e.getAuthor().getAvatarUrl())
                                        .setColor(Color.RED)
                                        .setDescription("Error!")
                                        .build()).complete();
                            }
                            return;
                        },"INSERT INTO `minepos_alpha_user_info` (`id`, `name`, `server_ip`, `server_type`, `minepos_link`) VALUES (NULL, '"+info.get("name")+"', '"+info.get("server-ip")+"', '"+info.get("host-type")+"', '"+info.get("link")+"');");
                        return;
                    });
                    return;
                });
                return;
            });
            return;
        });
        thread.setName("Add to alpha - " + target.getUser().getName());
        thread.start();
    }
}
