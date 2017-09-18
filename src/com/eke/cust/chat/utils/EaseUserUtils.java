package com.eke.cust.chat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.controller.EaseUI;
import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;


public class EaseUserUtils {

    static EaseUI.EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        return ChatHelper.getInstance().getUserInfo(username);
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if(username.equals(EaseConstant.EXTRA_KEFU)){
            imageView.setImageResource(R.drawable.kefu);

        }else{
            CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();

            if (currentUser != null && username.toLowerCase().equals(currentUser.custid.toLowerCase())) {
                if (!TextUtils.isEmpty( currentUser.ekeicon )){
                    user.setAvatar(currentUser.ekeicon);
                }

            }
            if (user!=null&&!StringCheckHelper.isEmpty(user.getAvatar())) {
                imageView.setImageBitmap(BitmapUtils.stringtoBitmap(user.getAvatar()));
            } else {
                imageView.setImageResource(R.drawable.head_gray);
            }
        }



    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {

            EaseUser user = getUserInfo(username);
            if (user!=null&&!StringCheckHelper.isEmpty(user.getNikename())) {
                textView.setText(user.getNikename());
            } else {
                textView.setText("");
            }
        }
    }

}
