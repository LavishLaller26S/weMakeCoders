package com.weMakeCoder.WeMakeCoder.controller;

import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserResponse;
import com.weMakeCoder.WeMakeCoder.dto.UserDto.UserPatchRequest;
import com.weMakeCoder.WeMakeCoder.dto.UserDto.UserResponse;
import com.weMakeCoder.WeMakeCoder.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/wmc/api/v1/users/{userId}")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getUserProfileForHome(@PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUserEntry(userId));
    }
//    @PatchMapping("/account")
//    public ResponseEntity<UserResponse> updateSetting(@RequestBody UserPatchRequest userPatchRequest){
//        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateSetting());
//    }

    @GetMapping("/sheets/dsa/{dsaEntryId}")
    public ResponseEntity<List<DsaSheetUserResponse>> getDsaEntryForHome(@PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getDsaEntries(userId));
    }

    @PostMapping("/sheet/dsa/add")
    public ResponseEntity<DsaSheetUserResponse> addDsaEntryForUser(@RequestBody DsaSheetUserPostRequest request,
                                                                   @PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.addDsaEntry(request,userId));
    }

    @DeleteMapping("sheet/dsa/delete")
    public ResponseEntity<Void> addDsaEntryForUser(@RequestBody List<Long> ids,
                                                                   @PathVariable UUID userId){
        this.userService.deleteDsaEntries(ids,userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
