package me.andrew.discordsupport;

import me.andrew.discordsupport.DiscordSupportBot;
import me.andrew.discordsupport.objects.QuestionRunnable;
import me.andrew.discordsupport.objects.YesNoRunnable;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;

public class DiscordUtil {

    public static Object pullEvent(Class<? extends Event> eventClass) {
        final Object[] pulledEvent = {null};

        ListenerAdapter adapter = new ListenerAdapter() {
            @Override
            public void onGenericEvent(Event event) {
                if (event.getClass().getSimpleName().equals(eventClass.getSimpleName())) {
                    pulledEvent[0] = event;
                }
            }
        };
        DiscordSupportBot.get().getJda().addEventListener(adapter);

        while (pulledEvent[0] == null) try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DiscordSupportBot.get().getJda().removeEventListener(adapter);
        return pulledEvent[0];
    }

    public static GuildMessageReceivedEvent pullGuildMessageReceivedEvent(User user) {
        while (1 < 2) {
            GuildMessageReceivedEvent pulled = (GuildMessageReceivedEvent) pullEvent(GuildMessageReceivedEvent.class);
            if (pulled.getMember() == null || pulled.getMember().getUser().isBot()) continue;
            if (pulled.getMember().getUser().equals(user)) {
                return pulled;
            }
        }
    }


    public static GuildMessageReactionAddEvent pullGuildMessageReactionAddEvent(User user) {
        while (1 < 2) {
            GuildMessageReactionAddEvent pulled = (GuildMessageReactionAddEvent) pullEvent(GuildMessageReactionAddEvent.class);
            if (pulled.getMember() == null || pulled.getMember().getUser().isBot()) continue;
            if (pulled.getMember().getUser().equals(user)) {
                return pulled;
            }
        }
    }
    public static PrivateMessageReactionAddEvent pullPrivateMessageReactionAddEvent(User user) {
        System.out.println("Pulled event!!!");
        while (true) {
            PrivateMessageReactionAddEvent pulled = (PrivateMessageReactionAddEvent) pullEvent(PrivateMessageReactionAddEvent.class);

            if (pulled.getUser() == null || pulled.getUser().isBot()) continue;
            if (pulled.getUser().equals(user)) {
                return pulled;
            }
        }
    }
    public static GuildMessageReactionAddEvent pullGuildMessageReactionAddEvent(Message message, User user) {
        message.addReaction("✅").queue();
        while (1 < 2) {
            GuildMessageReactionAddEvent pulled = (GuildMessageReactionAddEvent) pullEvent(GuildMessageReactionAddEvent.class);
            if (pulled.getMember() == null || pulled.getMember().getUser().isBot()) continue;
            if (pulled.getMember().getUser().equals(user) && pulled.getMessageId().equals(message.getId())) {
                return pulled;
            }
        }
    }


    public static void pullYesOrNo(Message messageToListenTo, User author, YesNoRunnable runnable) {
        messageToListenTo.addReaction("✅").queue();
        messageToListenTo.addReaction("❌").queue();

        while (1 < 2) {
            GuildMessageReactionAddEvent pulled = DiscordUtil.pullGuildMessageReactionAddEvent(author);
            if (!pulled.getMessageId().equals(messageToListenTo.getId())) continue;
            String emoji = pulled.getReactionEmote().getName();
            if (!emoji.equals("✅") && !emoji.equals("❌")) continue;
            boolean b = emoji.equals("✅");
            runnable.run(b);
        }
    }

    public static void pullString(TextChannel channel, User user, QuestionRunnable runnable) {
        while (1 < 2) {
            GuildMessageReceivedEvent pulled = DiscordUtil.pullGuildMessageReceivedEvent(user);
            runnable.run(pulled.getMessage().getContentStripped());
        }
    }

    public static boolean pullYesOrNo(Message messageToListenTo, User author) {
        messageToListenTo.addReaction("✅").queue();
        messageToListenTo.addReaction("❌").queue();

        while (1 < 2) {
            GuildMessageReactionAddEvent pulled = DiscordUtil.pullGuildMessageReactionAddEvent(author);
            if (!pulled.getMessageId().equals(messageToListenTo.getId())) continue;
            String emoji = pulled.getReactionEmote().getName();
            if (!emoji.equals("✅") && !emoji.equals("❌")) continue;
            return emoji.equals("✅");
        }
    }
    public static boolean pullYesOrNoPrivate(Message messageToListenTo, User author) {
        messageToListenTo.addReaction("✅").queue();
        messageToListenTo.addReaction("❌").queue();

        while (1 < 2) {
            PrivateMessageReactionAddEvent pulled = DiscordUtil.pullPrivateMessageReactionAddEvent(author);
            if (!pulled.getMessageId().equals(messageToListenTo.getId())) continue;
            String emoji = pulled.getReactionEmote().getName();
            if (!emoji.equals("✅") && !emoji.equals("❌")) continue;
            return emoji.equals("✅");
        }
    }

    public static int pullInteger(TextChannel channel, User user) {
        while (1 < 2) {
            GuildMessageReceivedEvent pulled = DiscordUtil.pullGuildMessageReceivedEvent(user);
            if (StringUtils.isNumeric(pulled.getMessage().getContentRaw())) {
                return Integer.parseInt(pulled.getMessage().getContentRaw());
            } else {
                channel.sendMessage("Your message must be an integer.").queue();
            }
        }
    }

    public static Role getRoleByNameFromGuild(Guild guild, String roleName) {
        for (Role role : guild.getRoles()) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }


}
