package com.eke.cust.share.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.share.bean.WXShareContent;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.onekeyshare.OnekeyShare;
import com.onekeyshare.OnekeyShareTheme;
import com.onekeyshare.ShareContent;


/**
 * Created by wujian on 2016/8/3.
 */

public class ShareHelper {

    public void share() {
        WXShareContent contentWX = new WXShareContent();
        contentWX.setScene(WXShareContent.WXSession)
                .setType(WXShareContent.share_type.Text)
                .setText("This is TPShareLogin test, 4 weixin!");
        //分享Webpage类型，微信分享需要缩略图的其他类型图片都会被压缩至32k以内
                /*contentWX.setScene(WXShareContent.WXSession)
                        .setType(WXShareContent.share_type.WebPage)
                        .setWeb_url("http://lujun.co")
                        .setTitle("WebTitle")
                        .setDescription("Web description, description, description")
                        .setImage_url("http://lujun-wordpress.stor.sinaapp.com/uploads/2014/09/lujun-375x500.jpg");*/
        //分享Image类型
                /*contentWX.setScene(WXShareContent.WXSession)
                        .setType(WXShareContent.share_type.Image)
                        .setImage_url("http://lujun-wordpress.stor.sinaapp.com/uploads/2014/09/lujun-375x500.jpg");*/
        //分享Video类型
                /*contentWX.setScene(WXShareContent.WXSession)
                        .setType(WXShareContent.share_type.Video)
                        .setVideo_url("http://lujun.co")
                        .setTitle("VideoTitle")
                        .setDescription("Video description, description, description")
                        .setImage_url("http://lujun-wordpress.stor.sinaapp.com/uploads/2014/09/lujun-375x500.jpg");*/
        //分享Music类型
                /*contentWX.setScene(WXShareContent.WXSession)
                        .setType(WXShareContent.share_type.Music)
                        .setMusic_url("http://lujun.co")
                        .setTitle("MusicTitle")
                        .setDescription("Music description, description, description")
                        .setImage_url("http://lujun-wordpress.stor.sinaapp.com/uploads/2014/09/lujun-375x500.jpg");*/
        //分享Appdata类型
//                contentWX.setScene(WXShareContent.WXSession)
//                        .setType(WXShareContent.share_type.Appdata)
//                        .setTitle("AppdataTitle")
//                        .setDescription("Appdata description, description, description")
//                        .setImage_url("http://lujun-wordpress.stor.sinaapp.com/uploads/2014/09/lujun-375x500.jpg")
//                        .setApp_data_path(Environment.getExternalStorageDirectory() + "/1234321.png");
    }

    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare 指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     */
    public static void showShareHouse(Context context, String platformToShare, String title, String content, int index, String url) {
        OnekeyShare oks = new OnekeyShare();
        Bitmap bitmap=null;
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        oks.setTitle(title);
        oks.setText(content);
        switch (index){
            case 0:
                bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_house1));
                break;
            case 1:
            	bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_house2));

                break;
            case 2:
                bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_house3));
                break;
            case 3:
                bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_house4));
                break;
            case 4:
                bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_house5));
                break;
            case 5:
                bitmap=BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.icon_default));
                break;

        }
        oks.setShareContentCustomizeCallback(new ShareContent(bitmap));
        oks.setUrl(url); //微信不绕过审核分享链接
        // 启动分享
        oks.show(context);
    }

    public static void showShareApp(Context context, String platformToShare) {
        OnekeyShare oks = new OnekeyShare();
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        oks.setTitle("[待命名]租房神器!深圳、广州租金减半?还一对一全城带看?快来~");
        oks.setText("[待命名]租房神器!深圳、广州租金减半?还一对一全城带看?快来~");
        oks.setUrl("http://www.ekeae.com"); //微信不绕过审核分享链接
        oks.setShareContentCustomizeCallback(new ShareContent(BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(AppContext.getInstance(), R.drawable.icon_share_logo))));
        // 启动分享
        oks.show(context);
    }
}
