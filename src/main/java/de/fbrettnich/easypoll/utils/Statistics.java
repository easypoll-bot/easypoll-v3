/*
 * Copyright (c) 2021 Felix Brettnich
 *
 * This file is part of EasyPoll (https://github.com/fbrettnich/easypoll)
 *
 * All contents of this source code are protected by copyright.
 * The copyright lies, if not expressly differently marked,
 * by Felix Brettnich. All rights reserved.
 *
 * Any kind of duplication, distribution, rental, lending,
 * public accessibility or other use requires the explicit,
 * written consent from Felix Brettnich
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
     * @param command The type of the command statistic
     */
    public static void insertCommandUsage(StatisticsCommands command) {
        if(Constants.DEVMODE) return;

        try {
            PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_commands` (`id`, `command`) VALUES (NULL, ?)");
            ps.setString(1, command.name().toLowerCase());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Sentry.captureException(ex);
        }
    }

    /**
     * This method adds event statistics to the MySQL database
     *
     * @param event The type of the event statistic
     */
    public static void insertEventCall(StatisticsEvents event) {
        if(Constants.DEVMODE) return;

        try {
            PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_events` (`id`, `event`) VALUES (NULL, ?)");
            ps.setString(1, event.name().toLowerCase());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Sentry.captureException(ex);
        }
    }
}
