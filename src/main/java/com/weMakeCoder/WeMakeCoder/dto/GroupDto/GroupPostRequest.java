package com.weMakeCoder.WeMakeCoder.dto.GroupDto;

import java.util.UUID;

public record GroupPostRequest(
        String groupName,
        String displayName,
        String password,
        UUID userId
) {
}
