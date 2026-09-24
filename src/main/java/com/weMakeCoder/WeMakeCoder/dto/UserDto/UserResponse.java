package com.weMakeCoder.WeMakeCoder.dto.UserDto;

import com.weMakeCoder.WeMakeCoder.enums.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String userName,
        String displayName,
        LocalDate dateOfBirth,
        Gender gender,
        String mail,
        Instant createdAt,
        Instant updatedAt,
        boolean active
) {
}
