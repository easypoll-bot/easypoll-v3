package de.fbrettnich.easypoll.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;

public class Permissions {

    private final Member member;

    public Permissions(Member member) {
        this.member = member;
    }

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
