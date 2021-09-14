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

package de.fbrettnich.easypoll.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;

public class Permissions {

    private final Member member;

    /**
     *
     * @param member Guild member of the user
     */
    public Permissions(Member member) {
        this.member = member;
    }

    /**
     * Check if the member has the PollCreator role
     *
     * @return true if the member has the PollCreator role, otherwise false
     */
    public boolean hasPollCreatorRole() {
        String[] groups = {"PollCreator"};

        for(Role role : this.member.getRoles()) {
            if(Arrays.stream(groups).parallel().allMatch(role.getName()::contains)) {
                return true;
            }
        }
        return false;
    }
}
