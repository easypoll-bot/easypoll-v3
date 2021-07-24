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

package de.fbrettnich.easypoll.listener;

import de.fbrettnich.easypoll.commands.*;
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

        String commandName = event.getName();

        switch (commandName) {

            case "closepoll":
                new ClosePollCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_CLOSEPOLL);
                break;

            case "help":
            case "easypoll":
                new HelpCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_HELP);
                break;

            case "info":
                new InfoCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_INFO);
                break;

            case "invite":
                new InviteCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_INVITE);
                break;

            case "ping":
                new PingCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_PING);
                break;

            case "poll":
                new PollCommand(event, false);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_POLL);
                break;

            case "timepoll":
                new PollCommand(event, true);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_TIMEPOLL);
                break;

            case "vote":
                new VoteCommand(event);
                Statistics.insertCommandUsage(StatisticsCommands.SLASH_VOTE);
                break;

            default:
                event.reply("Sorry! I cannot process this command.").queue(null, Sentry::captureException);
                Sentry.captureMessage("Cannot process command: " + commandName, SentryLevel.ERROR);
                break;
        }

    }
}
