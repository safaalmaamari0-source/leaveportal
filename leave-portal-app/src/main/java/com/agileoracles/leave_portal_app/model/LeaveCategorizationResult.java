package com.agileoracles.leave_portal_app.model;

import java.util.List;

public record LeaveCategorizationResult(
        LeaveCategory category,
        List<String> matchedKeywords,
        String reason
) {
}
