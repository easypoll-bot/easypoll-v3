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
