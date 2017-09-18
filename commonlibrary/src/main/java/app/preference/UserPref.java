package app.preference;

import android.content.Context;

import foundation.util.JSONUtils;

public class UserPref extends BasePref {
    private static final String USERINFO = "userInfo";


    public UserPref(Context context, String account) {
        super(context, account);
    }


}
