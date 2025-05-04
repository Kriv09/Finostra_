package org.example.finostra.Controllers.UserDocuments;

import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.UserDocs.ImgBBService;
import jakarta.transaction.Transactional;
import org.example.finostra.Services.User.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/userdocs")
public class UserDocumentsController {


    private final ImgBBService imgBBService;
    private final UserService userService;

    public UserDocumentsController(ImgBBService imgBBService, UserService userService) {
        this.imgBBService = imgBBService;
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

        String uploadImageLink = imgBBService.uploadImage(file);

        User fetched = userService.getById(publicUUID);
        fetched.setDocsLink(uploadImageLink);
        userService.update(fetched);

        return ResponseEntity.ok("Successfully uploaded image!");

    }
}
