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

package de.fbrettnich.easypoll.commands;

import com.vdurmont.emoji.EmojiManager;
import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.utils.Permissions;
import de.fbrettnich.easypoll.utils.PollManager;
import de.fbrettnich.easypoll.utils.enums.PollType;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollCommand {

    public PollCommand(@Nonnull SlashCommandEvent event, boolean isTimePoll) {

        event.deferReply().queue(null, Sentry::captureException);

        InteractionHook hook = event.getHook();
        Guild guild = event.getGuild();
        User user = event.getUser();
        Member member = event.getMember();

        if(guild == null) return;
        if(member == null) return;

        if(
                !member.isOwner() &&
                !member.hasPermission(Permission.ADMINISTRATOR) &&
                !member.hasPermission(Permission.MANAGE_PERMISSIONS) &&
                !new Permissions(event.getMember()).hasPollCreatorRole() &&
                !event.getChannel().getName().toLowerCase().contains("easypoll") &&
                !(event.getTextChannel().getTopic() != null && event.getTextChannel().getTopic().toLowerCase().contains("easypoll"))
        ) {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTitle("You do not have premissions to use this command!", Constants.WEBSITE_URL);
            eb.addField(
                    "To use this command you need at least one of them:",
                    "\u2022 ADMINISTRATOR *(Permission)*\n" +
                            "\u2022 MANAGE_PERMISSIONS *(Permission)*\n" +
                            "\u2022 PollCreator *(Role)*\n" +
                            "\u2022 EasyPoll *(in Channel Name)*\n" +
                            "\u2022 EasyPoll *(in Channel Topic)*",
                    true);

            hook.sendMessageEmbeds(
                    eb.build()
            )
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(Objects::nonNull, Sentry::captureException)
                    );

            return;
        }


        PollType pollType;
        long endTime = -1;

        List<OptionMapping> optionDataList = event.getOptions();
        OptionMapping optionDataQuestion = event.getOption("question");

        if(optionDataQuestion == null) return;

        String pollId = new PollManager().generateUniquePollId();
        String question = optionDataQuestion.getAsString();
        question = question.substring(0, Math.min(question.length(), 1950));

        if(isTimePoll) {
            String time = event.getOption("time").getAsString();
            time = time.replace(" ", "");

            long multiplier = 1L;
            if(time.endsWith("m")) {
                time = time.replace("m", "");
                multiplier = 60L;
            }else if(time.endsWith("h")) {
                time = time.replace("h", "");
                multiplier = 60*60L;
            }else if(time.endsWith("d")) {
                time = time.replace("d", "");
                multiplier = 24*60*60L;
            }

            try {
                Integer.parseInt(time);
            } catch(NumberFormatException e) {
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(Color.ORANGE);
                eb.setTitle("Invalid time specification!", Constants.WEBSITE_URL);
                eb.addField(
                        "You have entered an invalid time!",
                        "Used at the end of a number m (minute), h (hour) or d (day) for time specifications.\n" +
                                "Examples: 15m for 15 minutes, 1h for 1 hour, 3d for 3 days\n" +
                                "\n" +
                                "*PS: You will find your sent command of this poll to copy as a message to which I replied (click on the blue `/timepoll` command above this message)*",
                        true);

                hook.sendMessageEmbeds(
                        eb.build()
                ).queue(null, Sentry::captureException);

                return;
            }

            long totalTime = Integer.parseInt(time) * multiplier * 1000L;
            if(totalTime > 7*24*60*60*1000L) totalTime = 7*24*60*60*1000L;
            endTime = System.currentTimeMillis() + totalTime;
        }

        if(optionDataList.stream().map(OptionMapping::getName).noneMatch(s -> s.startsWith("answer"))) {

            pollType = isTimePoll ? PollType.TIME_UPDOWN : PollType.DEFAULT_UPDOWN;

            Message message = null;
            try {
                message = hook.sendMessageEmbeds(
                        new PollManager().getPollEmbed(
                                pollId,
                                pollType,
                                endTime,
                                false,
                                null,
                                null,
                                question,
                                null,
                                null
                        )
                ).complete();
            }catch (Exception e) {
                Sentry.captureException(e);
            }

            if(message == null) return;

            new PollManager().createPoll(
                    pollId,
                    guild.getId(),
                    event.getChannel().getId(),
                    message.getId(),
                    user.getId(),
                    question,
                    Arrays.asList(":thumbsup:", ":thumbsdown:"),
                    Arrays.asList("Yes", "No"),
                    pollType,
                    false,
                    endTime
            );

            try {
                message.addReaction("\uD83D\uDC4D").queue(null, Sentry::captureException); // 👍 :thumbsup:
                message.addReaction("\uD83D\uDC4E").queue(null, Sentry::captureException); // 👎 :thumbsdown:
            }catch (InsufficientPermissionException e) {

                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(Color.RED);
                eb.setTitle("I do not have permissions to add reactions to the messages in the specified channel.");
                eb.addField("Please make sure that I have the following permissions",
                        "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
                        true);

                hook.sendMessageEmbeds(
                        eb.build()
                ).queue(null, Sentry::captureException);
            }

        } else {

            pollType = isTimePoll ? PollType.TIME_MULTI : PollType.DEFAULT_MULTI;

            ArrayList<String> reactionsListAdd = new ArrayList<>();
            ArrayList<String> reactionsList = new ArrayList<>();
            ArrayList<String> choiceList = new ArrayList<>();
            OptionMapping optionDataAllowmultiplechoices = event.getOption("allowmultiplechoices");
            boolean allowmultiplechoices = (optionDataAllowmultiplechoices != null && optionDataAllowmultiplechoices.getAsBoolean());

            int choiceCount = 0;
            for (OptionMapping optionData : optionDataList) {

                if(!optionData.getName().startsWith("answer")) continue;

                String answer = optionData.getAsString();
                if (answer.startsWith(" ")) answer = answer.replaceFirst(" ", "");

                String[] partSplit = answer.split(" ");

                Pattern pattern = Pattern.compile("^<a?:([a-zA-Z0-9_]+):([0-9]+)>$");
                Matcher matcher = pattern.matcher(partSplit[0]);

                if(matcher.find()) {
                    reactionsListAdd.add(matcher.group(1) + ":" + matcher.group(2));
                    reactionsList.add(partSplit[0]);
                    choiceList.add(answer.replace(partSplit[0], ""));
                } else if(EmojiManager.isEmoji(partSplit[0].replace("️", ""))) { // Replaces an empty character, which prevents the isEmoji detection
                    reactionsListAdd.add(partSplit[0]);
                    reactionsList.add(partSplit[0]);
                    choiceList.add(answer.replace(partSplit[0], ""));
                } else {
                    String charReaction = String.copyValueOf(Character.toChars("\uD83C\uDDE6".codePointAt(0) + choiceCount));
                    reactionsListAdd.add(charReaction);
                    reactionsList.add(charReaction);
                    choiceList.add(answer);
                }
                choiceCount++;
            }

            Message message = null;
            try {
                message = hook.sendMessageEmbeds(
                        new PollManager().getPollEmbed(
                                pollId,
                                pollType,
                                endTime,
                                false,
                                null,
                                allowmultiplechoices,
                                question,
                                reactionsList,
                                choiceList
                        )
                ).complete();
            }catch (Exception e) {
                Sentry.captureException(e);
            }

            if(message == null) return;

            new PollManager().createPoll(
                    pollId,
                    guild.getId(),
                    event.getChannel().getId(),
                    message.getId(),
                    user.getId(),
                    question,
                    reactionsList,
                    choiceList,
                    pollType,
                    allowmultiplechoices,
                    endTime
            );

            for (String reaction : reactionsListAdd) {
                try {

                    message.addReaction(reaction).complete();

                }catch (ErrorResponseException e) {
                    if(e.getErrorResponse() == ErrorResponse.UNKNOWN_EMOJI) {

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.ORANGE);
                        eb.setTitle("Unknown Emoji", Constants.WEBSITE_URL);
                        eb.addField(
                                "You have specified an unknown emoji!",
                                "Please make sure it is an **official Discord** emoji or an emoji **from this server**. EasyPoll can only use emojis from servers where EasyPoll is too.\n" +
                                        "\n" +
                                        "*PS: You will find your sent command of this poll to copy as a message to which I replied (click on the blue `" + (isTimePoll ? "/timepoll" : "/poll") +  "` command above this message)*",
                                true);

                        message.clearReactions().queue(null, Sentry::captureException);
                        message.editMessageEmbeds(eb.build()).queue(null, Sentry::captureException);

                        break;

                    }else if(e.getErrorResponse() == ErrorResponse.MISSING_PERMISSIONS) {

                        EmbedBuilder eb = new EmbedBuilder();

                        eb.setColor(Color.RED);
                        eb.setTitle("I do not have permissions to add reactions to the messages in the specified channel.");
                        eb.addField("Please make sure that I have the following permissions",
                                "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
                                true);

                        hook.sendMessageEmbeds(
                                eb.build()
                        ).queue(null, Sentry::captureException);

                    }else{
                        Sentry.captureException(e);
                    }
                }catch (InsufficientPermissionException e) {

                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(Color.RED);
                    eb.setTitle("I do not have permissions to add reactions to the messages in the specified channel.");
                    eb.addField("Please make sure that I have the following permissions",
                            "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
                            true);

                    hook.sendMessageEmbeds(
                            eb.build()
                    ).queue(null, Sentry::captureException);

                    break;
                }
            }
        }
    }
}
