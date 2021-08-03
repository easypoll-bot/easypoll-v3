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
