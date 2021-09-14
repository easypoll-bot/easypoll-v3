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
        activities.add(Activity.watching("www.easypoll.bot"));
        activities.add(Activity.playing("easypoll.bot | /help"));
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
