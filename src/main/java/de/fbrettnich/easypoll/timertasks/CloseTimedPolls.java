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

package de.fbrettnich.easypoll.timertasks;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.utils.PollManager;

import java.util.TimerTask;

public class CloseTimedPolls extends TimerTask {

    @Override
    public void run() {

        new Thread(() -> {

            DBCollection collection = Main.getMongoDB().getCollection("polls");

            DBObject searchQuery = new BasicDBObject()
                    .append("active", true)
                    .append("end",
                            new BasicDBObject()
                                    .append("$gt", 0)
                                    .append("$lt", System.currentTimeMillis())
                    );

            collection.find(searchQuery).limit(5).forEach(document -> new PollManager().closePollByPollId((String) document.get("pollId")));

        }).start();

    }
}
