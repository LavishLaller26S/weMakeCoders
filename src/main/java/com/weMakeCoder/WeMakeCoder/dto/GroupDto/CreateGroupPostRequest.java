package com.weMakeCoder.WeMakeCoder.dto.GroupDto;

public record CreateGroupPostRequest(
        String groupName,
        String displayName,
        String password
) {
}
