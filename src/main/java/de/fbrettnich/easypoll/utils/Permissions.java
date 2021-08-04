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
