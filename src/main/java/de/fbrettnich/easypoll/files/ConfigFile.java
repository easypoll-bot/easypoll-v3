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

package de.fbrettnich.easypoll.files;

import io.sentry.Sentry;

import java.io.*;
import java.util.Properties;

public class ConfigFile {

    private final Properties prop = new Properties();

    public ConfigFile() {
        createFile();
        loadFile();
    }

    /**
     * Create config file if not exists
     */
    private void createFile() {
        if(!fileExists()) {
            try (OutputStream output = new FileOutputStream("config.properties")) {

                Properties prop = new Properties();

                prop.setProperty("devmode", "true");
                prop.setProperty("bot.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                prop.setProperty("bot.token_dev", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

                prop.setProperty("sentry.url", "https://XXXXX.ingest.sentry.io/12345");

                prop.setProperty("mysql.host", "127.0.0.1");
                prop.setProperty("mysql.port", "3306");
                prop.setProperty("mysql.database", "easypoll");
                prop.setProperty("mysql.username", "easypoll");
                prop.setProperty("mysql.password", "topsecret");

                prop.setProperty("mongodb.clienturi", "mongodb+srv://XXX:XXX@XXX.mongodb.net/test?retryWrites=true&w=majority1");
                prop.setProperty("mongodb.clienturi_dev", "mongodb+srv://XXX:XXX@XXX.mongodb.net/test?retryWrites=true&w=majority1");
                prop.setProperty("mongodb.database", "DiscordBot");

                prop.setProperty("botlist.topgg.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                prop.setProperty("botlist.dbots.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                prop.setProperty("botlist.discordbotlist.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                prop.setProperty("botlist.discordbotsgg.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                prop.setProperty("botlist.botsondiscordxyz.token", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

                prop.store(output, null);

            } catch (IOException e) {
                Sentry.captureException(e);
            }
        }
    }

    /**
     * Load config file
     */
    private void loadFile() {
        try (InputStream input = new FileInputStream("config.properties")) {

            prop.load(input);

        } catch (IOException e) {
            Sentry.captureException(e);
        }
    }

    /**
     *
     * @return true if config file exists
     */
    private boolean fileExists() {
        try {

            InputStream input = new FileInputStream("config.properties");
            return true;

        } catch (FileNotFoundException ignored) { }

        return false;
    }

    /**
     *
     * @param key property key
     * @return property value
     */
    public String getString(String key) {
        return prop.getProperty(key);
    }
}
