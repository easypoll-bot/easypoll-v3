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

import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class PingCommand {

    public PingCommand(@Nonnull SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Pong! :ping_pong:");
        eb.setColor(Color.decode("#4CBB17"));
        eb.setDescription(
                "`Ping to Gateway " + ((int) event.getJDA().getGatewayPing()) + " ms`"
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
