package com.agileoracles.leave_portal_app.dto;

import java.time.LocalDateTime;

public record LeaveFileResponse(

        String objectName,
        String fileName,
        String leaveCategory,
        String userEmail,
        LocalDateTime createdAt

) {
}
