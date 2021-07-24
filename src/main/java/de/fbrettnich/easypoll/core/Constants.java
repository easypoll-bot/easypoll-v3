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

package de.fbrettnich.easypoll.core;

import java.awt.*;

public class Constants {

    public static boolean DEVMODE = true;

    public static final String VERSION = "3.0.0";

    public static String BOT_ID = "";
    public static final String BOT_OWNER_MENTION = "<@231091710195662848>";

    public static final String WEBSITE_URL = "https://easypoll.me/";
    public static final String INVITE_URL = "https://easypoll.me/invite";
    public static final String VOTE_URL = "https://easypoll.me/vote";
    public static final String DISCORD_URL = "https://easypoll.me/discord";
    public static final String ICON_URL = "https://easypoll.me/bot-images/easypoll-logo.png";

    public static final Color COLOR_POLL_UPDOWN = new Color(0, 255, 255);
    public static final Color COLOR_POLL_CUSTOM_SINGEL = new Color(0, 255, 254);
    public static final Color COLOR_POLL_CUSTOM_MULTI = new Color(0, 255, 253);
    public static final Color COLOR_POLL_CLOSED = new Color(250, 38, 38);

    public static final long STARTUP = System.currentTimeMillis();

}