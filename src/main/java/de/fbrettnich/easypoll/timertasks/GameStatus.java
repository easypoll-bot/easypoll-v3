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

package de.fbrettnich.easypoll.timertasks;

import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.utils.FormatUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.TimerTask;

public class GameStatus extends TimerTask {

    private final JDA jda;
    private int i = 0;

    public GameStatus(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {

        ArrayList<Activity> activities = new ArrayList<>();

        activities.add(Activity.listening("/poll"));
        activities.add(Activity.listening("/timepoll"));
        activities.add(Activity.listening("/easypoll"));
        activities.add(Activity.watching("www.easypoll.me"));
        activities.add(Activity.playing("easypoll.me | /help"));
        activities.add(Activity.streaming(
                "on " +
                        FormatUtil.decimalFormat(Main.getShardManager().getGuilds().size()) + " Servers | " +
                        FormatUtil.decimalFormat(Main.getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum()) + " Users | " +
                        FormatUtil.decimalFormat(jda.getShardInfo().getShardTotal()) + " Shards",
                "https://www.twitch.tv/floxiii_")
        );

        if(i == activities.size()) i = 0;

        jda.getPresence().setActivity(activities.get(i));

        i++;
    }
}
