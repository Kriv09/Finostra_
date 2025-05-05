package org.example.finostra.Services.User.UserDocs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageKitService {

    @Value("${api.key.imagekit}")
    private String privateApiKey;

    @Value("${api.url.imagekit.upload}")
    private String uploadUrl;

    @Value("${api.url.imagekit.get}")
    private String getFileUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String uploadImage(MultipartFile file) {
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", "data:image/png;base64," + base64);
            body.add("fileName", fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBasicAuth(privateApiKey, "");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(uploadUrl, HttpMethod.POST, request, String.class);

            JsonNode d = mapper.readTree(response.getBody());

            return d.path("fileId").asText();
        } catch (Exception e) {
            throw new IllegalStateException("ImageKit upload failed", e);
        }
    }

    public ImageKitInfo fetchImage(String fileId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(privateApiKey, "");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    getFileUrl.replace("{fileId}", fileId),
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode d = mapper.readTree(response.getBody());

            return new ImageKitInfo(
                    d.path("url").asText()
            );
        } catch (Exception e) {
            throw new IllegalStateException("ImageKit fetch failed", e);
        }
    }

    public record ImageKitInfo(String url) {}
}
