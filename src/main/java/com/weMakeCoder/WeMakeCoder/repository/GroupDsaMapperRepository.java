package com.weMakeCoder.WeMakeCoder.repository;

import com.weMakeCoder.WeMakeCoder.entity.GroupDsaMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupDsaMapperRepository extends JpaRepository<GroupDsaMapper,Long> {
    List<GroupDsaMapper> findAllByDsaIdIn(List<Long> dsaIds);

    void deleteAllByGroupIdAndDsaId(List<UUID> uuids,Long dsaId);
}
