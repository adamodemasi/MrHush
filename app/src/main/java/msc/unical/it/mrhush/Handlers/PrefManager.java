package msc.unical.it.mrhush.Handlers;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "mrhush-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String AUTHENTICATED_USER = "AuthenticatedUser";
    private static final String PROFILE_PIC = "ProfilePic";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context c;
    // shared pref mode
    int PRIVATE_MODE = 0;

    public void removeAuthUser()
    {
        editor.remove(AUTHENTICATED_USER);
        editor.remove(PROFILE_PIC);
        editor.commit();
    }

    public PrefManager(Context context) {
        this.c = context;
        pref = c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getProfilePicURL() {
        return pref.getString(PROFILE_PIC, null);
    }

    public void setProfilePicURL(String URL) {
        editor.putString(PROFILE_PIC, URL);
        editor.commit();
    }

    public void setAuthenticatedUser(String id) {
        editor.putString(AUTHENTICATED_USER, id);
        editor.commit();
    }

    public String isAuthenticatedUser() {
        return pref.getString(AUTHENTICATED_USER, null);


    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

}