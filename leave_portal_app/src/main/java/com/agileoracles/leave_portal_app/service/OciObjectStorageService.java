package com.agileoracles.leave_portal_app.service;

import com.agileoracles.leave_portal_app.dto.ObjectUploadResult;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;

import java.util.Comparator;
import java.util.List;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class OciObjectStorageService {

    private final ObjectStorage objectStorage;
    private final String namespace;
    private final String bucketName;

    public OciObjectStorageService(
            ObjectStorage objectStorage,
            @Value("${oci.namespace}") String namespace,
            @Value("${oci.bucket-name}") String bucketName
    ) {
        this.objectStorage = objectStorage;
        this.namespace = namespace;
        this.bucketName = bucketName;
    }

    public ObjectUploadResult uploadFile(
            MultipartFile file
    ) throws IOException {

        String originalFileName =
                file.getOriginalFilename() != null
                        ? file.getOriginalFilename()
                        : "leave-request.txt";

        String shortId =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        String safeFileName =
                originalFileName
                        .replace("\\", "_")
                        .replace("/", "_")
                        .replaceAll("[^a-zA-Z0-9._-]", "-");

        String objectName =
                shortId
                        + "-"
                        + safeFileName;

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .objectName(objectName)
                        .contentType("text/plain")
                        .contentLength(file.getSize())
                        .putObjectBody(file.getInputStream())
                        .build();

        PutObjectResponse response =
                objectStorage.putObject(request);

        String objectId =
                namespace
                        + "/"
                        + bucketName
                        + "/"
                        + objectName;

        return new ObjectUploadResult(
                objectName,
                objectId,
                response.getETag()
        );
    }
    public List<ObjectSummary> listAllFiles() {

        ListObjectsRequest request =
                ListObjectsRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .fields("name,size,etag,timeCreated")
                        .build();

        return objectStorage
                .listObjects(request)
                .getListObjects()
                .getObjects()
                .stream()
                .sorted(
                        Comparator.comparing(
                                ObjectSummary::getTimeCreated,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .toList();
    }

    public String getBucketName() {
        return bucketName;
    }

}