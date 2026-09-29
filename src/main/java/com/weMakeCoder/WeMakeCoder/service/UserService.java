package com.weMakeCoder.WeMakeCoder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserPostRequest;
import lombok.AllArgsConstructor;
import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserResponse;
import com.weMakeCoder.WeMakeCoder.dto.UserDto.UserResponse;
import com.weMakeCoder.WeMakeCoder.entity.DsaSheet;
import com.weMakeCoder.WeMakeCoder.entity.GroupDsaMapper;
import com.weMakeCoder.WeMakeCoder.entity.User;
import com.weMakeCoder.WeMakeCoder.exception.UserNotFoundException;
import com.weMakeCoder.WeMakeCoder.repository.DsaSheetRepository;
import com.weMakeCoder.WeMakeCoder.repository.GroupDsaMapperRepository;
import com.weMakeCoder.WeMakeCoder.repository.UserRepository;

import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DsaSheetRepository dsaSheetRepository;
    private final GroupDsaMapperRepository groupDsaMapperRepository;
    private final DsaService dsaService;


    public List<DsaSheetUserResponse> getDsaEntries(UUID userId) {
        checkUserExists(userId);

        List<DsaSheet> list=dsaSheetRepository.findAllByUserId(userId);
        List<Long> dsaIds=list.stream().map(DsaSheet::getId).toList();
        List<GroupDsaMapper> mappers=groupDsaMapperRepository.findAllByDsaIdIn(dsaIds);
        HashMap<Long,List<UUID>> groupDsaMap=new HashMap<>();
        for(GroupDsaMapper m:mappers){
            groupDsaMap
                    .computeIfAbsent(m.getDsaSheet().getId(), k -> new ArrayList<>())
                    .add(m.getGroup().getId());
        }

        List<DsaSheetUserResponse> response=new ArrayList<>();
        for(DsaSheet entry:list){
            DsaSheetUserResponse body=new DsaSheetUserResponse(
                    entry.getId(),
                    entry.getTitle(),
                    entry.getUrl(),
                    entry.getSource(),
                    entry.getSubmittedAt(),
                    entry.getUpdatedAt(),
                    groupDsaMap.getOrDefault(entry.getId(),List.of()),
                    entry.getNotes()
            );
            response.add(body);
        }

        return response;
    }

    public User checkUserExists(UUID userId){
        return userRepository.findById(userId).orElseThrow(()->
              new UserNotFoundException("user does not exist with this id")
        );
    }

    public UserResponse getUserEntry(UUID userId) {
        User user=checkUserExists(userId);

        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getDisplayName(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getMail(),
                user.getJoinedAt(),
                user.getUpdatedAt(),
                user.isActive()
        );
    }

    public DsaSheetUserResponse addDsaEntry(DsaSheetUserPostRequest request,UUID userId) {
        return dsaService.addDsaEntry(request,userId);
    }

    public void deleteDsaEntries(List<Long> ids, UUID userId) {
        dsaService.deleteDsaEntry(ids,userId);
    }
}
