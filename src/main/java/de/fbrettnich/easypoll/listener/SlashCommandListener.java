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

import de.fbrettnich.easypoll.commands.*;
import de.fbrettnich.easypoll.language.GuildLanguage;
import de.fbrettnich.easypoll.utils.Statistics;
import de.fbrettnich.easypoll.utils.enums.StatisticsCommands;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener  extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        if(event.getGuild() == null) return;

        GuildLanguage gl = new GuildLanguage(event.getGuild());

        String commandName = event.getName();
        String subCommandName = event.getSubcommandName();

        switch (commandName) {
            case "closepoll":
                new ClosePollCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_CLOSEPOLL);
                break;

            case "help":
            case "easypoll":
                new HelpCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_HELP);
                break;

            case "info":
                new InfoCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_INFO);
                break;

            case "invite":
                new InviteCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_INVITE);
                break;

            case "ping":
                new PingCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_PING);
                break;

            case "poll":
                new PollCommand(event, gl, false);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_POLL);
                break;

            case "timepoll":
                new PollCommand(event, gl, true);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_TIMEPOLL);
                break;

            case "setup":
                if(subCommandName == null) break;
                switch (subCommandName) {
                    case "language":
                        new SetupLanguageCommand(event, gl);
                        break;
                    case "permissions":
                        new SetupPermissionsCommand(event, gl);
                        break;
                }
                break;

            case "vote":
                new VoteCommand(event, gl);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_VOTE);
                break;

            default:
                event.reply("Sorry! I cannot process this command.").queue(null, Sentry::captureException);
                Sentry.captureMessage("Cannot process command: " + commandName, SentryLevel.ERROR);
                break;
        }
    }
}
