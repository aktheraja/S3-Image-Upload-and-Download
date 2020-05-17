package com.aktheraja.awsimageupload.datastore;

import com.aktheraja.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileStore {
    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();
    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("97a8ae7d-805c-45d8-8610-51c2a1104b3b"),"janetjones",null));
                USER_PROFILES.add( new UserProfile(UUID.fromString("1ad5ad8f-0039-420f-8dcf-17ff67bbf43a"),"anoniojunior",null));
    }
        public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
