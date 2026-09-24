package com.weMakeCoder.WeMakeCoder.service;

import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupResponse;
import com.weMakeCoder.WeMakeCoder.entity.Group;
import com.weMakeCoder.WeMakeCoder.entity.Members;
import com.weMakeCoder.WeMakeCoder.repository.GroupRepository;
import com.weMakeCoder.WeMakeCoder.repository.MembersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MembersRepository membersRepository;
    private final UserService userService;

    public List<GroupResponse> getGroupEntry(UUID userId) {
        userService.checkUserExists(userId);
        List<Members> members=membersRepository.findAllByUserId(userId);

        List<UUID> groupIds=members.stream().map(mem-> mem.getGroup().getId()).toList();

        return groupRepository.findAllById(groupIds).stream()
                .map(group -> new GroupResponse(
                        group.getId(),
                        group.getGroupName(),
                        group.getDisplayName(),
                        group.getAdmin().getId(),
                        group.getMembersCount(),
                        group.getCreatedAt(),
                        group.getUpdatedAt()))
                .toList();
    }
}
