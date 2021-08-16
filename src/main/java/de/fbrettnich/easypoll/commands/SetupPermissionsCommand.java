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
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
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

public class SetupPermissionsCommand {

    public SetupPermissionsCommand(@Nonnull SlashCommandEvent event) {

        event.deferReply().queue(null, Sentry::captureException);

        if(event.getGuild() == null) return;

        InteractionHook hook = event.getHook();
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        GuildChannel guildChannel = event.getGuildChannel();

        if(member == null) return;

        if(
                !member.isOwner() &&
                !member.hasPermission(Permission.ADMINISTRATOR) &&
                !member.hasPermission(Permission.MANAGE_PERMISSIONS)
        )
        {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTitle("You do not have premissions to use this command!", Constants.WEBSITE_URL);
            eb.addField(
                    "To use this command you need at least one of them:",
                    "\u2022 ADMINISTRATOR *(Permission)*\n" +
                            "\u2022 MANAGE_PERMISSIONS *(Permission)*",
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

        eb.setTitle("Required Bot Permission Check", "https://github.com/fbrettnich/easypoll-bot/wiki/Required-Bot-Permissions");
        eb.setColor(Color.decode("#FDA50F"));
        eb.setDescription(
                "**Server Permissions**\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_READ) ? ":white_check_mark:" : ":no_entry_sign:") + " Read Messages\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_WRITE) ? ":white_check_mark:" : ":no_entry_sign:") + " Send Messages\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_MANAGE) ? ":white_check_mark:" : ":no_entry_sign:") + " Manage Messages\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS) ? ":white_check_mark:" : ":no_entry_sign:") + " Embed Links\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_HISTORY) ? ":white_check_mark:" : ":no_entry_sign:") + " Read message History\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_ADD_REACTION) ? ":white_check_mark:" : ":no_entry_sign:") + " Add Reactions\n" +
                        (selfMember.hasPermission(Permission.MESSAGE_EXT_EMOJI) ? ":white_check_mark:" : ":no_entry_sign:") + " Use External Emojis\n" +
                        "\n" +
                "**Channel Permissions** (#" + event.getChannel().getName() + ")\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_READ) ? ":white_check_mark:" : ":no_entry_sign:") + " Read Messages\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_WRITE) ? ":white_check_mark:" : ":no_entry_sign:") + " Send Messages\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_MANAGE) ? ":white_check_mark:" : ":no_entry_sign:") + " Manage Messages\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_EMBED_LINKS) ? ":white_check_mark:" : ":no_entry_sign:") + " Embed Links\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_HISTORY) ? ":white_check_mark:" : ":no_entry_sign:") + " Read message History\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_ADD_REACTION) ? ":white_check_mark:" : ":no_entry_sign:") + " Add Reactions\n" +
                        (selfMember.hasPermission(guildChannel, Permission.MESSAGE_EXT_EMOJI) ? ":white_check_mark:" : ":no_entry_sign:") + " Use External Emojis"
        );

        hook.sendMessageEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }
}
