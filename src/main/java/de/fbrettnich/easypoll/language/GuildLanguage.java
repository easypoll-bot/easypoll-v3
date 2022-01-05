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

package de.fbrettnich.easypoll.language;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import de.fbrettnich.easypoll.core.Constants;
import de.fbrettnich.easypoll.core.Main;
import net.dv8tion.jda.api.entities.Guild;

public class GuildLanguage {

    private final String guildId;
    private String language;

    public GuildLanguage(String guildId) {
        this.guildId = guildId;
        this.language = getLanguage();
    }

    public GuildLanguage(Guild guild) {
        this.guildId = guild.getId();
        this.language = getLanguage();
    }

    /**
     * Set guild language code
     *
     * @param lang language code
     */
    public void setLanguage(String lang) {

        this.language = lang;
        Main.guildLanguageCache.put(this.guildId, lang);

        DBCollection collection = Main.getMongoDB().getCollection("guilds");
        DBObject searchQuery = new BasicDBObject("guildId", this.guildId);
        DBObject document = collection.findOne(searchQuery);

        if(document == null) {
            document = new BasicDBObject();

            document.put("guildId", this.guildId);
            document.put("language", lang);

            collection.insert(document);
        }else{
            document.put("language", lang);

            collection.update(searchQuery, document);
        }
    }

    /**
     * Get guild language code
     *
     * @return language code
     */
    public String getLanguage() {

        if(Main.guildLanguageCache.containsKey(guildId)) {
            return Main.guildLanguageCache.get(this.guildId);
        }

        DBCollection collection = Main.getMongoDB().getCollection("guilds");
        DBObject searchQuery = new BasicDBObject("guildId", this.guildId);
        DBObject document = collection.findOne(searchQuery);

        String lang = Constants.DEFAULT_LANGUAGE;
        if (document != null) {
            lang = (String) document.get("language");
        }

        Main.guildLanguageCache.put(this.guildId, lang);
        return lang;
    }

    /**
     * Get translation in guild language
     *
     * @param key translation key
     * @return translation
     */
    public String getTl(String key, String... placeholder) {
        String translation = Main.getTranslationManager().getTranslation(this.language, key);

        for (String s : placeholder) {
            translation = translation.replaceFirst("%s", s);
        }

        return translation;
    }
}
