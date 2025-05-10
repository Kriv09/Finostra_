package org.example.finostra.Services.Contract;

import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import jakarta.annotation.PostConstruct;
import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final UserService userService;
    private final String connectionString;
    private final String containerName;
    private BlobContainerClient container;

    public ContractService(UserService userService,
                           @Value("${azure.storage.connection-string}") String connectionString,
                           @Value("${azure.storage.container.user-rel:user-rel}") String containerName) {
        this.userService      = userService;
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

    public List<Contract> fetchAllContractsByUserPublicUUID(String userPublicUUID) {

        User user = userService.getById(userPublicUUID);
        List<Contract> raw = user.getContracts();

        return raw.stream()
                .map(this::enrichWithSasLink)
                .collect(Collectors.toList());
    }

    private Contract enrichWithSasLink(Contract contract) {

        BlobClient blob = container.getBlobClient(contract.getBlobLink());

        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(15);
        String sasToken = blob.generateSas(new BlobServiceSasSignatureValues(
                expiry, new BlobSasPermission().setReadPermission(true)));

        contract.setBlobLink(blob.getBlobUrl() + "?" + sasToken);
        return contract;
    }
}
