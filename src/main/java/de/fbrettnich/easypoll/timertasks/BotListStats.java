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
                        .header("Authorization", Main.getConfig().getString("botlist.topgg.token"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }

        // dbots.me
        {
            JSONObject obj = new JSONObject().put("server_count", servers).put("shards_count", shards).put("user_count", users);
            try {
                Unirest.post("https://api.dbots.me/v1/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Main.getConfig().getString("dbots"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }

        // discordbotlist.com
        {
            JSONObject obj = new JSONObject().put("guilds", servers).put("users", users);
            try {
                Unirest.post("https://discordbotlist.com/api/v1/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Main.getConfig().getString("botlist.discordbotlist.token"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }

        // bots.ondiscord.xyz
        {
            JSONObject obj = new JSONObject().put("guildCount", servers);
            try {
                Unirest.post("https://bots.ondiscord.xyz/bot-api/bots/" + Constants.BOT_ID + "/guilds")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Main.getConfig().getString("botlist.botsondiscordxyz.token"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }

        // discord.bots.gg
        {
            JSONObject obj = new JSONObject().put("guildCount", servers).put("shardCount", shards);
            try {
                Unirest.post("https://discord.bots.gg/api/v1/bots/" + Constants.BOT_ID + "/stats")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Main.getConfig().getString("botlist.discordbotsgg.token"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }

        // botlist.space
        /*{
            JSONObject obj = new JSONObject().put("server_count", servers);
            try {
                Unirest.post("https://api.botlist.space/v1/bots/" + Constants.BOT_ID + "/")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Main.getConfig().getString("botlist.botsondiscordxyz.token"))
                        .body(obj.toString())
                        .asJson();
            } catch (UnirestException ex) {
                Sentry.captureException(ex);
            }
        }*/

    }
}
