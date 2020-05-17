package com.aktheraja.awsimageupload.profile;

import com.aktheraja.awsimageupload.datastore.FakeUserProfileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserProfileDataAccessService {
    private final FakeUserProfileStore fakeUserProfileStore;

    @Autowired
    public UserProfileDataAccessService(FakeUserProfileStore fakeUserProfileStore) {
        this.fakeUserProfileStore = fakeUserProfileStore;
    }
    List<UserProfile> getUserProfiles(){
        return fakeUserProfileStore.getUserProfiles();
    }
}
