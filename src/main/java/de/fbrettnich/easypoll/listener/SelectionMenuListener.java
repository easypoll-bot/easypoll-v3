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

package de.fbrettnich.easypoll.listener;

import de.fbrettnich.easypoll.language.GuildLanguage;
import de.fbrettnich.easypoll.selectionmenus.ChangeLanguageMenu;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelectionMenuListener extends ListenerAdapter {

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {

        if(event.getGuild() == null) return;

        GuildLanguage gl = new GuildLanguage(event.getGuild());

        String componentId = event.getComponentId();

        switch (componentId) {
            case "ChangeLanguageMenu":
                new ChangeLanguageMenu(event, gl);
                break;

            default:
                event.reply("Sorry! I cannot process this selection.").queue(null, Sentry::captureException);
                Sentry.captureMessage("Cannot process selection: " + componentId, SentryLevel.ERROR);
                break;
        }
    }
}
