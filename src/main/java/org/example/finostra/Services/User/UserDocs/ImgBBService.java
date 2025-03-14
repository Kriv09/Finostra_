package org.example.finostra.Services.User.UserDocs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ImgBBService {

    @Value("${api.key.imgbb}")
    private String apiKey;

    private final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public String uploadImage(MultipartFile file) {
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("key", apiKey);
            map.add("image", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(UPLOAD_URL, HttpMethod.POST, entity, String.class);

            return response.getBody();
        } catch (Exception e) {
            return "Upload failed: " + e.getMessage();
        }
    }
}
