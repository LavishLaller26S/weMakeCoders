package com.weMakeCoder.WeMakeCoder.enums;

import lombok.Getter;

@Getter
public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    DONOTWANTTOSPECIFY("Do not Want to Specify");

    private final String label;

    Gender(String label){
        this.label=label;
    }

}
