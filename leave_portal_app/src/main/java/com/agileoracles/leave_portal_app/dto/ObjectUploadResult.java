package com.agileoracles.leave_portal_app.dto;

public record ObjectUploadResult(
        String objectName,
        String objectId,
        String etag
) {
}