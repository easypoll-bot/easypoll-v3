/*
 * Copyright (c) 2021 Felix Brettnich
 *
 * This file is part of EasyPoll (https://github.com/fbrettnich/EasyPoll-Bot)
 *
 * All contents of this source code are protected by copyright.
 * The copyright lies, if not expressly differently marked,
 * by Felix Brettnich. All rights reserved.
 *
 * Any kind of duplication, distribution, rental, lending,
 * public accessibility or other use requires the explicit,
 * written consent from Felix Brettnich
 */

package de.fbrettnich.easypoll.listener;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.utils.Statistics;
import de.fbrettnich.easypoll.utils.enums.StatisticsEvents;
import io.sentry.Sentry;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageReactionAddListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        User user = event.getUser();
        Message message = null;
        Color messageColor = null;
        boolean removeAddedReaction = true;

        if(user == null) return;
        if(user.isBot()) return;

        try {
            message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        } catch (Exception ignored) {}

        if(message == null) return;
        if(message.getAuthor() != event.getJDA().getSelfUser()) return;
        if(!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) return;

        List<MessageReaction> messageReactions = message.getReactions();
        List<MessageEmbed> messageEmbedList = message.getEmbeds();

        if(!messageEmbedList.isEmpty()) {
            messageColor = messageEmbedList.get(0).getColor();
        }

        if(messageColor != null) {

            if (messageColor.equals(Constants.COLOR_POLL_UPDOWN) && messageReactions.get(0).getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D") && messageReactions.get(1).getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4E")) {

                removeAddedReaction = false;

                if (messageReactions.size() > 2) {
                    messageReactions.get(2).removeReaction(user).queue(null, Sentry::captureException);
                }

                switch (event.getReactionEmote().getName()) {
                    case "\uD83D\uDC4D": { // 👍 :thumbsup:

                        messageReactions.get(1).retrieveUsers().queue(users -> {
                            if (users.contains(user)) {
                                messageReactions.get(1).removeReaction(user).queue(null, Sentry::captureException);
                            }
                        }, Sentry::captureException);

                        break;
                    }

                    case "\uD83D\uDC4E": { // 👎 :thumbsdown:

                        messageReactions.get(0).retrieveUsers().queue(users -> {
                            if (users.contains(user)) {
                                messageReactions.get(0).removeReaction(user).queue(null, Sentry::captureException);
                            }
                        }, Sentry::captureException);

                        break;
                    }
                }

            }else if (messageColor.equals(Constants.COLOR_POLL_CUSTOM_SINGEL) || messageColor.equals(Constants.COLOR_POLL_CUSTOM_MULTI)) {

                AtomicBoolean isOtherReaction = new AtomicBoolean(false);
                removeAddedReaction = false;

                for (MessageReaction messageReaction : messageReactions) {
                    if (messageReaction.getReactionEmote().equals(event.getReactionEmote())) {
                        messageReaction.retrieveUsers().queue(users -> {
                            if (!users.contains(event.getJDA().getSelfUser())) {
                                messageReaction.removeReaction(user).queue(null, Sentry::captureException);
                                isOtherReaction.set(true);
                            }
                        }, Sentry::captureException);
                    }
                }

                if (!isOtherReaction.get()) {
                    if (messageColor.equals(Constants.COLOR_POLL_CUSTOM_SINGEL)) {
                        for (MessageReaction messageReaction : messageReactions) {
                            if (!messageReaction.getReactionEmote().equals(event.getReactionEmote())) {
                                messageReaction.retrieveUsers().queue(users -> {
                                    if (users.contains(user)) {
                                        messageReaction.removeReaction(user).queue(null, Sentry::captureException);
                                    }
                                }, Sentry::captureException);
                            }
                        }
                    }
                }

            }
        }

        if (removeAddedReaction) {
            event.getReaction().removeReaction(user).queue(null, Sentry::captureException);
        }

        Statistics.insertEventCall(StatisticsEvents.REACTIONADD);
    }
}
