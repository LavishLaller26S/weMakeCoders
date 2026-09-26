package com.weMakeCoder.WeMakeCoder.dto.DsaDto;

import com.weMakeCoder.WeMakeCoder.enums.Source;

import java.util.List;
import java.util.UUID;

public record DsaSheetUserPatchRequest(
        Long id,
        String title,
        String url,
        Source source,
        List<UUID> groupIdsDelete,
        List<UUID> groupIdsAdd,
        String notes
) {
}
