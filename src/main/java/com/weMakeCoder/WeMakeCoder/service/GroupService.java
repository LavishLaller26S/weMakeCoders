package com.weMakeCoder.WeMakeCoder.service;

import com.weMakeCoder.WeMakeCoder.dto.GroupDto.CreateGroupPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupResponse;
import com.weMakeCoder.WeMakeCoder.entity.Group;
import com.weMakeCoder.WeMakeCoder.entity.Members;
import com.weMakeCoder.WeMakeCoder.entity.User;
import com.weMakeCoder.WeMakeCoder.exception.GroupNameAlreadyExistException;
import com.weMakeCoder.WeMakeCoder.exception.UrlAlreadyExistException;
import com.weMakeCoder.WeMakeCoder.exception.UserNotExistsException;
import com.weMakeCoder.WeMakeCoder.repository.GroupRepository;
import com.weMakeCoder.WeMakeCoder.repository.MembersRepository;
import com.weMakeCoder.WeMakeCoder.util.JoinCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class GroupService {
    private static final int MAX_ATTEMPTS=5;
    private final GroupRepository groupRepository;
    private final MembersRepository membersRepository;
    private final UserService userService;
    private final JoinCodeGenerator joinCodeGenerator;
    private final PasswordEncoder encoder;


    public List<GroupResponse> getGroupEntry(UUID userId) {
        userService.checkUserExists(userId);
        return membersRepository.findGroupResponsesByUserId(userId);
    }

    public GroupResponse createGroup(CreateGroupPostRequest request, UUID userId) {
        User user=userService.checkUserExists(userId);
        nameExists(request.groupName());
        Group group;
        String hashPass=encoder.encode(request.password());
        for(int attempts=1;attempts<=MAX_ATTEMPTS;attempts++){
            String groupCode=joinCodeGenerator.generate();
            group=Group.builder().
                    groupName(request.groupName()).
                    displayName(request.displayName()).
                    admin(user).
                    passwordHash(hashPass).
                    build();
            group.setGroupCode(groupCode);

            try{
                Group savedGroup= groupRepository.saveAndFlush(group);
                return new GroupResponse(savedGroup.getId(),
                        savedGroup.getGroupName(),
                        savedGroup.getDisplayName(),
                        savedGroup.getAdmin().getId(),
                        savedGroup.getMembersCount(),
                        savedGroup.getCreatedAt(),
                        savedGroup.getUpdatedAt()
                );
            }catch (DataIntegrityViolationException ignored){

            }
        }
        throw new IllegalStateException("unable to generate group_code");
    }
    public void nameExists(String groupName){
        if(groupRepository.existsByGroupName(groupName.trim().toLowerCase(Locale.ROOT))){
            throw new GroupNameAlreadyExistException("group name already exist");
        }
    }

}
