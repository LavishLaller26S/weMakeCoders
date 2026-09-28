package com.weMakeCoder.WeMakeCoder.util;

import lombok.AllArgsConstructor;

import java.security.SecureRandom;


public class JoinCodeGenerator {

    private static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 9;

    private final SecureRandom secureRandom=new SecureRandom();

    public String generate(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<CODE_LENGTH;i++){
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
