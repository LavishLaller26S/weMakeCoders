package com.weMakeCoder.WeMakeCoder.repository;

import com.weMakeCoder.WeMakeCoder.entity.DsaSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DsaSheetRepository extends JpaRepository<DsaSheet,Long> {
    List<DsaSheet> findAllByUserId(UUID userId);

    Optional<DsaSheet> findAllByUrl(String url);

    boolean existsByUrl(String url);

    void deleteAllByIdAndUserId(List<Long> ids,UUID userId);
}
