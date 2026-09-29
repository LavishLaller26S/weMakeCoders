package com.weMakeCoder.WeMakeCoder.service;

import com.weMakeCoder.WeMakeCoder.dto.GroupDto.CreateGroupPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupResponse;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.JoinGroupPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.GroupDto.JoinGroupResponse;
import com.weMakeCoder.WeMakeCoder.entity.Group;
import com.weMakeCoder.WeMakeCoder.entity.Members;
import com.weMakeCoder.WeMakeCoder.entity.User;
import com.weMakeCoder.WeMakeCoder.enums.GroupRole;
import com.weMakeCoder.WeMakeCoder.exception.GroupNameAlreadyExistException;
import com.weMakeCoder.WeMakeCoder.exception.GroupNotFoundException;
import com.weMakeCoder.WeMakeCoder.exception.InvalidCredentialsException;
import com.weMakeCoder.WeMakeCoder.repository.GroupRepository;
import com.weMakeCoder.WeMakeCoder.repository.MembersRepository;
import com.weMakeCoder.WeMakeCoder.util.JoinCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GroupService {
    private static final int MAX_ATTEMPTS=5;
    private final GroupRepository groupRepository;
    private final MembersRepository membersRepository;
    private final UserService userService;
    private final JoinCodeGenerator joinCodeGenerator;
    private final PasswordEncoder encoder;
    private final GroupInserter inserter;


    public List<GroupResponse> getGroupEntry(UUID userId) {
        userService.checkUserExists(userId);
        return membersRepository.findGroupResponsesByUserId(userId);
    }

   @Transactional
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
                Group savedGroup= inserter.insert(group);

                Members newMem=Members.builder().
                        user(user).
                        group(savedGroup).
                        build();
                newMem.setRole(GroupRole.ADMIN);
                membersRepository.save(newMem);


                return new GroupResponse(savedGroup.getId(),
                        savedGroup.getGroupName(),
                        savedGroup.getDisplayName(),
                        savedGroup.getAdmin().getId(),
                        savedGroup.getMembersCount(),
                        savedGroup.getCreatedAt(),
                        savedGroup.getUpdatedAt()
                );
            }catch (DataIntegrityViolationException ignored){
                nameExists(request.groupName());
            }
        }
        throw new IllegalStateException("unable to generate group_code");
    }


    // if admin deletes the group then things must get stop

    @Transactional
    public JoinGroupResponse joinGroup(JoinGroupPostRequest request, UUID userId) {
        User user=userService.checkUserExists(userId);
        Group group=groupExists(request.groupId());
        if (group.getGroupCode().equals(request.groupCode())){
            if(encoder.matches(request.password(),group.getPasswordHash())){
                group.setMembersCount(group.getMembersCount()+1);

                groupExists(request.groupId());

                groupRepository.save(group);
                Members newMem=Members.builder().
                        user(user).
                        group(group).
                        build();
                newMem.setRole(GroupRole.MEMBER);
                membersRepository.save(newMem);
            }
            else{
                throw new InvalidCredentialsException("provided credentials does not match requirement");
            }
        }
        else{
            throw new InvalidCredentialsException("provided credentials does not match requirement");
        }
        return new JoinGroupResponse(user.getId(),group.getId(),GroupRole.MEMBER);


    }













    public void nameExists(String groupName){
        if(groupRepository.existsByGroupName(groupName.trim().toLowerCase(Locale.ROOT))){
            throw new GroupNameAlreadyExistException("group name already exist");
        }
    }

    public Group groupExists(UUID groupId){
        return groupRepository.findById(groupId).orElseThrow(
                ()->new GroupNotFoundException("group does not exist")
        );
    }
}
