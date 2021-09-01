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
import de.fbrettnich.easypoll.language.GuildLanguage;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nonnull;
import java.awt.*;

public class HelpCommand {

    public HelpCommand(@Nonnull SlashCommandEvent event, GuildLanguage gl) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(gl.getTl("commands.help.title"), Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#FDA50F"));

        eb.addField(
                gl.getTl("commands.help.fields.poll_commands.title"),
                "**/poll** \u2022 " + gl.getTl("commands.help.fields.poll_commands.commands.poll") + "\n" +
                        "**/timepoll** \u2022 " + gl.getTl("commands.help.fields.poll_commands.commands.timepoll") + "\n" +
                        "**/closepoll** \u2022 " + gl.getTl("commands.help.fields.poll_commands.commands.closepoll"),
                false
        );

        eb.addField(
                gl.getTl("commands.help.fields.public_commands.title"),
                "**/help** \u2022 " + gl.getTl("commands.help.fields.public_commands.commands.help") + "\n" +
                        "**/vote** \u2022 " + gl.getTl("commands.help.fields.public_commands.commands.vote") + "\n" +
                        "**/invite** \u2022 " + gl.getTl("commands.help.fields.public_commands.commands.invite") + "\n" +
                        "**/info** \u2022 " + gl.getTl("commands.help.fields.public_commands.commands.info") + "\n" +
                        "**/ping** \u2022 " + gl.getTl("commands.help.fields.public_commands.commands.ping"),
                false
        );

        event.replyEmbeds(
                        eb.build()
                )
                .addActionRow(
                        Button.link(Constants.DOCUMENTATION_URL, gl.getTl("commands.help.buttons.documentation")),
                        Button.link(Constants.DISCORD_DIRECT_URL, gl.getTl("commands.help.buttons.supportdiscord"))
                )
                .queue(null, Sentry::captureException);

    }
}
