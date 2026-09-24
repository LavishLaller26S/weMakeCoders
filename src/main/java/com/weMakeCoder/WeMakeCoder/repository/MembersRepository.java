package com.weMakeCoder.WeMakeCoder.repository;

import com.weMakeCoder.WeMakeCoder.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembersRepository extends JpaRepository<Members,Long> {
    List<Members> findAllByUserId(UUID userId);
}
