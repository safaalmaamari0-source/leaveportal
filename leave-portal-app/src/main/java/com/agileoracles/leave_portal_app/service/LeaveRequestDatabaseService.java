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
            String reasonForLeave,
            LeaveCategorizationResult categorizationResult,
            ObjectUploadResult uploadResult,
            String bucketName
    ) {

        LeaveRequestEntity entity =
                new LeaveRequestEntity();

        entity.setUserEmail(
                authenticatedUser
        );

        entity.setAttachedFilename(
                file.getOriginalFilename()
        );

        entity.setReasonForLeave(
                reasonForLeave
        );

        entity.setLeaveCategory(null);

        entity.setCreatedAt(
                LocalDateTime.now()
        );

        entity.setOciObjectName(
                uploadResult.objectName()
        );

        entity.setOciObjectId(
                uploadResult.objectId()
        );

        entity.setOciBucketName(
                bucketName
        );

        return repository.save(entity);
    }

    public List<LeaveRequestEntity> findAllRequests() {
        return repository.findAll();
    }
    public LeaveRequestEntity findByOciObjectName(
            String objectName
    ) {
        return repository
                .findByOciObjectName(objectName)
                .orElse(null);
    }
    public List<LeaveRequestEntity> findPendingCategorization() {
        return repository.findByLeaveCategoryIsNull();
    }
}