package com.agileoracles.leave_portal_app.model;

public enum LeaveCategory {
    SICK_LEAVE("Sick Leave"),
    ANNUAL_LEAVE("Annual Leave"),
    EMERGENCY_LEAVE("Emergency Leave"),
    MATERNITY_LEAVE("Maternity Leave"),
    UNPAID_LEAVE("Unpaid Leave"),
    OTHER("Other");

    private final String displayName;

    LeaveCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
