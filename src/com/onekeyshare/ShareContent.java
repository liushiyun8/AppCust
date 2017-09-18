package com.onekeyshare;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.utils.BitmapUtils;
import com.parse.twitter.Twitter;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by wujian on 16/8/11.
 */

public class ShareContent implements ShareContentCustomizeCallback {
    private Bitmap bitmap;
    public  ShareContent( Bitmap bitmap){
        this.bitmap=bitmap;

    }
    @Override
    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
        if (Wechat.NAME.equals(platform.getName()) || WechatMoments.NAME.equals(platform.getName())) {

            paramsToShare.setImageData(bitmap);
            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);

        }

    }
}
