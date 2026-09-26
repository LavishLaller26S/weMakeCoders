package com.weMakeCoder.WeMakeCoder.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserPatchRequest;
import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserPostRequest;
import com.weMakeCoder.WeMakeCoder.dto.DsaDto.DsaSheetUserResponse;
import com.weMakeCoder.WeMakeCoder.entity.DsaSheet;
import com.weMakeCoder.WeMakeCoder.entity.GroupDsaMapper;
import com.weMakeCoder.WeMakeCoder.entity.User;
import com.weMakeCoder.WeMakeCoder.exception.DsaEntryNotFoundException;
import com.weMakeCoder.WeMakeCoder.exception.UrlAlreadyExistException;
import com.weMakeCoder.WeMakeCoder.repository.*;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class DsaService {

    private final DsaSheetRepository dsaSheetRepository;
    private final UserService userService;
    private final GroupDsaMapperRepository groupDsaMapperRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public DsaSheetUserResponse addDsaEntry(DsaSheetUserPostRequest request, UUID userId) {
        User user=userService.checkUserExists(userId);
        urlExists(request.url());

        DsaSheet entry=DsaSheet.builder().
                title(request.title()).
                url(request.url()).
                source(request.source()).
                notes(request.notes()).
                user(user).
                build();

        DsaSheet savedEntry;
        try{
            savedEntry=dsaSheetRepository.save(entry);
        }catch (DataIntegrityViolationException e){
            throw new UrlAlreadyExistException("url already exist");
        }

        List<UUID> newGroupIds=groupDsaMapperFromList(request.groupIds(),savedEntry);

        return new DsaSheetUserResponse(
                savedEntry.getId(),
                savedEntry.getTitle(),
                savedEntry.getUrl(),
                savedEntry.getSource(),
                savedEntry.getSubmittedAt(),
                savedEntry.getUpdatedAt(),
                newGroupIds,
                savedEntry.getNotes());
    }

    @Transactional
    public void deleteDsaEntry(List<Long> ids,UUID userId){
            userService.checkUserExists(userId);
            dsaSheetRepository.deleteAllByIdAndUserId(ids,userId);
    }
    @Transactional
    public DsaSheetUserResponse patchDsaEntry(DsaSheetUserPatchRequest request){
        DsaSheet entry=dsaSheetRepository.findById(request.id()).orElseThrow(()->
                new DsaEntryNotFoundException("dsa entry does not exist"));

        if(request.notes()!=null){
            entry.setNotes(request.notes());
        }
        if(request.source()!=null){
            entry.setSource(request.source());
        }
        if(request.title()!=null){
            entry.setTitle(request.title());
        }
        if(request.url()!=null){
            urlExists(request.url());
            entry.setUrl(request.url());
        }

        DsaSheet savedEntry=dsaSheetRepository.save(entry);

        if(!request.groupIdsDelete().isEmpty()){
            groupDsaMapperRepository.deleteAllByGroupIdAndDsaId(request.groupIdsDelete(), request.id());
        }
        List<UUID> newGroupIds=new ArrayList<>();
        if(!request.groupIdsAdd().isEmpty()){
            newGroupIds=groupDsaMapperFromList(request.groupIdsAdd(),savedEntry);
        }

        return new DsaSheetUserResponse(
                savedEntry.getId(),
                savedEntry.getTitle(),
                savedEntry.getUrl(),
                savedEntry.getSource(),
                savedEntry.getSubmittedAt(),
                savedEntry.getUpdatedAt(),
                newGroupIds,
                savedEntry.getNotes());


    }


    public void urlExists(String url){
        if(dsaSheetRepository.existsByUrl(url)){
            throw new UrlAlreadyExistException("url already exist in the db");
        }
    }

    @Transactional
    public List<UUID> groupDsaMapperFromList(List<UUID> groupIds,DsaSheet entry){
        List<GroupDsaMapper> list=groupIds.stream().map(groupId-> {
                    return GroupDsaMapper.builder().dsaSheet(entry).
                            group(groupRepository.getReferenceById(groupId)).build();
                }
        ).toList();

        List<GroupDsaMapper> addedGroupDsaMapping=groupDsaMapperRepository.saveAll(list);

        return addedGroupDsaMapping.stream().map(g->
                g.getGroup().getId()).
                toList();

    }

}
