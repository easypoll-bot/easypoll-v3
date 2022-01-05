/*
 * EasyPoll Discord Bot (https://github.com/easypoll-bot/easypoll-v3)
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

package de.fbrettnich.easypoll.selectionmenus;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.language.GuildLanguage;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.ErrorResponse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChangeLanguageMenu {

    public ChangeLanguageMenu(@Nonnull SelectionMenuEvent event, GuildLanguage gl) {

        event.deferReply().queue(null, Sentry::captureException);

        InteractionHook hook = event.getHook();
        User user = event.getUser();
        Member member = event.getMember();
        Message message = event.getMessage();
        List<SelectOption> selectOptions = event.getSelectedOptions();

        if(member == null) return;
        if(selectOptions == null) return;

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

        if(message != null) {
            try {
                message.delete().queue(null, Sentry::captureException);
            }catch (MissingAccessException ignored) {}
        }

        String lang = Constants.DEFAULT_LANGUAGE;
        if(!selectOptions.isEmpty()) {
            lang = selectOptions.get(0).getValue();
        }

        gl.setLanguage(lang);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(gl.getTl("commands.setup.language.success.title"), Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#01FF70"));
        eb.setDescription(gl.getTl("commands.setup.language.success.description", gl.getTl("translation.name_local")));
        eb.setFooter(gl.getTl("commands.setup.language.success.footer", (user.getName() + "#" + user.getDiscriminator())));

        hook.sendMessageEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);
    }
}
