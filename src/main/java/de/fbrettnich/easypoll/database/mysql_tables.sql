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

CREATE TABLE IF NOT EXISTS `statistics_global`(
    `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `guilds` INT(11) NOT NULL DEFAULT '0',
    `unavailable_guilds` INT(11) NOT NULL DEFAULT '0',
    `all_users` INT(11) NOT NULL DEFAULT '0',
    `different_users` INT(11) NOT NULL DEFAULT '0',
    `shards` INT(11) NOT NULL DEFAULT '0',
    `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `statistics_commands`(
    `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `command` VARCHAR(64) NOT NULL DEFAULT '0',
    `value` INT(11) NOT NULL DEFAULT '1',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `statistics_events`(
    `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `event` VARCHAR(64) NOT NULL DEFAULT '0',
    `value` INT(11) NOT NULL DEFAULT '1',
);

CREATE TABLE IF NOT EXISTS `statistics_votes`(
    `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `votes` INT(11) NOT NULL DEFAULT '0',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `statistics_shards`(
    `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `shard_id` INT(11) NOT NULL DEFAULT '0',
    `guilds` INT(11) NOT NULL DEFAULT '0',
    `unavailable_guilds` INT(11) NOT NULL DEFAULT '0',
    `users` INT(11) NOT NULL DEFAULT '0',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);