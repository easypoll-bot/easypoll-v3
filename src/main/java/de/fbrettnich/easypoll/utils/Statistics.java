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

package de.fbrettnich.easypoll.utils;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.utils.enums.StatisticsCommands;
import de.fbrettnich.easypoll.utils.enums.StatisticsEvents;
import io.sentry.Sentry;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Statistics {

    /**
     * This method adds command statistics to the MySQL database
     *
     * @param command The {@link StatisticsCommands} type of the command statistic
     */
    public static void insertCommandUsage(StatisticsCommands command) {
        if(Constants.DEVMODE) return;

        try {
            PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_commands` (`id`, `command`) VALUES (NULL, ?)");
            ps.setString(1, command.name().toLowerCase());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Sentry.captureException(e);
        }
    }

    /**
     * This method adds event statistics to the MySQL database
     *
     * @param event The {@link StatisticsEvents} type of the event statistic
     */
    public static void insertEventCall(StatisticsEvents event) {
        if(Constants.DEVMODE) return;

        try {
            PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_events` (`id`, `event`) VALUES (NULL, ?)");
            ps.setString(1, event.name().toLowerCase());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Sentry.captureException(e);
        }
    }
}