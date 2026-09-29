package com.weMakeCoder.WeMakeCoder.dto.GroupDto;

import java.util.UUID;

public record JoinGroupPostRequest(
        UUID groupId,
        String groupCode,
        String password
) {
}
