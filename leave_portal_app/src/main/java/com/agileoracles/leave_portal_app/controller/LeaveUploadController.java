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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        this.categorizationService = categorizationService;
        this.objectStorageService = objectStorageService;
        this.databaseService = databaseService;
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

        String fileContent = new String(
                file.getBytes(),
                StandardCharsets.UTF_8
        );

        LeaveCategorizationResult categorizationResult =
                categorizationService.categorize(fileContent);

        String authenticatedUser =
                getAuthenticatedUser(authentication);

        ObjectUploadResult uploadResult =
                objectStorageService.uploadFile(file);

        LeaveRequestEntity savedRecord =
                databaseService.saveLeaveRequest(
                        authenticatedUser,
                        file,
                        categorizationResult,
                        uploadResult
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "databaseRecordId",
                savedRecord.getId()
        );

        response.put(
                "authenticatedUser",
                savedRecord.getAuthenticatedUser()
        );

        response.put(
                "uploadedFileName",
                savedRecord.getOriginalFileName()
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
                savedRecord.getUploadDateTime()
        );

        response.put(
                "objectName",
                savedRecord.getOciObjectName()
        );

        response.put(
                "objectId",
                uploadResult.objectId()
        );

        response.put(
                "etag",
                uploadResult.etag()
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
    public Map<String, Object> listAllFiles(
            Authentication authentication
    ) {

        List<UploadedObjectResponse> files =
                objectStorageService
                        .listAllFiles()
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

            if (email != null && !email.isBlank()) {
                authenticatedUser = email;
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

        if (fileName == null || fileName.isBlank()) {
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
                && !contentType.equalsIgnoreCase("text/plain")
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
}