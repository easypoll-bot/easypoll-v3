/*
 * EasyPoll Discord Bot (https://github.com/fbrettnich/easypoll-bot)
 * Copyright (C) 2021  Felix Brettnich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.fbrettnich.easypoll.listener;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.utils.Statistics;
import de.fbrettnich.easypoll.utils.enums.StatisticsEvents;
import io.sentry.Sentry;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
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

            if (messageColor.equals(Constants.COLOR_POLL_UPDOWN) && messageReactions.size() >= 2 && messageReactions.get(0).getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D") && messageReactions.get(1).getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4E")) {

                removeAddedReaction = false;

                if (messageReactions.size() > 2) {
                    try {
                        messageReactions.get(2).removeReaction(user).queue(null, Sentry::captureException);
                    }catch (InsufficientPermissionException ignored) { }
                }

                switch (event.getReactionEmote().getName()) {
                    case "\uD83D\uDC4D": { // 👍 :thumbsup:

                        messageReactions.get(1).retrieveUsers().queue(users -> {
                            if (users.contains(user)) {
                                try {
                                    messageReactions.get(1).removeReaction(user).queue(null, Sentry::captureException);
                                }catch (InsufficientPermissionException ignored) { }
                            }
                        }, Sentry::captureException);

                        break;
                    }

                    case "\uD83D\uDC4E": { // 👎 :thumbsdown:

                        messageReactions.get(0).retrieveUsers().queue(users -> {
                            if (users.contains(user)) {
                                try {
                                    messageReactions.get(0).removeReaction(user).queue(null, Sentry::captureException);
                                }catch (InsufficientPermissionException ignored) { }
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
                                try {
                                    messageReaction.removeReaction(user).queue(null, Sentry::captureException);
                                }catch (InsufficientPermissionException ignored) { }
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
                                        try {
                                            messageReaction.removeReaction(user).queue(null, Sentry::captureException);
                                        }catch (InsufficientPermissionException ignored) { }
                                    }
                                }, Sentry::captureException);
                            }
                        }
                    }
                }

            }
        }

        if (removeAddedReaction) {
            try {
                event.getReaction().removeReaction(user).queue(null, Sentry::captureException);
            }catch (InsufficientPermissionException ignored) { }
        }

        Statistics.insertEventCall(StatisticsEvents.REACTIONADD);
    }
}
