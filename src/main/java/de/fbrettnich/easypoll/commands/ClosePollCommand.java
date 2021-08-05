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

package de.fbrettnich.easypoll.commands;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.utils.Permissions;
import de.fbrettnich.easypoll.utils.PollManager;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ClosePollCommand {

    public ClosePollCommand(@Nonnull SlashCommandEvent event) {

        event.deferReply().queue(null, Sentry::captureException);

        InteractionHook hook = event.getHook();
        Member member = event.getMember();

        if(member == null) return;

        PollManager pm = new PollManager();
        String pollId = event.getOption("pollid").getAsString();

        if(
                !member.isOwner() &&
                !member.hasPermission(Permission.ADMINISTRATOR) &&
                !member.hasPermission(Permission.MANAGE_PERMISSIONS) &&
                !new Permissions(member).hasPollCreatorRole() &&
                !pm.getPollCreatorIdByPollId(pollId).equals(member.getId()))
        {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTitle("You do not have premissions to use this command!", Constants.WEBSITE_URL);
            eb.addField(
                    "To use this command you need at least one of them:",
                    "\u2022 ADMINISTRATOR *(Permission)*\n" +
                            "\u2022 MANAGE_PERMISSIONS *(Permission)*\n" +
                            "\u2022 PollCreator *(Role)*\n" +
                            "\u2022 Creator of this Poll",
                    true);

            hook.sendMessageEmbeds(
                    eb.build()
            )
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(Objects::nonNull, Sentry::captureException)
                    );

            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        if(pm.canClosePollByPollId(pollId, event.getGuild().getId())) {
            pm.closePollByPollId(pollId);

            eb.setTitle("Poll closed", Constants.WEBSITE_URL);
            eb.setColor(Color.decode("#01FF70"));
            eb.setDescription(
                    "The poll with ID **" + pollId + "** was closed!\n" +
                    "No more votes are allowed and the result is now displayed in the poll."
            );
        }else{
            eb.setTitle("Poll can not be closed", Constants.WEBSITE_URL);
            eb.setColor(Color.RED);
            eb.setDescription("The poll with ID **" + pollId + "** does not exist or is already closed!\n");
        }

        hook.sendMessageEmbeds(
                eb.build()
        )
                .delay(30, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                        .handle(Objects::nonNull, Sentry::captureException)
                );
    }
}
