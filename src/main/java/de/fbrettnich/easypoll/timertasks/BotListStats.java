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

package de.fbrettnich.easypoll.timertasks;

import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import io.sentry.Sentry;
import kong.unirest.CookieSpecs;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Guild;

import java.util.TimerTask;

public class BotListStats extends TimerTask {

    @Override
    public void run() {

        final int servers = Main.getShardManager().getGuilds().size();
        final int users = Main.getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum();
        final int shards = Main.getShardManager().getShards().size();

        Unirest.config().cookieSpec(CookieSpecs.STANDARD);

        // top.gg
        {
            JSONObject obj = new JSONObject().put("server_count", servers).put("shard_count", shards);
            try {
                Unirest.post("https://top.gg/api/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", System.getenv("BOTLIST_TOPGG"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException e) {
                Sentry.captureException(e);
            }
        }

        // discordbotlist.com
        {
            JSONObject obj = new JSONObject().put("guilds", servers).put("users", users);
            try {
                Unirest.post("https://discordbotlist.com/api/v1/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", System.getenv("BOTLIST_DISCORDBOTLISTCOM"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException e) {
                Sentry.captureException(e);
            }
        }

        // bots.ondiscord.xyz
        {
            JSONObject obj = new JSONObject().put("guildCount", servers);
            try {
                Unirest.post("https://bots.ondiscord.xyz/bot-api/bots/" + Constants.BOT_ID + "/guilds")
                        .header("Content-Type", "application/json")
                        .header("Authorization", System.getenv("BOTLIST_BOTSONDISCORDXYZ"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException e) {
                Sentry.captureException(e);
            }
        }

        // discord.bots.gg
        {
            JSONObject obj = new JSONObject().put("guildCount", servers).put("shardCount", shards);
            try {
                Unirest.post("https://discord.bots.gg/api/v1/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", System.getenv("BOTLIST_DISCORDBOTSGG"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException e) {
                Sentry.captureException(e);
            }
        }

        // voidbots.net
        {
            JSONObject obj = new JSONObject().put("server_count", servers).put("shard_count", shards);
            try {
                Unirest.post("https://api.voidbots.net/bot/stats/" + Constants.BOT_ID)
                        .header("Content-Type", "application/json")
                        .header("Authorization", System.getenv("BOTLIST_VOIDBOTSNET"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException e) {
                Sentry.captureException(e);
            }
        }
    }
}
