package com.agileoracles.leave_portal_app.controller;

import com.agileoracles.leave_portal_app.dto.ObjectUploadResult;
import com.agileoracles.leave_portal_app.dto.UploadedObjectResponse;
import com.agileoracles.leave_portal_app.exception.InvalidFileException;
import com.agileoracles.leave_portal_app.model.LeaveCategorizationResult;
import com.agileoracles.leave_portal_app.model.LeaveRequestEntity;
import com.agileoracles.leave_portal_app.service.LeaveCategorizationService;
import com.agileoracles.leave_portal_app.service.LeaveRequestDatabaseService;
import com.agileoracles.leave_portal_app.service.OciObjectStorageService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import com.agileoracles.leave_portal_app.dto.LeaveFileResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class LeaveUploadController {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final LeaveCategorizationService categorizationService;

    private final OciObjectStorageService objectStorageService;

    private final LeaveRequestDatabaseService databaseService;

    public LeaveUploadController(
            LeaveCategorizationService categorizationService,
            OciObjectStorageService objectStorageService,
            LeaveRequestDatabaseService databaseService
    ) {
        this.categorizationService =
                categorizationService;

        this.objectStorageService =
                objectStorageService;

        this.databaseService =
                databaseService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        validateFile(file);

        String fileContent =
                new String(
                        file.getBytes(),
                        StandardCharsets.UTF_8
                );

        LeaveCategorizationResult categorizationResult =
                categorizationService.categorize(
                        fileContent
                );

        String authenticatedUser =
                getAuthenticatedUser(
                        authentication
                );

        ObjectUploadResult uploadResult =
                objectStorageService.uploadFile(
                        file
                );

        LeaveRequestEntity savedRecord =
                databaseService.saveLeaveRequest(
                        authenticatedUser,
                        file,
                        fileContent,
                        categorizationResult,
                        uploadResult,
                        objectStorageService.getBucketName()
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "databaseRecordId",
                savedRecord.getId()
        );

        response.put(
                "authenticatedUser",
                savedRecord.getUserEmail()
        );

        response.put(
                "uploadedFileName",
                savedRecord.getAttachedFilename()
        );

        response.put(
                "leaveCategory",
                savedRecord.getLeaveCategory()
        );

        response.put(
                "matchedKeywords",
                categorizationResult.matchedKeywords()
        );

        response.put(
                "reason",
                categorizationResult.reason()
        );

        response.put(
                "uploadTimestamp",
                savedRecord.getCreatedAt()
        );

        response.put(
                "objectName",
                savedRecord.getOciObjectName()
        );

        response.put(
                "objectId",
                savedRecord.getOciObjectId()
        );

        response.put(
                "bucketName",
                savedRecord.getOciBucketName()
        );

        return response;
    }

    @GetMapping("/requests")
    public Map<String, Object> listSavedRequests(
            Authentication authentication
    ) {

        List<LeaveRequestEntity> requests =
                databaseService.findAllRequests();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "authenticatedUser",
                getAuthenticatedUser(authentication)
        );

        response.put(
                "numberOfRequests",
                requests.size()
        );

        response.put(
                "requests",
                requests
        );

        return response;
    }
    @GetMapping("/files")
    public Map<String, Object> listMyFiles(
            Authentication authentication
    ) {

        List<LeaveFileResponse> files =
                objectStorageService
                        .listMyFiles()
                        .stream()
                        .map(object -> {

                            LeaveRequestEntity record =
                                    databaseService
                                            .findByOciObjectName(
                                                    object.getName()
                                            );

                            if (record == null) {
                                return new LeaveFileResponse(
                                        object.getName(),
                                        object.getName(),
                                        null,
                                        null,
                                        null
                                );
                            }

                            return new LeaveFileResponse(
                                    object.getName(),
                                    record.getAttachedFilename(),
                                    record.getLeaveCategory(),
                                    record.getUserEmail(),
                                    record.getCreatedAt()
                            );
                        })
                        .toList();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "authenticatedUser",
                getAuthenticatedUser(authentication)
        );

        response.put(
                "bucketName",
                objectStorageService.getBucketName()
        );

        response.put(
                "numberOfFiles",
                files.size()
        );

        response.put(
                "files",
                files
        );

        return response;
    }

    @GetMapping("/files/shared")
    public Map<String, Object> listSharedFiles(
            Authentication authentication
    ) {

        List<UploadedObjectResponse> files =
                objectStorageService
                        .listSharedBucketFiles()
                        .stream()
                        .map(object ->
                                new UploadedObjectResponse(
                                        object.getName(),
                                        object.getSize(),
                                        object.getEtag(),
                                        object.getTimeCreated()
                                )
                        )
                        .toList();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "authenticatedUser",
                getAuthenticatedUser(authentication)
        );

        response.put(
                "bucketName",
                objectStorageService.getBucketName()
        );

        response.put(
                "numberOfFiles",
                files.size()
        );

        response.put(
                "files",
                files
        );

        return response;
    }


    private String getAuthenticatedUser(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "The user is not authenticated."
            );
        }

        String authenticatedUser =
                authentication.getName();

        if (authentication.getPrincipal()
                instanceof OAuth2User oauth2User) {

            String email =
                    oauth2User.getAttribute("email");

            if (email != null
                    && !email.isBlank()) {

                authenticatedUser =
                        email;
            }
        }

        return authenticatedUser;
    }

    private void validateFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new InvalidFileException(
                    "Please upload a non-empty file."
            );
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null
                || fileName.isBlank()) {

            throw new InvalidFileException(
                    "The uploaded file must have a valid name."
            );
        }

        if (!fileName
                .toLowerCase(Locale.ROOT)
                .endsWith(".txt")) {

            throw new InvalidFileException(
                    "Unsupported file type. Only .txt files are allowed."
            );
        }

        String contentType =
                file.getContentType();

        if (contentType != null
                && !contentType.isBlank()
                && !contentType.equalsIgnoreCase(
                "text/plain"
        )
                && !contentType.equalsIgnoreCase(
                "application/octet-stream"
        )) {

            throw new InvalidFileException(
                    "The uploaded file must be a plain text file."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new InvalidFileException(
                    "The uploaded file exceeds the 5 MB limit."
            );
        }
    }
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam String objectName
    ) throws IOException {

        byte[] fileBytes =
                objectStorageService.downloadFile(objectName);

        String fileName =
                objectName.contains("/")
                        ? objectName.substring(objectName.lastIndexOf("/") + 1)
                        : objectName;

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(MediaType.TEXT_PLAIN)
                .body(fileBytes);
    }
}