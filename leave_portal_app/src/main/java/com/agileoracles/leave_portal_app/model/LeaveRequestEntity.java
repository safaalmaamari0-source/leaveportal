package com.agileoracles.leave_portal_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "authenticated_user",
            nullable = false
    )
    private String authenticatedUser;

    @Column(
            name = "original_file_name",
            nullable = false
    )
    private String originalFileName;

    @Column(
            name = "leave_category",
            nullable = false
    )
    private String leaveCategory;

    @Column(
            name = "upload_date_time",
            nullable = false
    )
    private LocalDateTime uploadDateTime;

    @Column(
            name = "oci_object_name",
            nullable = false,
            length = 1000
    )
    private String ociObjectName;

    public LeaveRequestEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(
            String authenticatedUser
    ) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(
            String originalFileName
    ) {
        this.originalFileName = originalFileName;
    }

    public String getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(
            String leaveCategory
    ) {
        this.leaveCategory = leaveCategory;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(
            LocalDateTime uploadDateTime
    ) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getOciObjectName() {
        return ociObjectName;
    }

    public void setOciObjectName(
            String ociObjectName
    ) {
        this.ociObjectName = ociObjectName;
    }
}