package com.weMakeCoder.WeMakeCoder.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.weMakeCoder.WeMakeCoder.entity.Group;
import com.weMakeCoder.WeMakeCoder.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupInserter {
    private final GroupRepository groupRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Group insert(Group group){
        return groupRepository.saveAndFlush(group);
    }
}
