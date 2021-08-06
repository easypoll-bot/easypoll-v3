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
     * Check if the config file exists
     *
     * @return true if config file exists, otherwise false
     */
    private boolean fileExists() {
        try {

            InputStream input = new FileInputStream("config.properties");
            return true;

        } catch (FileNotFoundException ignored) { }

        return false;
    }

    /**
     * Get a string based on the key
     *
     * @param key property key
     * @return property value
     */
    public String getString(String key) {
        return prop.getProperty(key);
    }
}
