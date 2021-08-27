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

package de.fbrettnich.easypoll.commands;

import com.vdurmont.emoji.EmojiManager;
import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.language.GuildLanguage;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollCommand {

    public PollCommand(@Nonnull SlashCommandEvent event, GuildLanguage gl, boolean isTimePoll) {

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
            eb.setTitle(gl.getTl("errors.no_permissions.member.title"), Constants.WEBSITE_URL);
            eb.addField(
                    gl.getTl("errors.no_permissions.member.field.title"),
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

            time = time
                    .replace("s", "s/")
                    .replace("m", "m/")
                    .replace("h", "h/")
                    .replace("d", "d/")
                    .replace("w", "w/");

            String[] split = time.split("/");



            AtomicLong timeResult = new AtomicLong();
            AtomicBoolean error = new AtomicBoolean(false);

            Arrays.stream(split).forEach(timecode -> {
                long multiplier = 0L;

                if(timecode.endsWith("s")) {
                    multiplier = 1L;
                }else if(timecode.endsWith("m")) {
                    multiplier = 60L;
                }else if(timecode.endsWith("h")) {
                    multiplier = 60*60L;
                }else if(timecode.endsWith("d")) {
                    multiplier = 24*60*60L;
                }else if(timecode.endsWith("w")) {
                    multiplier = 7*24*60*60L;
                }

                String timeString = timecode.substring(0, timecode.length() - 1);
                if (timeString.matches("[0-9]+")){
                    timeResult.getAndAdd(Long.parseLong(timeString) * multiplier);
                }else {
                    error.set(true);
                }

            });

            if (error.get()){
                
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(Color.ORANGE);
                eb.setTitle(gl.getTl("commands.poll.invalid_time.title"), Constants.WEBSITE_URL);
                eb.addField(
                        gl.getTl("commands.poll.invalid_time.field.title"),
                        gl.getTl("commands.poll.invalid_time.field.description"),
                        true);

                hook.sendMessageEmbeds(
                        eb.build()
                ).queue(null, Sentry::captureException);

                return;
            }

            long totalTime = timeResult.get();
            if(totalTime > 7*24*60*60*1000L) totalTime = 7*24*60*60*1000L;
            endTime = System.currentTimeMillis() + totalTime + 1000L;
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
                                null,
                                gl
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
                eb.setTitle(gl.getTl("errors.no_permissions.bot.title"));
                eb.addField(gl.getTl("errors.no_permissions.bot.field.title"),
                        "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
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
                                choiceList,
                                gl
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
                        eb.setTitle(gl.getTl("commands.poll.unknown_emoji.title"), Constants.WEBSITE_URL);
                        eb.addField(
                                gl.getTl("commands.poll.unknown_emoji.field.title"),
                                gl.getTl("commands.poll.unknown_emoji.field.description", (isTimePoll ? "/timepoll" : "/poll")),
                                true);

                        message.clearReactions().queue(null, Sentry::captureException);
                        message.editMessageEmbeds(eb.build()).queue(null, Sentry::captureException);

                        break;

                    }else if(e.getErrorResponse() == ErrorResponse.MISSING_PERMISSIONS) {

                        EmbedBuilder eb = new EmbedBuilder();

                        eb.setColor(Color.RED);
                        eb.setTitle(gl.getTl("errors.no_permissions.bot.title"));
                        eb.addField(gl.getTl("errors.no_permissions.bot.field.title"),
                                "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
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
                    eb.setTitle(gl.getTl("errors.no_permissions.bot.title"));
                    eb.addField(gl.getTl("errors.no_permissions.bot.field.title"),
                            "MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EXT_EMOJI",
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
