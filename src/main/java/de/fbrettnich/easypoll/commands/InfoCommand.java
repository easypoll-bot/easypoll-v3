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

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.utils.FormatUtil;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class InfoCommand {

    public InfoCommand(@Nonnull SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("EasyPoll Information", Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#01FF70"));

        eb.addField("Creator", Constants.BOT_OWNER_MENTION, true);
        eb.addField("Library", "JDA (Java Discord API)", true);
        eb.addField("Version", Constants.VERSION, true);

        eb.addField("Servers", FormatUtil.decimalFormat(Main.getShardManager().getGuilds().size()), true);
        eb.addField("Users", FormatUtil.decimalFormat(Main.getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum()), true);
        eb.addBlankField(true);

        eb.addField("Shard", (event.getGuild().getJDA().getShardInfo().getShardId() + 1) + "/" + Main.getShardManager().getShardsTotal(), true);
        eb.addField("Uptime", getUptime(), true);
        eb.addBlankField(true);

        event.replyEmbeds(
                eb.build()
        ).queue(null, Sentry::captureException);

    }

    /**
     * Calculate the uptime of the bot and combine it as a string
     *
     * @return the bot uptime as string
     */
    private String getUptime() {
        int sec = (int)((System.currentTimeMillis() - Constants.STARTUP) / 1000);

        int day = sec / 60 / 60 / 24 % 365;
        int hour = sec / 60 /60 % 24;
        int minute = sec / 60 % 60;
        int second = sec % 60;

        if(sec < 60) second = sec;

        if(day == 0) {
            return hour + "h, " + minute + "m, " + second + "s";
        }else{
            return day + "d, " + hour + "h, " + minute + "m, " + second + "s";
        }
    }
}