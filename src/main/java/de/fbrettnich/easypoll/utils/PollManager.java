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

package de.fbrettnich.easypoll.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.lang.Nullable;
import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.utils.enums.PollType;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.Random;

public class PollManager {

    public PollManager() { }

    public MessageEmbed getPollEmbed(String pollId, PollType pollType, long endTime, Boolean closed, @Nullable List<MessageReaction> messageReactions, @Nullable Boolean allowmultiplechoices, String question, List<String> choicesReaction, List<String> choicesContent) {

        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder description = new StringBuilder();

        description
                .append("**Question**\n")
                .append(question)
                .append("\n")
                .append("\n")
                .append("**Choices**\n");

        if (pollType == PollType.DEFAULT_UPDOWN || pollType == PollType.TIME_UPDOWN) {
            eb.setColor(Constants.COLOR_POLL_UPDOWN);
            description
                    .append(":thumbsup: Yes\n")
                    .append(":thumbsdown: No\n");
        } else {
            for (int i = 0; i < choicesReaction.size(); i++) {
                description
                        .append(choicesReaction.get(i))
                        .append(" ")
                        .append(choicesContent.get(i))
                        .append("\n");
            }

            if (allowmultiplechoices != null && allowmultiplechoices) {
                eb.setColor(Constants.COLOR_POLL_CUSTOM_MULTI);
            } else {
                eb.setColor(Constants.COLOR_POLL_CUSTOM_SINGEL);
            }
        }

        if(closed && messageReactions != null) {

            double allReactionsCount = messageReactions.stream().mapToInt(MessageReaction::getCount).sum() - messageReactions.size();

            if(messageReactions.size() >= choicesReaction.size()) {

                description.append("\n**Final Result**\n");

                for (int i = 0; i < choicesReaction.size(); i++) {

                    int reactionCount = messageReactions.get(i).getCount() - 1;

                    double percentage = (reactionCount / allReactionsCount) * 100;
                    if (reactionCount == 0 || allReactionsCount == 0) percentage = 0;

                    description
                            .append(choicesReaction.get(i))
                            .append(" ")
                            .append(getProgressbar(percentage))
                            .append(" [").append(reactionCount)
                            .append(" • ")
                            .append(String.format("%.1f", percentage))
                            .append("%]\n");
                }
            }
        }

        if (pollType == PollType.TIME_UPDOWN || pollType == PollType.TIME_MULTI) {
            if(closed) {
                description
                        .append("\n:alarm_clock: Poll already ended");
            }else{
                description
                        .append("\n:alarm_clock: Ends ")
                        .append("<t:")
                        .append(endTime / 1000L)
                        .append(":R>");
            }
        }

        if (allowmultiplechoices != null && allowmultiplechoices) {
            description.append("\n:white_check_mark: Multiple choices allowed");
        } else {
            description.append("\n:no_entry: Multiple choices not allowed");
        }

        if(closed) {
            eb.setColor(Constants.COLOR_POLL_CLOSED);
            description.append("\n:lock: No other votes allowed");
        }

        eb.setDescription(description);

        eb.setFooter("Poll ID: " + pollId);

        return eb.build();
    }

    public void createPoll(String pollId, String guildId, String channelId, String messageId, String userId, String question, List<String> choicesReaction, List<String> choicesContent, PollType pollType, boolean allowmultiplechoices, long endTime) {

        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject document = new BasicDBObject();

        document.put("pollId", pollId);
        document.put("guildId", guildId);
        document.put("channelId", channelId);
        document.put("messageId", messageId);
        document.put("userId", userId);
        document.put("question", question);
        document.put("choices_reaction", choicesReaction);
        document.put("choices_content", choicesContent);
        document.put("type", pollType.name());
        document.put("multiplechoices", allowmultiplechoices);
        document.put("created", System.currentTimeMillis());
        document.put("end", endTime);
        document.put("closed", 0);
        document.put("active", true);

        collection.insert(document);
    }

    public void closePollByMessageId(String messageId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("messageId", messageId);
        DBObject document = collection.findOne(searchQuery);
        closePoll(collection, searchQuery, document);
    }

    public void closePollByPollId(String pollId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", pollId);
        DBObject document = collection.findOne(searchQuery);
        closePoll(collection, searchQuery, document);
    }

    private void closePoll(DBCollection collection, DBObject searchQuery, DBObject document) {
        if (document != null) {

            document.put("closed", System.currentTimeMillis());
            document.put("active", false);

            collection.update(searchQuery, document);

            Guild guild = Main.getShardManager().getGuildById((String) document.get("guildId"));
            if(guild != null) {

                TextChannel textChannel = null;
                try {
                    textChannel = guild.getTextChannelById((String) document.get("channelId"));
                }catch (InsufficientPermissionException ignored) { }

                if(textChannel != null) {
                    try {
                        try {
                            TextChannel finalTextChannel = textChannel;
                            textChannel.retrieveMessageById((String) document.get("messageId")).queue(message -> {

                                List<MessageReaction> messageReactions = message.getReactions();

                                try {
                                    finalTextChannel.editMessageEmbedsById((String) document.get("messageId"),
                                            new PollManager().getPollEmbed(
                                                    (String) document.get("pollId"),
                                                    PollType.valueOf((String) document.get("type")),
                                                    (long) document.get("end"),
                                                    true,
                                                    messageReactions,
                                                    (boolean) document.get("multiplechoices"),
                                                    (String) document.get("question"),
                                                    (List<String>) document.get("choices_reaction"),
                                                    (List<String>) document.get("choices_content")
                                            )
                                    ).queue(null, Sentry::captureException);
                                } catch (InsufficientPermissionException ignored) {}

                            });
                        }catch (InsufficientPermissionException ignored) { }
                    }catch (ErrorResponseException e) {
                        if(e.getErrorResponse() != ErrorResponse.UNKNOWN_MESSAGE) {
                            Sentry.captureException(e);
                        }
                    }
                }
            }
        }
    }

    public boolean canClosePollByPollId(String pollId, String guildId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject()
                .append("active", true)
                .append("pollId", pollId)
                .append("guildId", guildId);

        return collection.find(searchQuery).hasNext();
    }

    public boolean canClosePollByMessageId(String messageId, String guildId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject()
                .append("active", true)
                .append("messageId", messageId)
                .append("guildId", guildId);

        return collection.find(searchQuery).hasNext();
    }

    public String getPollCreatorIdByPollId(String pollId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", pollId);
        DBObject document = collection.findOne(searchQuery);

        if(document != null) {
            return (String) document.get("userId");
        }

        return "null";
    }

    public String generateUniquePollId() {

        int length = 10;
        String list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(list.length());
            char randomChar = list.charAt(index);
            sb.append(randomChar);
        }

        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", sb.toString());
        if(collection.find(searchQuery).hasNext()) {
            return generateUniquePollId();
        }

        return sb.toString();
    }

    private String getProgressbar(double percentage) {

        int chars = 10;
        int filled = (int) Math.round(percentage / chars);
        int empty = chars - filled;

        StringBuilder progressbar = new StringBuilder();
        for (int i = 0; i < filled; i++) {
            progressbar.append("▓");
        }
        for (int i = 0; i < empty; i++) {
            progressbar.append("░");
        }

        return progressbar.toString();
    }
}
