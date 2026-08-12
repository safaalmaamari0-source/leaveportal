package com.agileoracles.leave_portal_app.repository;

import com.agileoracles.leave_portal_app.model.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequestEntity, Long> {

    Optional<LeaveRequestEntity> findByOciObjectName(
            String ociObjectName
    );
    List<LeaveRequestEntity> findByLeaveCategoryIsNull();
}