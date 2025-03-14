package org.example.finostra.Controllers.UserDocuments;

import org.example.finostra.Services.User.UserDocs.ImgBBService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/userdocs")
public class UserDocumentsController {

    @Autowired
    private ImgBBService imgBBService;

    @PostMapping("/upload")
    @Transactional
    public String uploadUserDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty!";
        }
        // TODO: Check the image according to docs format

        return imgBBService.uploadImage(file);
        }
}
