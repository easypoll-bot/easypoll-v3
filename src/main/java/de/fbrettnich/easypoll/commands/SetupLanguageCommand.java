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
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.language.GuildLanguage;
import de.fbrettnich.easypoll.language.TranslationManager;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SetupLanguageCommand {

    public SetupLanguageCommand(@Nonnull SlashCommandEvent event, GuildLanguage gl) {

        event.deferReply().queue(null, Sentry::captureException);

        TranslationManager tm = Main.getTranslationManager();
        InteractionHook hook = event.getHook();
        Member member = event.getMember();

        if(member == null) return;

        if(
                !member.isOwner() &&
                !member.hasPermission(Permission.ADMINISTRATOR) &&
                !member.hasPermission(Permission.MANAGE_PERMISSIONS)
        )
        {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTitle(gl.getTl("errors.no_permissions.member.title"), Constants.WEBSITE_URL);
            eb.addField(
                    gl.getTl("errors.no_permissions.member.field.title"),
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

        eb.setTitle(gl.getTl("commands.setup.language.change.title"), Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#01FF70"));
        eb.setDescription(gl.getTl("commands.setup.language.change.description"));


        SelectionMenu.Builder selectionMenuBuilder = SelectionMenu
                .create("ChangeLanguageMenu")
                .setPlaceholder(gl.getTl("commands.setup.language.change.selectionmenu.placeholder"))
                .setMinValues(1)
                .setMaxValues(1);

        tm.getLanguages().forEach(lang -> selectionMenuBuilder.addOption(
                        tm.getTranslation(lang, "translation.name_local"),
                        lang,
                        tm.getTranslation(lang, "translation.name"),
                        Emoji.fromUnicode(tm.getTranslation(lang, "translation.flag_unicode"))
                )
        );

        hook.sendMessageEmbeds(
                        eb.build()
                )
                .addActionRow(
                        selectionMenuBuilder.build()
                )
                .queue(null, Sentry::captureException);

    }
}
