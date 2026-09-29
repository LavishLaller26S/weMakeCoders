package com.weMakeCoder.WeMakeCoder.dto.GroupDto;


import java.util.UUID;

import com.weMakeCoder.WeMakeCoder.enums.GroupRole;


public record JoinGroupResponse(
        UUID userId,
        UUID groupId,
        GroupRole role
) {
}
