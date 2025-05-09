package org.example.finostra.Services.User.UserDocs;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AzureBlobImageService {

    private final String connectionString;
    private final String containerName;

    private BlobContainerClient container;

    public AzureBlobImageService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container.user-rel:user-rel}") String containerName) {
        this.connectionString = connectionString;
        this.containerName    = containerName;
    }

    @PostConstruct
    void init() {
        BlobServiceClient service = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        container = service.getBlobContainerClient(containerName);
        if (!container.exists()) {
            container.create();
        }
    }

    public String uploadImage(MultipartFile file) {
        String blobName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blob = container.getBlobClient(blobName);

        try (var is = file.getInputStream()) {
            blob.upload(BinaryData.fromStream(is), true);
            blob.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
        } catch (IOException e) {
            throw new UncheckedIOException("Azure Blob upload failed", e);
        }
        return blobName;
    }

    public AzureBlobInfo fetchImage(String blobName) {
        BlobClient blob = container.getBlobClient(blobName);

        OffsetDateTime expires = OffsetDateTime.now().plusMinutes(15);
        String sas = blob.generateSas(new BlobServiceSasSignatureValues(
                expires,
                new BlobSasPermission().setReadPermission(true)));

        return new AzureBlobInfo(
                blob.getBlobUrl() + "?" + sas,
                expires.truncatedTo(ChronoUnit.SECONDS));
    }

    public record AzureBlobInfo(String url, OffsetDateTime expiresAt) {}
}
