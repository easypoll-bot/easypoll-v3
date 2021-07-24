/*
 * Copyright (c) 2021 Felix Brettnich
 *
 * This file is part of EasyPoll (https://github.com/fbrettnich/EasyPoll-Bot)
 *
 * All contents of this source code are protected by copyright.
 * The copyright lies, if not expressly differently marked,
 * by Felix Brettnich. All rights reserved.
 *
 * Any kind of duplication, distribution, rental, lending,
 * public accessibility or other use requires the explicit,
 * written consent from Felix Brettnich
 */

package de.fbrettnich.easypoll.core;

import de.fbrettnich.easypoll.database.MongoDB;
import de.fbrettnich.easypoll.database.MySQL;
import de.fbrettnich.easypoll.files.ConfigFile;
import de.fbrettnich.easypoll.listener.*;
import de.fbrettnich.easypoll.timertasks.BotListStats;
import de.fbrettnich.easypoll.timertasks.UpdateTimedPolls;
import io.sentry.Sentry;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static ConfigFile config;
    private static MongoDB mongodb;
    private static MySQL mysql;
    private static ShardManager shardManager;

    public static void main(String[] args) throws LoginException {

        config = new ConfigFile();
        Constants.DEVMODE = Boolean.parseBoolean(getConfig().getString("devmode"));

        Sentry.init(options -> {
            options.setDsn(getConfig().getString("sentry.url"));
            options.setEnvironment(Constants.DEVMODE ? "development" : "production");
        });

        mongodb = new MongoDB(Constants.DEVMODE ? getConfig().getString("mongodb.clienturi_dev") : getConfig().getString("mongodb.clienturi"), getConfig().getString("mongodb.database"));
        mysql = new MySQL(getConfig().getString("mysql.host"), getConfig().getString("mysql.port"), getConfig().getString("mysql.database"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));

        DefaultShardManagerBuilder defaultShardManagerBuilder = DefaultShardManagerBuilder.createDefault(Constants.DEVMODE ? getConfig().getString("bot.token_dev") : getConfig().getString("bot.token"))

                .setAutoReconnect(true)
                .setShardsTotal(-1)
                .setChunkingFilter(ChunkingFilter.NONE)
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .disableCache(CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.ROLE_TAGS)
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS)

                .addEventListeners(new GuildJoinListener())
                .addEventListeners(new GuildLeaveListener())
                .addEventListeners(new MessageReactionAddListener())
                .addEventListeners(new MessageReceivedListener())
                .addEventListeners(new ReadyListener())
                .addEventListeners(new SlashCommandListener())

                .setStatus(OnlineStatus.ONLINE);

        shardManager = defaultShardManagerBuilder.build();


        new Timer().schedule(new UpdateTimedPolls(), 5 * 60 * 1000, 1000);
        if(!Constants.DEVMODE) {
            new Timer().schedule(new BotListStats(), 5 * 60 * 1000, 5 * 60 * 1000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    int globalUnavailableGuilds = 0;

                    try {
                        for (JDA jda : Main.getShardManager().getShards()) {
                            try {
                                int shardUnavailableGuilds = jda.getUnavailableGuilds().size();
                                globalUnavailableGuilds += shardUnavailableGuilds;

                                PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_shards` (`id`, `shard_id`, `guilds`, `unavailable_guilds`, `users`) VALUES (NULL, ?, ?, ?, ?)");
                                ps.setInt(1, jda.getShardInfo().getShardId());
                                ps.setInt(2, jda.getGuilds().size());
                                ps.setInt(3, shardUnavailableGuilds);
                                ps.setInt(4, jda.getUsers().size());

                                ps.executeUpdate();
                                ps.close();
                            } catch (SQLException ex) {
                                Sentry.captureException(ex);
                            }
                        }
                    } catch (Exception ex) {
                        Sentry.captureException(ex);
                    }

                    try {
                        PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_global` (`id`, `guilds`, `unavailable_guilds`, `all_users`, `different_users`, `shards`) VALUES (NULL, ?, ?, ?, ?, ?)");
                        ps.setInt(1, Main.getShardManager().getGuilds().size());
                        ps.setInt(2, globalUnavailableGuilds);
                        ps.setInt(3, Main.getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum());
                        ps.setInt(4, Main.getShardManager().getUsers().size());
                        ps.setInt(5, Main.getShardManager().getShardsTotal());

                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException ex) {
                        Sentry.captureException(ex);
                    }

                    try {
                        int monthlypoints = Unirest.get("https://top.gg/api/bots/" + Constants.BOT_ID)
                                .header("Content-Type", "application/json")
                                .header("Authorization", Main.getConfig().getString("botlist.topgg.token"))
                                .asJson()
                                .getBody()
                                .getObject()
                                .getInt("monthlyPoints");

                        try {
                            PreparedStatement ps = Main.getMysql().getConnection().prepareStatement("INSERT INTO `statistics_votes` (`id`, `votes_topgg`) VALUES (NULL, ?)");
                            ps.setInt(1, monthlypoints);

                            ps.executeUpdate();
                            ps.close();
                        } catch (SQLException ex) {
                            Sentry.captureException(ex);
                        }
                    } catch (Exception ex) {
                        Sentry.captureException(ex);
                    }
                }
            }, 5 * 60 * 1000L, 5 * 60 * 1000L);
        }

        startConsolenInput();
    }

    /**
     * Listening to console inputs
     */
    public static void startConsolenInput() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String[] input = br.readLine().split(" ");

                switch (input[0].toLowerCase()) {
                    case "?":
                    case "help":
                        System.out.println("" +
                                "[HELP] Commands:\n" +
                                "registerslashcommands\n" +
                                "exit"
                        );
                        break;

                    case "stop":
                    case "end":
                    case "quit":
                    case "exit":
                        System.exit(0);
                        break;

                    case "registerslashcommands":
                        registerSlashCommands();
                        System.out.println("[CONSOLE] SlashCommands have been registered.");
                        break;

                    default:
                        System.out.println("[CONSOLE] Unknown Command! Type 'help' for help.");
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Sentry.captureException(ex);
                }
            }
        }
    }

    /**
     * Registering the Discord SlashCommands
     */
    public static void registerSlashCommands() {

        JDA jda = shardManager.getShardById(0);
        if(jda == null) return;

        jda.updateCommands()
                .addCommands(new CommandData("easypoll", "Learn more about EasyPoll and get help how to use the bot"))
                .addCommands(new CommandData("help", "Show the EasyPoll Bot Help"))
                .addCommands(new CommandData("vote", "Vote for the EasyPoll Bot"))
                .addCommands(new CommandData("invite", "Invite EasyPoll to your own Discord Server"))
                .addCommands(new CommandData("info", "Show some information about EasyPoll"))
                .addCommands(new CommandData("ping", "See the Ping of the Bot to the Discord Gateway"))
                .addCommands(
                        new CommandData("closepoll", "Close a poll so that no more votes are counted")
                                .addOption(OptionType.STRING, "pollid", "Poll ID", true)
                )
                .addCommands(
                        new CommandData("poll", "Create a normal poll")
                                .addOption(OptionType.STRING, "question", "What is the question?", true)
                                .addOption(OptionType.BOOLEAN, "allowmultiplechoices", "Are multiple choices allowed?")
                                .addOption(OptionType.STRING, "answer1", "Answer 1")
                                .addOption(OptionType.STRING, "answer2", "Answer 2")
                                .addOption(OptionType.STRING, "answer3", "Answer 3")
                                .addOption(OptionType.STRING, "answer4", "Answer 4")
                                .addOption(OptionType.STRING, "answer5", "Answer 5")
                                .addOption(OptionType.STRING, "answer6", "Answer 6")
                                .addOption(OptionType.STRING, "answer7", "Answer 7")
                                .addOption(OptionType.STRING, "answer8", "Answer 8")
                                .addOption(OptionType.STRING, "answer9", "Answer 9")
                                .addOption(OptionType.STRING, "answer10", "Answer 10")
                                .addOption(OptionType.STRING, "answer11", "Answer 11")
                                .addOption(OptionType.STRING, "answer12", "Answer 12")
                                .addOption(OptionType.STRING, "answer13", "Answer 13")
                                .addOption(OptionType.STRING, "answer14", "Answer 14")
                                .addOption(OptionType.STRING, "answer15", "Answer 15")
                                .addOption(OptionType.STRING, "answer16", "Answer 16")
                                .addOption(OptionType.STRING, "answer17", "Answer 17")
                                .addOption(OptionType.STRING, "answer18", "Answer 18")
                                .addOption(OptionType.STRING, "answer19", "Answer 19")
                )
                .addCommands(
                        new CommandData("timepoll", "Create a timed poll with end date")
                                .addOption(OptionType.STRING, "question", "What is the question?", true)
                                .addOption(OptionType.STRING, "time", "How long should the poll run? (Minutes (m), Hours (h), Days (d) | Example: 3h | Max: 7d)", true)
                                .addOption(OptionType.BOOLEAN, "allowmultiplechoices", "Are multiple choices allowed?")
                                .addOption(OptionType.STRING, "answer1", "Answer 1")
                                .addOption(OptionType.STRING, "answer2", "Answer 2")
                                .addOption(OptionType.STRING, "answer3", "Answer 3")
                                .addOption(OptionType.STRING, "answer4", "Answer 4")
                                .addOption(OptionType.STRING, "answer5", "Answer 5")
                                .addOption(OptionType.STRING, "answer6", "Answer 6")
                                .addOption(OptionType.STRING, "answer7", "Answer 7")
                                .addOption(OptionType.STRING, "answer8", "Answer 8")
                                .addOption(OptionType.STRING, "answer9", "Answer 9")
                                .addOption(OptionType.STRING, "answer10", "Answer 10")
                                .addOption(OptionType.STRING, "answer11", "Answer 11")
                                .addOption(OptionType.STRING, "answer12", "Answer 12")
                                .addOption(OptionType.STRING, "answer13", "Answer 13")
                                .addOption(OptionType.STRING, "answer14", "Answer 14")
                                .addOption(OptionType.STRING, "answer15", "Answer 15")
                                .addOption(OptionType.STRING, "answer16", "Answer 16")
                                .addOption(OptionType.STRING, "answer17", "Answer 17")
                                .addOption(OptionType.STRING, "answer18", "Answer 18")
                                .addOption(OptionType.STRING, "answer19", "Answer 19")
                )
                .queue(null, Sentry::captureException);
    }

    /**
     *
     * @return config file
     */
    public static ConfigFile getConfig() {
        return config;
    }


    /**
     *
     * @return MongoDB
     */
    public static MongoDB getMongoDB() {
        return mongodb;
    }

    /**
     *
     * @return mysql
     */
    public static MySQL getMysql() {
        return mysql;
    }

    /**
     *
     * @return ShardManager
     */
    public static ShardManager getShardManager() {
        return shardManager;
    }
}
