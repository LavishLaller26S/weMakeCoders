package com.weMakeCoder.WeMakeCoder.dto.DsaDto;

import com.weMakeCoder.WeMakeCoder.enums.Source;

import java.util.List;
import java.util.UUID;

public record DsaSheetUserPostRequest(
        String title,
        String url,
        Source source,
        List<UUID> groupIds,
        String notes
) {
}
