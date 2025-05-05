package org.example.finostra.Controllers.UserDocuments;

import jakarta.servlet.http.HttpServletResponse;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.UserDocs.ImageKitService;
import jakarta.transaction.Transactional;
import org.example.finostra.Services.User.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/userdocs")
public class UserDocumentsController {


    private final ImageKitService imageKitService;
    private final UserService userService;

    public UserDocumentsController(ImageKitService imageKitService, UserService userService) {
        this.imageKitService = imageKitService;
        this.userService = userService;
    }

    @PostMapping("/upload/{publicUUID}")
    @Transactional
    public ResponseEntity<String> uploadUserDocument(
            @PathVariable String publicUUID,
            @RequestParam("file") MultipartFile file)
    {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }

        String uploadedImageId = imageKitService.uploadImage(file);

        User fetched = userService.getById(publicUUID);
        fetched.setDocsLink(uploadedImageId);
        userService.update(fetched);

        return ResponseEntity.ok("Successfully uploaded image!");
    }

    @GetMapping("/fetch/{publicUUID}")
    @Transactional
    public ImageKitService.ImageKitInfo fetchDocs(@PathVariable String publicUUID) {
        User fetched = userService.getById(publicUUID);
        return imageKitService.fetchImage(fetched.getDocsLink());
    }
}
