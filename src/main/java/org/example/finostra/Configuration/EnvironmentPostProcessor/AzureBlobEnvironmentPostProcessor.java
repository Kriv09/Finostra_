package org.example.finostra.Configuration.EnvironmentPostProcessor;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AzureBlobEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String CONNECTION_STRING = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String CONTAINER_NAME = "config-files";
    private static final String BLOB_NAME = "application.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(CONNECTION_STRING)
                .containerName(CONTAINER_NAME)
                .blobName(BLOB_NAME)
                .buildClient();

        Properties props = new Properties();
        try (InputStream is = blobClient.openInputStream()) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from Azure Blob Storage", e);
        }

        PropertySource<?> propertySource = new PropertiesPropertySource("azureBlobProperties", props);
        environment.getPropertySources().addLast(propertySource);
    }
}