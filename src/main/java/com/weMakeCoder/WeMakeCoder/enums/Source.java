package com.weMakeCoder.WeMakeCoder.enums;

import lombok.Getter;

@Getter
public enum Source {
    LEETCODE("LeetCode"),
    CODEFORCES("CodeForces"),
    CODECHEF("CodeChef"),
    OTHER("Other");

    private final String label;

    Source(String label){
        this.label=label;
    }
}
