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

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.timertasks.GameStatus;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        Constants.BOT_ID = event.getJDA().getSelfUser().getId();
        new Timer().schedule(new GameStatus(event.getJDA()), 1000, 2*60*1000);
        System.out.println("[INFO|READY] EasyPoll (Shard #" + event.getJDA().getShardInfo().getShardId() + ") is running on " + event.getJDA().getGuilds().size() + " servers.");

        try {
            Main.startShard(event.getJDA().getShardInfo().getShardId() + 1);
        } catch (InterruptedException e) {
            Sentry.captureException(e);
        }
    }
}
