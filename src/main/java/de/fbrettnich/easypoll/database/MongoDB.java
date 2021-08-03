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
     * Get Mongo Collection from Old Database
     *
     * @param collection name
     * @return DBCollection
     */
    public DBCollection getCollection(String collection) {
        return db.getCollection(collection);
    }
}
