package com.weMakeCoder.WeMakeCoder.enums;

import lombok.Getter;

@Getter
public enum GroupRole {

    ADMIN("Admin"),
    MEMBER("Member");

    private final String label;

    GroupRole(String label) {
        this.label = label;
    }

}