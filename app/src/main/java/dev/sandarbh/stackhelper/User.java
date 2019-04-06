package dev.sandarbh.stackhelper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class User {
    public String username,profileURL;
    public Integer userId,reputation;
    public URL avatarURL;
    public boolean isRegisteredUser;

    private final String ANONYMOUS_AVATAR_URL = "https://www.timeshighereducation.com/sites/default/files/byline_photos/anonymous-user-gravatar_0.png",
                        ERROR_URL = "https://stackoverflow.com/users/-2/";

    public User(JSONObject owner) throws JSONException, MalformedURLException {

        Log.e("DEBUG", "User: Creating user");

        username = owner.getString("display_name");

        if(owner.getString("user_type").equals("does_not_exist")){
            isRegisteredUser =false;
            reputation=-1;
            avatarURL = new URL(ANONYMOUS_AVATAR_URL);
            profileURL = ERROR_URL;
        }
        else{
            isRegisteredUser = true;

            userId = owner.getInt("user_id");
            reputation = owner.getInt("reputation");

            profileURL = owner.getString("link");
            avatarURL = new URL(owner.getString("profile_image"));
        }
    }
}
