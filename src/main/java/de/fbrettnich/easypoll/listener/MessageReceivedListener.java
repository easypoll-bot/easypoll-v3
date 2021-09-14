/*
 * EasyPoll Discord Bot (https://github.com/easypoll-bot/easypoll-java)
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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User user = event.getAuthor();
        if (user.isBot()) return;

        if(event.isFromType(ChannelType.TEXT)) {
            List<Member> mentionedUsers = event.getMessage().getMentionedMembers();
            if (!mentionedUsers.isEmpty() && mentionedUsers.contains(event.getGuild().getSelfMember())) {

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("How to use EasyPoll", Constants.WEBSITE_URL);
                eb.setColor(Color.decode("#01FF70"));
                eb.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());

                eb.addField("Bot Prefix", "`/` *(Using SlashCommands)*", false);
                eb.addField("Help Command", "`/help`", false);
                eb.addField("Website", "[www.easypoll.bot](" + Constants.WEBSITE_URL + ")", false);

                eb.setFooter("Requested by " + user.getName() + "#" + user.getDiscriminator());
                eb.setTimestamp(new Date().toInstant());

                try {
                    event.getTextChannel().sendMessageEmbeds(eb.build())
                            .delay(30, TimeUnit.SECONDS)
                            .flatMap(Message::delete)
                            .queue(null, new ErrorHandler()
                                    .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                                    .handle(Objects::nonNull, Sentry::captureException)
                            );
                } catch (InsufficientPermissionException ignored) {
                }

            }
        }

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
                    "Hello " + event.getAuthor().getAsMention() + " :wave:, here is [EasyPoll](https://easypoll.bot/) always at your service!\n" +
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
