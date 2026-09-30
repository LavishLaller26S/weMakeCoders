package com.weMakeCoder.WeMakeCoder.repository;

import java.util.List;
import java.util.UUID;

import com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupResponse;
import com.weMakeCoder.WeMakeCoder.entity.Members;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface MembersRepository extends JpaRepository<Members,Long> {

    @Query("select new com.weMakeCoder.WeMakeCoder.dto.GroupDto.GroupResponse(" +
            "g.id, g.groupName, g.displayName, g.admin.id, " +
            "g.membersCount, g.createdAt, g.updatedAt) " +
            "from Members m join m.group g " +
            "where m.user.id = :userId")
    List<GroupResponse> findGroupResponsesByUserId(@Param("userId") UUID userId);

    boolean existsByGroupIdAndUserId(UUID uuid, UUID userId);
}
