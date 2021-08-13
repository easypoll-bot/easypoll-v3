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

import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class SetupPermissionsCommand {

    public SetupPermissionsCommand(@Nonnull SlashCommandEvent event) {

        if(event.getGuild() == null) return;

        Member member = event.getGuild().getSelfMember();
        GuildChannel guildChannel = event.getGuildChannel();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Required Bot Permission Check", "https://github.com/fbrettnich/easypoll-bot/wiki/Required-Bot-Permissions");
        eb.setColor(Color.decode("#FDA50F"));
        eb.setDescription(
                "**Server Permissions**\n" +
                        (member.hasPermission(Permission.MESSAGE_READ) ? ":white_check_mark:" : ":no_entry_sign:") + " Read Messages\n" +
                        (member.hasPermission(Permission.MESSAGE_WRITE) ? ":white_check_mark:" : ":no_entry_sign:") + " Send Messages\n" +
                        (member.hasPermission(Permission.MESSAGE_MANAGE) ? ":white_check_mark:" : ":no_entry_sign:") + " Manage Messages\n" +
                        (member.hasPermission(Permission.MESSAGE_EMBED_LINKS) ? ":white_check_mark:" : ":no_entry_sign:") + " Embed Links\n" +
                        (member.hasPermission(Permission.MESSAGE_HISTORY) ? ":white_check_mark:" : ":no_entry_sign:") + " Read message History\n" +
                        (member.hasPermission(Permission.MESSAGE_ADD_REACTION) ? ":white_check_mark:" : ":no_entry_sign:") + " Add Reactions\n" +
                        (member.hasPermission(Permission.MESSAGE_EXT_EMOJI) ? ":white_check_mark:" : ":no_entry_sign:") + " Use External Emojis\n" +
                        "\n" +
                "**Channel Permissions** (#" + event.getChannel().getName() + ")\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_READ) ? ":white_check_mark:" : ":no_entry_sign:") + " Read Messages\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_WRITE) ? ":white_check_mark:" : ":no_entry_sign:") + " Send Messages\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_MANAGE) ? ":white_check_mark:" : ":no_entry_sign:") + " Manage Messages\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_EMBED_LINKS) ? ":white_check_mark:" : ":no_entry_sign:") + " Embed Links\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_HISTORY) ? ":white_check_mark:" : ":no_entry_sign:") + " Read message History\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_ADD_REACTION) ? ":white_check_mark:" : ":no_entry_sign:") + " Add Reactions\n" +
                        (member.hasPermission(guildChannel, Permission.MESSAGE_EXT_EMOJI) ? ":white_check_mark:" : ":no_entry_sign:") + " Use External Emojis"
        );

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
