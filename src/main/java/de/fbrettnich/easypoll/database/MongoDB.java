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

package de.fbrettnich.easypoll.database;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDB {

    private final MongoClient mongoClient;
    private final DB db;

    public MongoDB(String clientUri, String database) {

        MongoClientURI uri = new MongoClientURI(clientUri);

        this.mongoClient = new MongoClient(uri);
        this.db = mongoClient.getDB(database);

    }

    /**
     *
     * @return MongoClient
     */
    public MongoClient getClient() {
        return mongoClient;
    }

    /**
     * Get Mongo Collection from Database
     *
     * @param collection name
     * @return DBCollection
     */
    public DBCollection getCollection(String collection) {
        return db.getCollection(collection);
    }
}
