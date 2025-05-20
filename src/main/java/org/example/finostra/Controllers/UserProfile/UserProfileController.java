package org.example.finostra.Controllers.UserProfile;

import org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile.PhoneNumberRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile.UserProfileRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserProfile.GetUserProfileResponse;
import org.example.finostra.Services.User.UserProfile.UserProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/userProfile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/get")
    public ResponseEntity<GetUserProfileResponse> getUserProfile(Authentication auth) {
        var userProfile = userProfileService.getUserProfile(auth.getName());

        if(userProfile == null) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUserProfile(@RequestBody UserProfileRequest userProfileRequest, Authentication auth) {
        userProfileService.updateUserProfile(userProfileRequest, auth.getName());
        return ResponseEntity.ok("User profile updated!");
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUserProfile(@RequestBody UserProfileRequest userProfileRequest, Authentication auth) {
        userProfileService.createUserProfile(userProfileRequest, auth.getName());
        return ResponseEntity.ok("User profile created!");
    }

    @PostMapping("/addPhoneNumber")
    public ResponseEntity<String> addPhoneNumber(@RequestBody PhoneNumberRequest phoneNumberRequest, Authentication auth) {
        userProfileService.addPhoneNumber(phoneNumberRequest, auth.getName());
        return ResponseEntity.ok("Phone number added to user profile!");
    }
    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<String> updatePhoneNumber(@RequestBody PhoneNumberRequest phoneNumberRequest, Authentication auth) {
        userProfileService.updatePhoneNumber(auth.getName(), phoneNumberRequest);
        return ResponseEntity.ok("Phone number updated!");
    }
    @DeleteMapping("/deletePhoneNumber")
    public ResponseEntity<String> deletePhoneNumber(@RequestParam String phoneNumber, Authentication auth) {
        userProfileService.deletePhoneNumber(auth.getName(), phoneNumber);
        return ResponseEntity.ok("Phone number deleted!");
    }

    @PostMapping(value = "/uploadAvatarImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatarImage(@RequestParam("image") MultipartFile image, Authentication auth) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image is empty!");
        }
        userProfileService.uploadImageAvatar(image, auth.getName());
        return ResponseEntity.ok("Image uploaded!");
    }

}
