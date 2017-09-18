package com.eke.cust.launch;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.api.ApiHelper;
import com.eke.cust.api.BaseRestApi;
import com.eke.cust.api.lunch.LaunchApiHelper;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.bean.LaunchModel;
import com.eke.cust.helper.HuanXingHelper;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.main.MainActivity;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.model.UserLocation;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MD5;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONException;
import org.json.JSONObject;

import foundation.SimpleCache;
import foundation.callback.ICallback1;
import foundation.toast.ToastManager;
import foundation.toast.ToastUtil;
import foundation.util.ThreadUtils;

import static com.eke.cust.AppContancts.LAUNCHMODEL;
import static foundation.SimpleCache.TIME_DAY;

/**
 * 启动动画
 *
 * @author wujian
 */
public class WelcomeActivity extends BaseActivity implements ICallback1<BaseRestApi> {

    // 背景
    private RelativeLayout mBgLayout;

    private WebView webView;

    private AlphaAnimation animation;
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {

                    case Constants.NO_NETWORK:
                        break;

                    case Constants.TAG_SUCCESS:
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url
                                        .equals(ServerUrl.METHOD_queryCityByName)) {
                                    JSONObject objout = jsonObject
                                            .optJSONObject("data");
                                    boolean data = objout.optBoolean("data");
                                    if (data) {
                                        String cityid = JSONUtils.getString(objout,
                                                "cityid",
                                                null);
                                        AppContext.getInstance().getAppPref()
                                                .setCityId(cityid);
                                    }

                                } else if (request_url
                                        .equals(ServerUrl.METHOD_login)) {
                                    JSONObject objout = jsonObject
                                            .optJSONObject("data");
                                    CurrentUser user = JSONUtils.getObject(objout, CurrentUser.class);
                                    AppContext.getInstance().onLogin(user);
                                    HuanXingHelper.getInstance().Login(WelcomeActivity.this, new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            HuanXingHelper.getInstance().loginSuccess(getApplicationContext());

                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            ThreadUtils.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {ToastManager.manager.show("即时通讯登录失败");
                                                }
                                            });

                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });
                                }

                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "请求出错!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        queryListCity();
        mBgLayout = (RelativeLayout) findViewById(R.id.bg_layout);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        // 渐变展示启动屏
        animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);

        mBgLayout.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                UIHelper.startActivity(WelcomeActivity.this, MainActivity.class);
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        if (AppContext.getInstance().isLogin()) {
            getUserInfo();
            HuanXingHelper.getInstance().sendHeartBeats(this);
            HuanXingHelper.getInstance().loadNikeName(this);

        }
         LaunchModel launchModel = (LaunchModel) SimpleCache.get(this).getAsObject(LAUNCHMODEL);
        if (launchModel == null) {
            LaunchApiHelper.getLinkextSwitch(this);
        } else {
            webView.loadUrl(launchModel.getCoverurl());
        }

    }


    private void queryListCity() {
        JSONObject obj = new JSONObject();
        try {
            UserLocation userLocation = AppContext.getInstance().getAppPref()
                    .getUserLocation();

            if (userLocation != null && !StringCheckHelper.isEmpty(userLocation.city)) {
                obj.put("cityname", userLocation.city);
            } else {
                obj.put("cityname", "深圳");

            }
            ClientHelper clientHelper = new ClientHelper(WelcomeActivity.this,
                    ServerUrl.METHOD_queryCityByName, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在进行查询, 请稍候...");
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(false);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getUserInfo() {

        JSONObject obj = new JSONObject();
        try {
            String phone = AppContext.getInstance().getAppPref().userPhone();
            String CustType = AppContext.getInstance().getAppPref().getCustType();
            if (!StringCheckHelper.isEmpty(CustType)){
                switch (CustType) {
                    case "quality"://优质用户
                        if (!TextUtils.isEmpty(phone)){
                            String pwd = AppContext.getInstance().getAppPref().getMePassword();
                            obj.put("custtel", phone);
                            obj.put("pwd", pwd);
                            ClientHelper clientHelper = new ClientHelper(WelcomeActivity.this,
                                    ServerUrl.METHOD_login, obj.toString(), mHandler);
                            clientHelper.isShowProgress(false);
                            clientHelper.sendPost(false);
                        }
                        break;
                    default:
                        if (!TextUtils.isEmpty(phone)) {
//                            if (TextUtils.isEmpty(password)) {
//                                password = MD5.md5(phone.substring(5, phone.length())).toUpperCase();
//                            }
                            String password =AppContext.getInstance().getAppPref().userPassword();
                            obj.put("custtel", phone);
                            obj.put("pwd", password);
                            ClientHelper clientHelper = new ClientHelper(WelcomeActivity.this,
                                    ServerUrl.METHOD_login, obj.toString(), mHandler);
                            clientHelper.isShowProgress(false);
                            clientHelper.sendPost(false);
                        }
                        break;

                }
            }



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void callback(BaseRestApi baseRestApi) {
        if (ApiHelper.filterError(baseRestApi)) {
            JSONObject jsonObject = baseRestApi.responseData;
            LaunchModel launchModel = null;
            try {
                launchModel = JSONUtils.getObject(jsonObject.getJSONObject("data"), LaunchModel.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SimpleCache.get(this).put(LAUNCHMODEL, launchModel, TIME_DAY);
            if (launchModel != null) {
                webView.loadUrl(launchModel.getCoverurl());
            }

        }

    }
}