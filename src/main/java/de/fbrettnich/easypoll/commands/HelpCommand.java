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
