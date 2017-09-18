package com.eke.cust.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.AbAppUtil;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MD5;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import org.droidparts.util.AppUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import foundation.notification.NotificationCenter;

/**
 * Created by wujian on 2016/8/2.
 * <p>
 * 环信帮助类
 */

public class HuanXingHelper {
    private volatile static HuanXingHelper instance;

    private static String TAG = "HuanXingHelper";

    /**
     * 单一实例
     */
    public static HuanXingHelper getInstance() {
        if (instance == null) {
            synchronized (HuanXingHelper.class) {
                if (instance == null) {
                    instance = new HuanXingHelper();
                }
            }
        }
        return instance;
    }


    public HuanXingHelper() {

    }


    /**
     * 心跳
     *
     * @param context
     */
    public void sendHeartBeats(Context context) {
        JSONObject obj = new JSONObject();
        try {
            String mobileuuid = null;
            if (AppContext.getInstance().isLogin()) {
                CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
                mobileuuid = currentUser.custid;
            } else {
                mobileuuid = AppUtils.getDeviceId(context);
            }
            obj.put("mobileuuid", mobileuuid);
            ClientHelper clientHelper = new ClientHelper(context,
                    ServerUrl.METHOD_heartbeats, obj.toString(), null,
                    false);
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Login(final Context context, EMCallBack callBack) {
        if (!AbAppUtil.isNetworkAvailable(context)) {
            ToastUtils.show(context, R.string.network_isnot_available);
            return;
        }

        if (!ChatHelper.getInstance().isLoggedIn()) {
            String phone = AppContext.getInstance().getAppPref().userPhone();
            String password = AppContext.getInstance().getAppPref().userPassword();
            CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
            ChatHelper.getInstance().setCurrentUserName(currentUser.custid);

            String CustType = AppContext.getInstance().getAppPref().getCustType();
            if (!StringCheckHelper.isEmpty(CustType)){
                switch (CustType){
                    case "quality"://优质用户
                        password = AppContext.getInstance().getAppPref().getMePassword();
                        MyLog.d(TAG, "password------------------" + password);
//                        EMClient.getInstance().login(currentUser.custid, pwd, callBack);
//
                        break;
                    default:
                        if (TextUtils.isEmpty(password)) {
                            MyLog.d(TAG, "password=======" + password);
                            password = MD5.md5(phone.substring(5, phone.length())).toUpperCase();
                        } else {
                            MyLog.d(TAG, "password+++++++++" + password);
                            password = genSecPwd(password);
                        }


                        break;
                }
                EMClient.getInstance().login(currentUser.custid, password, callBack);

            }


            MyLog.d(TAG, "custid=" + currentUser.custid);



        }


    }

    public void loginSuccess(Context context) {
        boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                AppContext.currentUserNick.trim());
        sendHeartBeats(context);
        loadNikeName(context);
        if (!updatenick) {
            Log.e("LoginActivity", "update current user nick fail");
        }

        ChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
    }


    public void loadNikeName(final Context context) {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        StringBuilder stringBuilder = new StringBuilder();
        for (EMConversation conversation : conversations.values()) {
            String username = conversation.getUserName();
            if (!username.equals(EaseConstant.EXTRA_KEFU)) {
                stringBuilder.append(username.toUpperCase() + "/");
                MyLog.d(TAG, "username=" + username);
            }

        }
        if (stringBuilder.length() == 0) {
            return;
        }
        String empid = stringBuilder.toString().substring(0, stringBuilder.toString().lastIndexOf("/"));
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", empid);
            ClientHelper clientHelper = new ClientHelper(context,
                    ServerUrl.METHOD_findEmployeeByIds, obj.toString(), new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    if (msg != null) {
                        switch (msg.what) {
                            case Constants.TAG_SUCCESS:
                                Bundle bundle = msg.getData();
                                String request_url = bundle.getString("request_url");
                                String resp = bundle.getString("resp");
                                try {
                                    JSONObject jsonObject = new JSONObject(resp);
                                    String result = jsonObject.optString("result", "");
                                    if (result.equals(Constants.RESULT_SUCCESS)) {
                                        if (request_url
                                                .equals(ServerUrl.METHOD_findEmployeeByIds)) {
                                            JSONArray array_data = jsonObject
                                                    .optJSONArray("data");
                                            if (array_data != null) {
                                                for (int i = 0; i < array_data.length(); i++) {

                                                    JSONObject object = array_data
                                                            .getJSONObject(i);
                                                    if (object != null) {
                                                        String empid = JSONUtils.getString(object, "empid");
                                                        String ekenickname = JSONUtils.getString(object, "ekenickname", "");
                                                        String ekeicon = JSONUtils.getString(object, "ekeicon", "");
                                                        EaseUser easeUser = new EaseUser(empid);
                                                        if (StringCheckHelper.isEmpty(ekeicon)) {
                                                            ekeicon = BitmapUtils.bitmapToBase64(BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(context, R.drawable.default_avatar)));
                                                        }
                                                        if (StringCheckHelper.isEmpty(ekenickname)) {
                                                            ekenickname = "暂无";
                                                        }
                                                        easeUser.setAvatar(ekeicon);
                                                        easeUser.setNikeName(ekenickname);
                                                        ChatHelper.getInstance().saveContact(easeUser);
                                                        NotificationCenter.defaultCenter.postNotification(NotificationKey.update_conversation);
                                                    }
                                                }


                                            }
                                        }
                                    } else if (result.equals(Constants.RESULT_ERROR)) {


                                        MyLog.d(TAG, "会话信息请求出错");
                                    }

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();

                                    MyLog.d(TAG, "会话信息请求出错" + e.getMessage());

                                }


                                break;
                        }

                    }

                    super.dispatchMessage(msg);
                }
            });
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int getContactSize() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        return list.size();
    }


    public static String genSecPwd(String orig) {
        String md51 = MD5.md5(orig).toUpperCase();
        StringBuilder sbBuilder = new StringBuilder();
        sbBuilder.append(md51.substring(0, 24));
        sbBuilder.append("ABC");
        sbBuilder.append(md51.substring(27));
        String pwd = MD5.md5(sbBuilder.toString()).toUpperCase();
        return pwd;
    }


}
