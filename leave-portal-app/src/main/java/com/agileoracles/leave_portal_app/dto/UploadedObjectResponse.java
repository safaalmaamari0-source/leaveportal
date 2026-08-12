package com.agileoracles.leave_portal_app.dto;

import java.util.Date;

public record UploadedObjectResponse(
        String objectName,
        Long sizeInBytes,
        String etag,
        Date uploadedAt
) {
}
