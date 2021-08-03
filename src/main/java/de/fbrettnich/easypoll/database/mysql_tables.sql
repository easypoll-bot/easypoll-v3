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