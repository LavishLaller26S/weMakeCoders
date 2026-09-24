package com.weMakeCoder.WeMakeCoder.dto.GroupDto;

import java.time.Instant;
import java.util.UUID;

public record GroupResponse(
        UUID groupId,
        String groupName,
        String displayName,
        UUID adminId,
        Long membersCount,
        Instant createdAt,
        Instant updateAt
) {
}
