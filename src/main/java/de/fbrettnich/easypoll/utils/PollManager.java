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

package de.fbrettnich.easypoll.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.lang.Nullable;
import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.language.GuildLanguage;
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

    /**
     * Generate the MessageEmbed for a Poll
     *
     * @param pollId ID of the poll
     * @param pollType Type of the poll
     * @param endTime Timestamp when the poll ends
     * @param closed If the poll is closed
     * @param messageReactions Current Reactions of the Poll Message
     * @param allowmultiplechoices If multiple answers are allowed
     * @param question Poll question
     * @param choicesReaction Reaction list of the answers
     * @param choicesContent Text list of the answers
     * @param gl {@link GuildLanguage} Guild language manager
     * @return The full and complete message embed
     */
    public MessageEmbed getPollEmbed(String pollId, PollType pollType, long endTime, Boolean closed, @Nullable List<MessageReaction> messageReactions, @Nullable Boolean allowmultiplechoices, String question, List<String> choicesReaction, List<String> choicesContent, GuildLanguage gl) {

        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder description = new StringBuilder();

        description
                .append("**")
                .append(gl.getTl("polls.question"))
                .append("**\n")
                .append(question)
                .append("\n")
                .append("\n")
                .append("**")
                .append(gl.getTl("polls.choices"))
                .append("**\n");

        if (pollType == PollType.DEFAULT_UPDOWN || pollType == PollType.TIME_UPDOWN) {
            eb.setColor(Constants.COLOR_POLL_UPDOWN);
            description
                    .append(":thumbsup: ")
                    .append(gl.getTl("polls.yes"))
                    .append("\n")
                    .append(":thumbsdown: ")
                    .append(gl.getTl("polls.no"))
                    .append("\n");
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

                description
                        .append("\n**")
                        .append(gl.getTl("polls.finalresult"))
                        .append("**\n");

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
                description.
                        append("\n:alarm_clock: ")
                        .append(gl.getTl("polls.alreadyended"));
            }else{
                description
                        .append("\n:alarm_clock: ")
                        .append(gl.getTl("polls.ends"))
                        .append(" ")
                        .append("<t:")
                        .append(endTime / 1000L)
                        .append(":R>");
            }
        }

        if (allowmultiplechoices != null && allowmultiplechoices) {
            description
                    .append("\n:white_check_mark: ")
                    .append(gl.getTl("polls.multiplechoice.allowed"));
        } else {
            description
                    .append("\n:no_entry: ")
                    .append(gl.getTl("polls.multiplechoice.disallowed"));
        }

        if(closed) {
            eb.setColor(Constants.COLOR_POLL_CLOSED);
            description
                    .append("\n:lock: ")
                    .append(gl.getTl("polls.noothervotes"));
        }

        eb.setDescription(description);

        eb.setFooter(gl.getTl("polls.pollid", pollId));

        return eb.build();
    }

    /**
     * Create a new poll in the database
     *
     * @param pollId ID of the poll
     * @param guildId ID of the guild
     * @param channelId ID of the channel
     * @param messageId ID of the poll message
     * @param userId ID of the poll creator user
     * @param question Poll question
     * @param choicesReaction Reaction list of the answers
     * @param choicesContent Text list of the answers
     * @param pollType Type of the poll
     * @param allowmultiplechoices If multiple answers are allowed
     * @param endTime Timestamp when the poll ends
     */
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

    /**
     * Close a poll based on the Message ID
     *
     * @param messageId ID of the message
     */
    public void closePollByMessageId(String messageId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("messageId", messageId);
        DBObject document = collection.findOne(searchQuery);
        closePoll(collection, searchQuery, document);
    }

    /**
     * Close a poll based on the Poll ID
     *
     * @param pollId ID of the poll
     */
    public void closePollByPollId(String pollId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", pollId);
        DBObject document = collection.findOne(searchQuery);
        closePoll(collection, searchQuery, document);
    }

    /**
     * Close a poll and update the message
     *
     * @param collection MongoDB Collection
     * @param searchQuery Database search query
     * @param document Mongo doucument
     */
    private void closePoll(DBCollection collection, DBObject searchQuery, DBObject document) {
        if (document != null) {

            document.put("closed", System.currentTimeMillis());
            document.put("active", false);
            document.put("closeMessageUpdateFailed", false);

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
                                                    (List<String>) document.get("choices_content"),
                                                    new GuildLanguage((String) document.get("guildId"))
                                            )
                                    ).queue(null, Sentry::captureException);
                                } catch (InsufficientPermissionException ex) {
                                    document.put("closeMessageUpdateFailed", true);
                                }

                            });
                        }catch (InsufficientPermissionException ex) {
                            document.put("closeMessageUpdateFailed", true);
                        }
                    }catch (ErrorResponseException e) {
                        document.put("closeMessageUpdateFailed", true);
                        if(e.getErrorResponse() != ErrorResponse.UNKNOWN_MESSAGE) {
                            Sentry.captureException(e);
                        }
                    }
                }
            }

            collection.update(searchQuery, document);
        }
    }

    /**
     * Reload a poll based on the Message ID
     *
     * @param messageId ID of the message
     */
    public void reloadPollByMessageId(String messageId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("messageId", messageId);
        DBObject document = collection.findOne(searchQuery);
        reloadPoll(collection, searchQuery, document);
    }

    /**
     * Reload a poll based on the Poll ID
     *
     * @param pollId ID of the poll
     */
    public void reloadPollByPollId(String pollId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", pollId);
        DBObject document = collection.findOne(searchQuery);
        reloadPoll(collection, searchQuery, document);
    }

    /**
     * Reload a poll and update the message
     *
     * @param collection MongoDB Collection
     * @param searchQuery Database search query
     * @param document Mongo doucument
     */
    private void reloadPoll(DBCollection collection, DBObject searchQuery, DBObject document) {
        if (document != null) {

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
                                                    (!(boolean) document.get("active")),
                                                    messageReactions,
                                                    (boolean) document.get("multiplechoices"),
                                                    (String) document.get("question"),
                                                    (List<String>) document.get("choices_reaction"),
                                                    (List<String>) document.get("choices_content"),
                                                    new GuildLanguage((String) document.get("guildId"))
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

    /**
     * Checking on the basis of the Poll ID if a poll can be closed
     *
     * @param pollId ID of the poll
     * @param guildId ID of the guild
     * @return true if poll can be closed, otherwise false
     */
    public boolean canClosePollByPollId(String pollId, String guildId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject()
                .append("pollId", pollId)
                .append("guildId", guildId);

        DBObject document = collection.findOne(searchQuery);

        if(document != null) {
            return (document.get("active") != null && (Boolean) document.get("active")) || (document.get("closeMessageUpdateFailed") != null && (Boolean) document.get("closeMessageUpdateFailed"));
        }

        return false;
    }

    /**
     * Checking on the basis of the Message ID if a poll can be closed
     *
     * @param messageId ID of the message
     * @param guildId ID of the guild
     * @return true if poll can be closed, otherwise false
     */
    public boolean canClosePollByMessageId(String messageId, String guildId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject()
                .append("messageId", messageId)
                .append("guildId", guildId);

        DBObject document = collection.findOne(searchQuery);

        if(document != null) {
            return (document.get("active") != null && (Boolean) document.get("active")) || (document.get("closeMessageUpdateFailed") != null && (Boolean) document.get("closeMessageUpdateFailed"));
        }

        return false;
    }

    /**
     * Get the User ID of a poll creator
     *
     * @param pollId ID of the poll
     * @return User ID of the poll creator
     */
    public String getPollCreatorIdByPollId(String pollId) {
        DBCollection collection = Main.getMongoDB().getCollection("polls");
        DBObject searchQuery = new BasicDBObject("pollId", pollId);
        DBObject document = collection.findOne(searchQuery);

        if(document != null) {
            return (String) document.get("userId");
        }

        return "null";
    }

    /**
     * Generate a new and unique Poll ID
     *
     * @return A unique Poll ID
     */
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

    /**
     * Convert the percentage to a progress bar
     *
     * @param percentage The percentage
     * @return The progress bar
     */
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
