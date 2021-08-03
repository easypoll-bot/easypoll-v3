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

package de.fbrettnich.easypoll.commands;

import de.fbrettnich.easypoll.core.Constants;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class VoteCommand {

    public VoteCommand(@Nonnull SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Vote for EasyPoll", Constants.VOTE_URL);
        eb.setColor(Color.decode("#01FF70"));
        eb.setDescription("" +
                "We are happy that you want to vote for our bot. You show us that you like the bot and help us to grow further.\n" +
                "\n" +
                "You can vote for us on the following pages:\n" +
                "\u2022 [top.gg/bot/437618149505105920](https://top.gg/bot/437618149505105920/vote)\n" +
                "\u2022 [dbots.me/bot/easypoll](https://dbots.me/bot/easypoll/vote)\n" +
                "\u2022 [discordbotlist.com/bots/easypoll](https://discordbotlist.com/bots/easypoll/upvote)\n"
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
