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

public class InviteCommand {

    public InviteCommand(@Nonnull SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Invite EasyPoll", Constants.INVITE_URL);
        eb.setColor(Color.decode("#5865F2"));
        eb.setDescription(
                "You can invite " + event.getJDA().getSelfUser().getAsMention() + " here: [www.easypoll.me/invite](https://easypoll.me/invite)"
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
