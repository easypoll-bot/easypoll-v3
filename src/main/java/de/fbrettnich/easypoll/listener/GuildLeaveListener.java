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
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        System.out.println("[INFO|GUILD] Bot left " + event.getGuild().getId() + " ( " + event.getGuild().getName() + " )");
        Statistics.insertEventCall(StatisticsEvents.GUILDLEAVE);
    }
}
