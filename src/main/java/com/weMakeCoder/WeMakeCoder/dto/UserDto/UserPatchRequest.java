package com.weMakeCoder.WeMakeCoder.dto.UserDto;

import com.weMakeCoder.WeMakeCoder.enums.ChangeRequest;
import com.weMakeCoder.WeMakeCoder.enums.Gender;

import java.time.LocalDate;

public record UserPatchRequest (
        String userName,
        String displayName,
        String mail,
        String password,
        Gender gender,
        LocalDate dateOfBirth
){
}
