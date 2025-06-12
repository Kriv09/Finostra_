package org.example.finostra.Services.Contract;

import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import com.azure.storage.blob.BlobUrlParts;
import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ContractService {

    private final UserService userService;
    private final BlobContainerClient container;

    public ContractService(UserService userService,
                           @Value("${azure.storage.connection-string}") String connectionString,
                           @Value("${azure.storage.container.contracts}") String containerName) {
        this.userService = userService;
        BlobServiceClient svc = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.container = svc.getBlobContainerClient(containerName);
        if (!container.exists()) container.create();
    }

    public List<ContractDto> fetchAllContractsByUserPublicUUID(String userPublicUUID) {
        User user = userService.getById(userPublicUUID);
        return user.getContracts()
                .stream()
                .map(c -> new ContractDto(c.getId(), buildSas(extractBlobName(c.getBlobLink()))))
                .toList();
    }

    public String signUrl(String storedLink) {
        return buildSas(extractBlobName(storedLink));
    }

    private String extractBlobName(String stored) {
        if (stored.startsWith("https://")) {
            try {
                BlobUrlParts parts = BlobUrlParts.parse(new URL(stored));
                return parts.getBlobName();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid blob URL", e);
            }
        }
        return stored;
    }

    private String buildSas(String blobName) {
        BlobClient blob = container.getBlobClient(blobName);
        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(15);
        String token = blob.generateSas(new BlobServiceSasSignatureValues(
                expiry, new BlobSasPermission().setReadPermission(true)));
        return blob.getBlobUrl() + "?" + token;
    }

    public record ContractDto(Long id, String url) {}
}
