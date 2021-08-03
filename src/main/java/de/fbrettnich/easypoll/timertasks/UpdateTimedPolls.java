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
import de.fbrettnich.easypoll.utils.enums.PollType;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class UpdateTimedPolls extends TimerTask {

    @Override
    public void run() {

        new Thread(() -> {

            DBCollection collection = Main.getMongoDB().getCollection("polls");
            DBObject searchQuery = new BasicDBObject()
                    .append("active", true)
                    .append("timerLastUpdated",
                            new BasicDBObject()
                                    .append("$lt", (System.currentTimeMillis() - (5*60*1000L))))
                    .append("end",
                            new BasicDBObject()
                                    .append("$gt", System.currentTimeMillis())
                    );

            collection.find(searchQuery).limit(5).forEach(document -> {

                Guild guild = Main.getShardManager().getGuildById((String) document.get("guildId"));
                if(guild != null) {

                    TextChannel textChannel = guild.getTextChannelById((String) document.get("channelId"));
                    if (textChannel != null) {

                        try {
                            textChannel.editMessageEmbedsById((String) document.get("messageId"),
                                    new PollManager().getPollEmbed(
                                            (String) document.get("pollId"),
                                            PollType.valueOf((String) document.get("type")),
                                            (long) document.get("end"),
                                            false,
                                            null,
                                            (boolean) document.get("multiplechoices"),
                                            (String) document.get("question"),
                                            (List<String>) document.get("choices_reaction"),
                                            (List<String>) document.get("choices_content")
                                    )
                            ).queue(null, new ErrorHandler()
                                    .handle(ErrorResponse.UNKNOWN_MESSAGE, e -> {
                                        document.put("active", false);
                                        collection.update(new BasicDBObject("messageId", document.get("messageId")), document);
                                    })
                                    .handle(Objects::nonNull, Sentry::captureException)
                            );
                        }catch (InsufficientPermissionException ignored) { }

                    }else{
                        document.put("active", false);
                    }
                }else{
                    document.put("active", false);
                }

                document.put("timerLastUpdated", System.currentTimeMillis());
                collection.update(new BasicDBObject("messageId", document.get("messageId")), document);

            });

        }).start();

    }
}
