package org.example.finostra.Configuration.Tesseract;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class TessdataBootstrap {

    private final BlobServiceClient blobService;

    @Value("${tesseract.datapath:/tmp/tessdata}")
    private String datapath;

    @PostConstruct
    public void ensureTessdataPresent() throws IOException {

        Path dir = Paths.get(datapath, "tessdata");
        if (Files.exists(dir) && Files.list(dir).findAny().isPresent()) return;

        Files.createDirectories(dir);

        BlobContainerClient container = blobService.getBlobContainerClient("props");
        for (BlobItem item : container.listBlobs()) {
            if (!item.getName().endsWith(".traineddata")) continue;
            try (OutputStream out = Files.newOutputStream(dir.resolve(item.getName()))) {
                container.getBlobClient(item.getName()).download(out);
            }
        }
    }
}