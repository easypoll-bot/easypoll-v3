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

package de.fbrettnich.easypoll.commands;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.language.GuildLanguage;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class VoteCommand {

    public VoteCommand(@Nonnull SlashCommandEvent event, GuildLanguage gl) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(gl.getTl("commands.vote.title"), Constants.VOTE_URL);
        eb.setColor(Color.decode("#01FF70"));
        eb.setDescription(
                gl.getTl("commands.vote.description") +
                "\n" +
                "\u2022 [top.gg/bot/437618149505105920](https://top.gg/bot/437618149505105920/vote)\n" +
                "\u2022 [discordbotlist.com/bots/easypoll](https://discordbotlist.com/bots/easypoll/upvote)\n"
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
