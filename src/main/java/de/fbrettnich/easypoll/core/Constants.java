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

package de.fbrettnich.easypoll.core;

import java.awt.*;

public class Constants {

    public static boolean DEVMODE = true;

    public static final String VERSION = "3.0.0";
    public static final String DEFAULT_LANGUAGE = "en-us";

    public static String BOT_ID = "";
    public static final String BOT_OWNER_MENTION = "<@231091710195662848>";

    public static final String WEBSITE_URL = "https://easypoll.me/?utm_source=discordbot&utm_medium=website&utm_campaign=easypoll";
    public static final String INVITE_URL = "https://discord.com/oauth2/authorize?client_id=437618149505105920&permissions=355392&redirect_uri=https%3A%2F%2Feasypoll.me%2Fdiscord&response_type=code&scope=bot%20applications.commands";
    public static final String VOTE_URL = "https://easypoll.me/vote";

    public static final Color COLOR_POLL_UPDOWN = new Color(0, 255, 255);
    public static final Color COLOR_POLL_CUSTOM_SINGEL = new Color(0, 255, 254);
    public static final Color COLOR_POLL_CUSTOM_MULTI = new Color(0, 255, 253);
    public static final Color COLOR_POLL_CLOSED = new Color(250, 38, 38);

    public static final long STARTUP = System.currentTimeMillis();

}