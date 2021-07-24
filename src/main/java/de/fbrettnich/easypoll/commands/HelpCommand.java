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
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class HelpCommand {

    public HelpCommand(@Nonnull SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("EasyPoll Help", Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#FDA50F"));

        eb.addField(
                ":bar_chart: Poll Commands",
                "**/poll** \u2022 Create a normal poll without time limit\n" +
                        "**/timepoll** \u2022 Create a timed poll, set an end date until when the poll will run\n" +
                        "**/closepoll** \u2022 Close a poll so that no more votes are counted",
                false
        );

        eb.addField(
                ":mag_right: Public Commands",
                "**/help** \u2022 Show this Bot Help\n" +
                        "**/vote** \u2022 Vote for the EasyPoll Bot\n" +
                        "**/invite** \u2022 Invite EasyPoll to your own Discord Server\n" +
                        "**/info** \u2022 Show some information about EasyPoll\n" +
                        "**/ping** \u2022 See the Ping of the Bot to the Discord Gateway",
                false
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
