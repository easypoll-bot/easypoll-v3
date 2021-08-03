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

package de.fbrettnich.easypoll.listener;

import de.fbrettnich.easypoll.utils.Statistics;
import de.fbrettnich.easypoll.utils.enums.StatisticsEvents;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println("[INFO|GUILD] Bot entered " + event.getGuild().getId() + " ( " + event.getGuild().getName() + " )");
        Statistics.insertEventCall(StatisticsEvents.GUILDJOIN);
    }
}
