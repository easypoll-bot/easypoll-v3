/*
 * Copyright (c) 2021 Felix Brettnich
 *
 * This file is part of EasyPoll (https://github.com/fbrettnich/easypoll)
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

import de.fbrettnich.easypoll.utils.Statistics;
import de.fbrettnich.easypoll.utils.enums.StatisticsEvents;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        /* Information for users who use old EasyPoll commands */
        String messageContent = event.getMessage().getContentDisplay();
        if(messageContent.toLowerCase().startsWith("ep!poll") ||
                messageContent.toLowerCase().startsWith("ep!pool") ||
                messageContent.toLowerCase().startsWith("ep!help") ||
                messageContent.toLowerCase().startsWith("ep!ping") ||
                messageContent.toLowerCase().startsWith("ep!stats") ||
                messageContent.toLowerCase().startsWith("ep!uptime") ||
                messageContent.toLowerCase().startsWith("ep!vote") ||
                messageContent.toLowerCase().startsWith("ep!invite")) {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);

            eb.setDescription(
                    "Hello " + event.getAuthor().getAsMention() + " :wave:, here is [EasyPoll](https://easypoll.me/) always at your service!\n" +
                            "\n" +
                            "I have received a big update and from now on I only use [Discord SlashCommands](https://discord.com/developers/docs/interactions/slash-commands)\n" +
                            "You can get a list of all SlashCommands with `/easypoll`\n" +
                            "\n" +
                            "By using SlashCommands I have more possibilities to get new features :smirk:\n" +
                            "Thank you for your understanding :heart:\n" +
                            "\n" +
                            "If you need further help or have any questions, please visit me on my Discord Server [discord.gg/JnuXNCv](https://discord.gg/JnuXNCv)"
            );

            try {
                event.getTextChannel().sendMessageEmbeds(eb.build())
                        .delay(2, TimeUnit.MINUTES)
                        .flatMap(Message::delete)
                        .queue(null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                                .handle(Objects::nonNull, Sentry::captureException)
                        );
            }catch (InsufficientPermissionException ignored) {}
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            Statistics.insertEventCall(StatisticsEvents.DIRECTMESSAGE);
        }
    }
}
