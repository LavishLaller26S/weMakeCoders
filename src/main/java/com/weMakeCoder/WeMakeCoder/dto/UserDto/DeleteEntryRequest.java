package com.weMakeCoder.WeMakeCoder.dto.UserDto;

import java.util.UUID;

public record DeleteEntryRequest(
        Long id,
        UUID userId
) {
}
