package org.example.finostra.Controllers.UserDocuments;

import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.UserDocs.AzureBlobImageService;
import org.example.finostra.Services.User.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/userdocs")
public class UserDocumentsController {

    private final AzureBlobImageService imageService;
    private final UserService userService;

    public UserDocumentsController(AzureBlobImageService imageService,
                                   UserService userService) {
        this.imageService = imageService;
        this.userService  = userService;
    }

    @PostMapping(value = "/upload/{publicUUID}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<String> uploadUserDocument(@PathVariable String publicUUID,
                                                     @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }
        String blobName = imageService.uploadImage(file);
        User user = userService.getById(publicUUID);
        user.setDocsLink(blobName);
        userService.update(user);
        return ResponseEntity.ok("Successfully uploaded image!");
    }

    @GetMapping("/fetch/{publicUUID}")
    @Transactional
    public ResponseEntity<BlobDto> fetchDocs(@PathVariable String publicUUID,
                                             Authentication auth) {

        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!auth.getName().equals(publicUUID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getById(publicUUID);
        if (user.getDocsLink() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AzureBlobImageService.AzureBlobInfo info = imageService.fetchImage(user.getDocsLink());
        return ResponseEntity.ok(new BlobDto(info.url(), info.expiresAt(), "image/png"));
    }

    public record BlobDto(String url, OffsetDateTime expiresAt, String contentType) {}
}
