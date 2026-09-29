package com.weMakeCoder.WeMakeCoder.controller;

import com.weMakeCoder.WeMakeCoder.dto.GroupDto.*;
import com.weMakeCoder.WeMakeCoder.entity.Group;
import com.weMakeCoder.WeMakeCoder.service.GroupService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("wmd/api/v1/user/{userId}")
public class GroupController {
    private final GroupService groupService;


    @GetMapping("/group")
    public ResponseEntity<List<GroupResponse>> getGroupForHome(@PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.groupService.getGroupEntry(userId));
    }

    @PostMapping("/group/create")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupPostRequest request,
                                                     @PathVariable UUID userId){

        return ResponseEntity.status(HttpStatus.CREATED).body(this.groupService.createGroup(request,userId));

    }
    @PostMapping("/group/join")
    public ResponseEntity<JoinGroupResponse> createGroup(@RequestBody JoinGroupPostRequest request,
                                                         @PathVariable UUID userId){

        return ResponseEntity.status(HttpStatus.OK).body(this.groupService.joinGroup(request,userId));

    }
    @GetMapping("/group/checkName")
    public ResponseEntity<Void> getGroupForHome(@RequestParam String groupName){
        this.groupService.nameExists(groupName);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }







}
