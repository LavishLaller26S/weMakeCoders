package com.weMakeCoder.WeMakeCoder.repository;

import com.weMakeCoder.WeMakeCoder.entity.GroupDsaMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupDsaMapperRepository extends JpaRepository<GroupDsaMapper,Long> {
    List<GroupDsaMapper> findAllByDsaIdIn(List<Long> dsaIds);
}
