package com.weMakeCoder.WeMakeCoder.dto;

import com.weMakeCoder.WeMakeCoder.enums.Source;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DsaSheetUserResponse(
        Long id,
        String title,
        String url,
        Source source,
        Instant submittedAt,
        Instant updatedAt,
        List<UUID> groupIds,
        String notes
) {
}
