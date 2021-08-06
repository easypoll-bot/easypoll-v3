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
                con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useUnicode=true&serverTimezone=UTC", USER, PASSWORD);
                System.out.println("[MySQL] The connection to the MySQL database was successfully established.");
            } catch (SQLException e) {
                Sentry.captureException(e);
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
        } catch (SQLException e) {
            Sentry.captureException(e);
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
