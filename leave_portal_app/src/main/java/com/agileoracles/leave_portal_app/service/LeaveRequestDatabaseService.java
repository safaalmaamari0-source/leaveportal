package com.agileoracles.leave_portal_app.service;

import com.agileoracles.leave_portal_app.dto.ObjectUploadResult;
import com.agileoracles.leave_portal_app.model.LeaveCategorizationResult;
import com.agileoracles.leave_portal_app.model.LeaveRequestEntity;
import com.agileoracles.leave_portal_app.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveRequestDatabaseService {

    private final LeaveRequestRepository repository;

    public LeaveRequestDatabaseService(
            LeaveRequestRepository repository
    ) {
        this.repository = repository;
    }

    public LeaveRequestEntity saveLeaveRequest(
            String authenticatedUser,
            MultipartFile file,
            LeaveCategorizationResult categorizationResult,
            ObjectUploadResult uploadResult
    ) {

        LeaveRequestEntity entity =
                new LeaveRequestEntity();

        entity.setAuthenticatedUser(
                authenticatedUser
        );

        entity.setOriginalFileName(
                file.getOriginalFilename()
        );

        entity.setLeaveCategory(
                categorizationResult
                        .category()
                        .getDisplayName()
        );

        entity.setUploadDateTime(
                LocalDateTime.now()
        );

        entity.setOciObjectName(
                uploadResult.objectName()
        );

        return repository.save(entity);
    }

    public List<LeaveRequestEntity> findAllRequests() {
        return repository.findAll();
    }
}
