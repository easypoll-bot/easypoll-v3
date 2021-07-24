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

package de.fbrettnich.easypoll.database;

import io.sentry.Sentry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private Connection con = null;

    private final String HOST;
    private final String PORT;
    private final String DATABASE;
    private final String USER;
    private final String PASSWORD;

    /**
     * Establish a MySQL connection
     *
     * @param host mysql host
     * @param port mysql port (default = 3306)
     * @param database mysql database name
     * @param user mysql user name
     * @param password mysql user password
     */
    public MySQL(String host, String port, String database, String user, String password) {
        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;

        connect();
    }

    /**
     * Connect to MySQL Server
     */
    public void connect() {
        if(!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useUnicode=true", USER, PASSWORD);
                System.out.println("[MySQL] The connection to the MySQL database was successfully established.");
            } catch (SQLException ex) {
                Sentry.captureException(ex);
                System.err.println("[MySQL] Error while connecting to the database!");
            }
        }
    }

    /**
     * Disconnect from MySQL Server
     */
    public void disconnect() {
        try {
            if(isConnected()) {
                con.close();
                con = null;
                System.out.println("[MySQL] The connection to the MySQL database was successfully closed.");
            }
        } catch (SQLException ex) {
            Sentry.captureException(ex);
            System.err.println("[MySQL] Error when disconnecting from the database!");
        }
    }

    /**
     * Get MySQL Connection
     *
     * @return mysql connection
     */
    public Connection getConnection() {
        if(!isConnected()) connect();

        return con;
    }


    /**
     * Check MySQL is connected
     *
     * @return mysql is connected
     */
    public boolean isConnected(){
        return con != null;
    }

}
