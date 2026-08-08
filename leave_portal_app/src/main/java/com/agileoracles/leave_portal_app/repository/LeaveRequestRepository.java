package com.agileoracles.leave_portal_app.repository;

import com.agileoracles.leave_portal_app.model.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequestEntity, Long> {
}
