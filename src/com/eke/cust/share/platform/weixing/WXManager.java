package com.eke.cust.share.platform.weixing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;


import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.share.TPManager;
import com.eke.cust.share.bean.WXShareContent;
import com.eke.cust.share.listener.StateListener;
import com.eke.cust.share.utils.ImageUtils;
import com.eke.cust.share.utils.WXUtil;
import com.eke.cust.utils.BitmapUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * Created by lujun on 2015/9/6.
 */
public class WXManager {

    private static StateListener<String> mListener;

    private static final String SCOPE = "snsapi_userinfo";
    private static final String STATE = "com.eke.cust_wechat_login";

    private Context mContext;
    private static IWXAPI mAPI;
    private static String appId;

    public WXManager(Context context) {
        this.mContext = context;
        appId = TPManager.getInstance().getWXAppId();
        mAPI = WXAPIFactory.createWXAPI(context, appId, true);
    }

    /**
     * 微信登录
     */
    public void onLoginWithWX() {
        judegeWX();
        mAPI.registerApp(appId);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE;
        req.state = STATE;
        mAPI.sendReq(req);
    }

    /**
     * WXShareContent
     *
     * @param content
     */
    public void share(WXShareContent content) {
        judegeWX();
        mAPI.registerApp(appId);
        WXMediaMessage msg = new WXMediaMessage();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        switch (content.getType()) {
            case Text:
                shareText(content, msg, req);
                break;

            case Image:
                sharePicture(content, msg, req);
                break;
            case WebPage:
                shareWebPage(content, msg, req);
                break;

            case Music:
                shareMusic(content, msg, req);
                break;

            case Video:
                shareVideo(content, msg, req);
                break;

            case Appdata:
                shareAppData(content, msg, req);
                break;

            case Emoji:

                break;

            default:
                break;
        }
    }

    /**
     * share AppData only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void shareAppData(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXAppExtendObject wxAppExtendObject = new WXAppExtendObject();
        wxAppExtendObject.fileData = WXUtil.readFromFile(content.getApp_data_path(), 0, -1);
        wxAppExtendObject.extInfo = "this is ext info";
        msg.mediaObject = wxAppExtendObject;
        msg.title = content.getTitle();
        msg.description = content.getDescription();
        msg.setThumbImage(content.getShareImageBitmap());
        msg.setThumbImage(WXUtil.extractThumbNail(content.getApp_data_path(), 150, 150, true));
        req.transaction = buildTransaction("appdata");
        req.message = msg;
        req.scene = content.getScene();
        mAPI.sendReq(req);
    }

    /**
     * share music only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void shareMusic(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXMusicObject wxMusicObject = new WXMusicObject();
        wxMusicObject.musicUrl = content.getMusic_url();
        msg.mediaObject = wxMusicObject;
        msg.title = content.getTitle();
        msg.description = content.getDescription();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = content.getScene();
        shareAsync(content.getImage_url(), req, true);
    }

    /**
     * share video only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void shareVideo(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXVideoObject wxVideoObject = new WXVideoObject();
        wxVideoObject.videoUrl = content.getVideo_url();
        msg.mediaObject = wxVideoObject;
        msg.title = content.getTitle();
        msg.description = content.getDescription();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = content.getScene();
        shareAsync(content.getImage_url(), req, true);
    }

    /**
     * share picture only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void sharePicture(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXImageObject wxImageObject = new WXImageObject();
        msg.mediaObject = wxImageObject;
        msg.title = content.getTitle();
        msg.description = content.getText();
        req.transaction = buildTransaction("image");
        req.message = msg;
        req.scene = content.getScene();
        shareAsync(content.getImage_url(), req, false);
    }

    private void shareLocalPicture(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXImageObject imgObj = new WXImageObject();
        msg.mediaObject = imgObj;
        req.transaction = buildTransaction("image");
        req.message = msg;
        req.scene = content.getScene();
        sendShare(content.getShareImageBitmap(), req);
    }

    /**
     * share webPage only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void shareWebPage(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = content.getWeb_url();
        msg.mediaObject = wxWebpageObject;
        msg.title = content.getTitle();
        msg.description = content.getDescription();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = content.getScene();
        shareAsync(content.getImage_url(), req, true);
    }

    /**
     * share text only
     *
     * @param content
     * @param msg
     * @param req
     */
    private void shareText(WXShareContent content, WXMediaMessage msg, SendMessageToWX.Req req) {
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = content.getText();
        msg.mediaObject = wxTextObject;
        msg.description = content.getText();
        req.transaction = buildTransaction("text");
        msg.setThumbImage(content.getShareImageBitmap());
        req.message = msg;
        //发送的目标场景， 可以选择发送到会话 WXSceneSession 或者朋友圈 WXSceneTimeline。 默认发送到会话。
        //WXSceneTimeline, WXSceneSession
        req.scene = content.getScene();
        mAPI.sendReq(req);
    }

    private static final int THUMB_SIZE = 116;

    /**
     * 异步分享
     *
     * @param imageUrl
     * @param req
     */
    private void shareAsync(final String imageUrl, final SendMessageToWX.Req req, final boolean isNeedScale) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (TextUtils.isEmpty(imageUrl)) {
                    bitmap = BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(AppContext.getInstance(), R.drawable.icon_share_logo));
                } else {
                    bitmap= WXUtil.getBitmapFromUrl(imageUrl);

                }
                if (bitmap != null&&!bitmap.isRecycled()) {
                    if (isNeedScale) {
                        req.message.thumbData = WXUtil.bmpToByteArray(
                                WXUtil.scaleCenterCrop(bitmap, THUMB_SIZE, THUMB_SIZE), true);
                    } else {
                        req.message.mediaObject = new WXImageObject(bitmap);
                        req.message.thumbData = WXUtil.bmpToByteArray(
                                WXUtil.scaleCenterCrop(bitmap, THUMB_SIZE, THUMB_SIZE), true);
                    }
                }
                mAPI.sendReq(req);
                bitmap.recycle();

            }
        }).start();
    }

    private void sendShare(final Bitmap bitmap, final SendMessageToWX.Req req) {
        Bitmap image = bitmap;
        if (image != null) {
            // todo image.length <= 10485760
            if (req.message.mediaObject instanceof WXImageObject) {
                req.message.mediaObject = new WXImageObject(image);
            }
            req.message.thumbData = ImageUtils.bmpToByteArray(ImageUtils.scaleCenterCrop(image, THUMB_SIZE, THUMB_SIZE));
        }
        // 就算图片没有了 尽量能发出分享
        mAPI.sendReq(req);
    }


    /**
     * set state listener
     *
     * @param listener
     */
    public void setListener(StateListener<String> listener) {
        this.mListener = listener;
    }

    /**
     * 检测判断合法性
     */
    private void judegeWX() {
        if (mAPI == null) {
            if (mListener != null) {
                mListener.onError("createWXAPI error!");
            }
            return;
        }
        if (!mAPI.isWXAppInstalled()) {
            if (mListener != null) {
                mListener.onError("WeChat is not install!");
            }
            return;
        }
    }

    /**
     * get IWXAPI
     *
     * @return
     */
    public static IWXAPI getWXAPI() {
        return mAPI;
    }

    /**
     * get StateListener
     *
     * @return
     */
    public static StateListener<String> getStateListener() {
        return mListener;
    }

    /**
     * build transaction
     *
     * @param type
     * @return
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
