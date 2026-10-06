package com.weMakeCoder.WeMakeCoder.repository;

import com.weMakeCoder.WeMakeCoder.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group,UUID> {
    boolean existsByGroupName(String groupName);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Group g SET g.membersCount = g.membersCount + 1 WHERE g.id = :groupId")
    void incrementMembersCount(@Param("groupId") UUID groupId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Group g SET g.membersCount = g.membersCount - 1 WHERE g.id = :groupId AND g.membersCount > 0")
    void decrementMembersCount(@Param("groupId") UUID groupId);

}
