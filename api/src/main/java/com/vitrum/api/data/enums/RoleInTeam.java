package com.vitrum.api.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleInTeam {
    MEMBER, CO_LEADER, LEADER;

    public boolean canChangeRole() {
        return this == LEADER;
    }

    public boolean canChangeTo(RoleInTeam targetRole) {
        if (this == LEADER) {
            return true;
        } else if (this == CO_LEADER) {
            return targetRole == MEMBER;
        }
        return false;
    }
}
