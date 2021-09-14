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

package de.fbrettnich.easypoll.language;

import de.fbrettnich.easypoll.core.Constants;
import io.sentry.Sentry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class TranslationManager {

    private final ArrayList<String> languages = new ArrayList<>();
    private final HashMap<String, String> translations = new HashMap<>();

    public TranslationManager() { }

    /**
     * Load multiple translations
     *
     * @param countryCodes country code list
     */
    public void loadTranslations(String... countryCodes) {
        for (String countryCode : countryCodes) {
            loadTranslation(countryCode);
        }
    }

    /**
     * Load a translation
     *
     * @param countryCode country code
     */
    public void loadTranslation(String countryCode) {
        languages.add(countryCode);

        JSONParser parser = new JSONParser();
        InputStream inputStream = TranslationManager.class.getResourceAsStream("/translations/" + countryCode + ".json");

        try {
            Object obj = parser.parse(inputStreamToString(inputStream));
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.forEach((key, value) -> addTranslation(countryCode, "", key, value));
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }

    /**
     * Read an InputStream and convert it to a String
     *
     * @param stream {@link InputStream}
     * @return {@link InputStream} as {@link String}
     * @throws IOException
     */
    private String inputStreamToString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        String read;
        while((read = br.readLine()) != null) {
            sb.append(read);
        }
        br.close();

        return sb.toString();
    }

    /**
     * Load a translation path
     *
     * @param lang country code
     * @param path translation path
     * @param key translation path key
     * @param obj translation object
     */
    private void addTranslation(String lang, String path, Object key, Object obj) {
        String pathKey = path + "." + key;
        if(obj instanceof JSONObject) {
            ((JSONObject) obj).forEach((o, o2) -> addTranslation(lang, pathKey, o, o2));
        }else{
            String k = lang + ":" + pathKey;
            k = k.replaceFirst("\\.", "");
            translations.put(k, (String) obj);
        }
    }

    /**
     * Get a translation
     *
     * @param lang country code
     * @param key translation path key
     * @return translation
     */
    public String getTranslation(String lang, String key) {
        if (translations.containsKey(lang + ":" + key)) {
            return translations.get(lang + ":" + key);
        }else {
            return translations.getOrDefault(Constants.DEFAULT_LANGUAGE + ":" + key, "[Empty translation " + key + "]");
        }
    }

    /**
     * Get loaded languages code list
     *
     * @return languages
     */
    public ArrayList<String> getLanguages() {
        return languages;
    }
}