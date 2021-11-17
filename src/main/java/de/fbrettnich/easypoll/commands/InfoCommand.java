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

package de.fbrettnich.easypoll.commands;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import de.fbrettnich.easypoll.language.GuildLanguage;
import de.fbrettnich.easypoll.utils.FormatUtil;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nonnull;
import java.awt.*;

public class InfoCommand {

    public InfoCommand(@Nonnull SlashCommandEvent event, GuildLanguage gl) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(gl.getTl("commands.info.title"), Constants.WEBSITE_URL);
        eb.setColor(Color.decode("#01FF70"));
        eb.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());

        eb.addField(gl.getTl("commands.info.fields.creator"), Constants.BOT_OWNER_MENTION, false);
        eb.addField(gl.getTl("commands.info.fields.repository"), "[github.com/fbrettnich/easypoll-bot](" + Constants.GITHUB_URL + ")", false);
        eb.addField(gl.getTl("commands.info.fields.version"), Constants.VERSION, false);

        eb.addField(gl.getTl("commands.info.fields.library"), "[JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)", false);

        eb.addField(gl.getTl("commands.info.fields.servers"), FormatUtil.decimalFormat(Main.getShardManager().getGuilds().size()), false);
        eb.addField(gl.getTl("commands.info.fields.users"), FormatUtil.decimalFormat(Main.getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum()), false);

        eb.addField(gl.getTl("commands.info.fields.shard"), (event.getGuild().getJDA().getShardInfo().getShardId() + 1) + "/" + Main.getShardManager().getShardsTotal(), false);
        eb.addField(gl.getTl("commands.info.fields.uptime"), getUptime(), false);

        event.replyEmbeds(
                        eb.build()
                )
                .addActionRow(
                        Button.link(Constants.WEBSITE_URL, gl.getTl("buttons.website")),
                        Button.link(Constants.DOCUMENTATION_URL, gl.getTl("buttons.documentation")),
                        Button.link(Constants.GITHUB_URL, gl.getTl("buttons.repository"))
                )
                .queue(null, Sentry::captureException);

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