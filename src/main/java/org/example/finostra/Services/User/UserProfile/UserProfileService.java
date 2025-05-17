package org.example.finostra.Services.User.UserProfile;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile.PhoneNumberRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile.UserProfileRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserProfile.GetUserProfileResponse;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserProfile.PhoneNumberResponse;
import org.example.finostra.Entity.User.UserProfile.PhoneNumber;
import org.example.finostra.Entity.User.UserProfile.UserProfile;
import org.example.finostra.Repositories.User.UserProfileRepository;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final String connectionString;
    private final String containerName;
    private BlobContainerClient container;

    private final List<String> allowedImageTypes = List.of("image/png", "image/jpeg", "image/webp");

    public UserProfileService(UserProfileRepository userProfileRepository,
                              @Value("${azure.storage.connection-string}") String connectionString,
                              @Value("${azure.storage.container.user-avatars:user-avatars}") String containerName,
                              UserService userService) {
        this.userProfileRepository = userProfileRepository;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.userService = userService;
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

    @Transactional
    public void createUserProfile(UserProfileRequest userProfileRequest, String userUUID) {

        var user = userService.getById(userUUID);

        UserProfile userProfile = UserProfile.builder()
                .firstNameUa(userProfileRequest.getFirstNameUa())
                .lastNameUa(userProfileRequest.getLastNameUa())
                .patronymicUa(userProfileRequest.getPatronymicUa())
                .firstNameEn(userProfileRequest.getFirstNameEn())
                .lastNameEn(userProfileRequest.getLastNameEn())
                .patronymicEn(userProfileRequest.getPatronymicEn())
                .birthDate(userProfileRequest.getBirthDate())
                .user(user)
                .build();

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public String getPhoneNumber(String userUUID) {

        var user = userService.getById(userUUID);

        return user.getPhoneNumber();
    }

    @Transactional
    public void updateUserProfile(UserProfileRequest userProfileRequest, String userUUID) {

        var user = userService.getById(userUUID);

        var isUserProfile = userProfileRepository.findByUserId(user.getId());
        if(isUserProfile.isEmpty()) throw new EntityNotFoundException("User profile not found!");

        var userProfile = isUserProfile.get();

        userProfile.setFirstNameUa(userProfileRequest.getFirstNameUa());
        userProfile.setLastNameUa(userProfileRequest.getLastNameUa());
        userProfile.setPatronymicUa(userProfileRequest.getPatronymicUa())
        ;
        userProfile.setFirstNameEn(userProfileRequest.getFirstNameEn());
        userProfile.setLastNameEn(userProfileRequest.getLastNameEn());
        userProfile.setPatronymicEn(userProfileRequest.getPatronymicEn());

        userProfile.setBirthDate(userProfileRequest.getBirthDate());

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void addPhoneNumber(PhoneNumberRequest phoneNumberRequest, String userUUID) {

        var user = userService.getById(userUUID);

        var isUserProfile = userProfileRepository.findByUserId(user.getId());
        if (isUserProfile.isEmpty()) throw new EntityNotFoundException("User not found");

        var userProfile = isUserProfile.get();

        userProfile.getPhoneNumbers().add(new PhoneNumber(phoneNumberRequest.getPhoneNumber(), phoneNumberRequest.getDescription()));

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void deletePhoneNumber(String userUUID, String phoneNumberToDelete) {
        var user = userService.getById(userUUID);
        var optionalUserProfile = userProfileRepository.findByUserId(user.getId());
        if (optionalUserProfile.isEmpty()) throw new EntityNotFoundException("User not found");

        var userProfile = optionalUserProfile.get();

        boolean removed = userProfile.getPhoneNumbers().removeIf(
                phoneNumber -> phoneNumber.getPhoneNumber().equals(phoneNumberToDelete)
        );

        if (!removed) {
            throw new EntityNotFoundException("Phone number not found");
        }

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void updatePhoneNumber(String userUUID, PhoneNumberRequest updatedPhoneNumberRequest) {
        var user = userService.getById(userUUID);
        var optionalUserProfile = userProfileRepository.findByUserId(user.getId());
        if (optionalUserProfile.isEmpty()) throw new EntityNotFoundException("User not found");

        var userProfile = optionalUserProfile.get();

        var existingPhone = userProfile.getPhoneNumbers().stream()
                .filter(p -> p.getPhoneNumber().equals(updatedPhoneNumberRequest.getPhoneNumber()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Phone number not found"));

        existingPhone.setDescription(updatedPhoneNumberRequest.getDescription());

        userProfileRepository.save(userProfile);
    }


    @Transactional
    public String uploadImageAvatar(MultipartFile file, String userUUID) {

        var user = userService.getById(userUUID);

        var isUserProfile = userProfileRepository.findByUserId(user.getId());
        if (isUserProfile.isEmpty()) throw new EntityNotFoundException("User not found");

        var userProfile = isUserProfile.get();

        if (!allowedImageTypes.contains(file.getContentType())) throw new IllegalArgumentException("Wrong image type!");

        String oldBlobName = userProfile.getAvatarBlobLink();
        if (oldBlobName != null && !oldBlobName.isBlank()) {
            BlobClient oldBlob = container.getBlobClient(oldBlobName);
            if (oldBlob.exists()) {
                oldBlob.delete();
            }
        }

        String blobName = user.getPublicUUID() + "_" + userProfile.getId().toString() + "_" + file.getOriginalFilename();
        BlobClient blob = container.getBlobClient(blobName);

        try (var is = file.getInputStream()) {
            blob.upload(BinaryData.fromStream(is), true);
            blob.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
        } catch (IOException e) {
            throw new UncheckedIOException("Azure Blob upload failed", e);
        }

        userProfile.setAvatarBlobLink(blobName);
        userProfileRepository.save(userProfile);

        return blobName;
    }

    @Transactional
    public GetUserProfileResponse getUserProfile(String userUUID) {

        var user = userService.getById(userUUID);

        var isUserProfile = userProfileRepository.findByUserId(user.getId());
        if (isUserProfile.isEmpty()) return null;

        var userProfile = isUserProfile.get();

        GetUserProfileResponse response = GetUserProfileResponse.builder()
                .firstNameUa(userProfile.getFirstNameUa())
                .lastNameUa(userProfile.getLastNameUa())
                .patronymicUa(userProfile.getPatronymicUa())
                .firstNameEn(userProfile.getFirstNameEn())
                .lastNameEn(userProfile.getLastNameEn())
                .patronymicEn(userProfile.getPatronymicEn())
                .birthDate(userProfile.getBirthDate())
                .avatarBlobLink(getSasLink(userProfile.getAvatarBlobLink()))
                .phoneNumber(user.getPhoneNumber())
                .phoneNumbers(
                        userProfile.getPhoneNumbers().stream()
                                .map(p -> new PhoneNumberResponse(p.getPhoneNumber(), p.getDescription()))
                                .collect(Collectors.toList())
                )
                .build();

        return response;
    }

    private String getSasLink(String blobName) {

        BlobClient blob = container.getBlobClient(blobName);

        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(15);
        String sasToken = blob.generateSas(new BlobServiceSasSignatureValues(
                expiry, new BlobSasPermission().setReadPermission(true)));

        return blob.getBlobUrl() + "?" + sasToken;
    }


}

