package com.aktheraja.awsimageupload.profile;

import com.aktheraja.awsimageupload.bucket.BucketName;
import com.aktheraja.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {
    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore filestore;
    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore filestore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.filestore = filestore;
    }
    List<UserProfile> getUserProfiles(){
        return userProfileDataAccessService.getUserProfiles();
    }

    void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //1. Check if image is empty
        isFileEmpty(file);
        //2. if file is an image
        isImage(file);
        //3.The user exists in our database
        UserProfile user = getUserProfileOrThrow(userProfileId);
        //Grab some metadata from file if any
        Map<String, String> metaData = extractMetaData(file);

        //Store the image in s3 and update database(userProfileImageLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());
        String filename = String.format("%s-%s", file.getOriginalFilename(),UUID.randomUUID());
        try {
            filestore.save(path,filename,Optional.of(metaData),file.getInputStream());
            user.setUserProfileImageLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public byte [] downLoadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());
        return user.getUserProfileImageLink().map(key->filestore.download(path,key)).orElse(new byte[0]);
    }
    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));
        return metaData;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService.getUserProfiles().stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(),
                ContentType.IMAGE_PNG.getMimeType(),
                ContentType.IMAGE_GIF.getMimeType()
        ).contains(file.getContentType())){
            throw new IllegalStateException("File must be an image [" + file.getContentType() +"]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
        }
    }


}
