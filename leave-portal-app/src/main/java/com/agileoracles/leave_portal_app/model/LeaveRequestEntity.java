package com.agileoracles.leave_portal_app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "attached_filename", nullable = false)
    private String attachedFilename;

    @Column(name = "reason_for_leave", length = 4000)
    private String reasonForLeave;

    @Column(name = "leave_category")
    private String leaveCategory;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "oci_object_name", nullable = false, length = 1000)
    private String ociObjectName;

    @Column(name = "oci_object_id", nullable = false, length = 1000)
    private String ociObjectId;

    @Column(name = "oci_bucket_name", nullable = false)
    private String ociBucketName;

    public LeaveRequestEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAttachedFilename() {
        return attachedFilename;
    }

    public void setAttachedFilename(String attachedFilename) {
        this.attachedFilename = attachedFilename;
    }

    public String getReasonForLeave() {
        return reasonForLeave;
    }

    public void setReasonForLeave(String reasonForLeave) {
        this.reasonForLeave = reasonForLeave;
    }

    public String getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(String leaveCategory) {
        this.leaveCategory = leaveCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOciObjectName() {
        return ociObjectName;
    }

    public void setOciObjectName(String ociObjectName) {
        this.ociObjectName = ociObjectName;
    }

    public String getOciObjectId() {
        return ociObjectId;
    }

    public void setOciObjectId(String ociObjectId) {
        this.ociObjectId = ociObjectId;
    }

    public String getOciBucketName() {
        return ociBucketName;
    }

    public void setOciBucketName(String ociBucketName) {
        this.ociBucketName = ociBucketName;
    }
}