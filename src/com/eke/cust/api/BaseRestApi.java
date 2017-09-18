package com.eke.cust.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Looper;
import android.text.TextUtils;

import com.eke.cust.utils.MyLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;

import app.BaseAppContext;
import foundation.enums.RestApiCode;
import foundation.util.ThreadUtils;


public class BaseRestApi extends RestApi {
    protected static final String SimpleResponse = "{\"flag\":0, \"message\":\"成功\"}";
    public JSONObject responseData;


    // endregion
    public BaseRestApi(String url) {
        super(url, HttpMethod.POST, RequestType.JsonQuest);
    }

    public BaseRestApi(String url, HttpMethod httpMethod) {
        super(url, httpMethod, RequestType.ParmQuest);
    }
    public BaseRestApi(String url, HttpMethod method, RequestType callBackType) {
        super(url, method, callBackType);
    }


    // region 调用API
    protected BaseRestApiListener _listener;

    protected String encode(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void setListener(BaseRestApiListener listener) {
        _listener = listener;
    }

    public void call(boolean async, int timeout) {
        if (!async
                && Thread.currentThread() == Looper.getMainLooper()
                .getThread()) {
            throw new RestApiException("不允许在主线程中使用同步方式调用此接口");
        }

        if (isMock()) {
            if (async) {
                ThreadUtils.runOnWorkThread(new Runnable() {
                    @Override
                    public void run() {
                        mockResponse();
                    }
                });
            } else {
                mockResponse();
            }
        } else {
            super.call(async, timeout);
        }
    }

    public void call() {
        if (isMock()) {
            mockResponse();
        } else {
            this.call(true);
        }
    }

    private void mockResponse() {
        JSONObject mockData = null;
        String fileName = this.mockFile();
        if (!TextUtils.isEmpty(fileName)) {
            String data = readAssets(BaseAppContext.getInstance(), fileName);
            try {
                mockData = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mockData != null) {
            mockData = this.mockData();
        }
        if (mockData != null) {
            throw new RestApiException(this.getClass().getName() + "no mock data");
        }
        this.doSuccess(mockData);
    }



    protected void onSuccess() {
        super.onSuccess();
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Success", getInfoMap());
        if (_listener != null) {
            _listener.onSuccessed(this);
        }
    }

    protected void onCancel() {
        super.onCancel();
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Cancel", getInfoMap());
        if (_listener != null) {
            _listener.onCancelled(this);
        }
    }


    private HashMap<String, String> getInfoMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("action", this.getClass().getName());
        map.put("code", code + "");
        map.put("messageInfo", msg);
        return map;
    }

    protected void onTimeout() {
        super.onTimeout();
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Timeout", getInfoMap());
        if (_listener != null) {
            _listener.onTimeout(this);
        }
    }

    protected void onError(final Exception e) {
        super.onError(e);

        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Error", getInfoMap());
        if (e instanceof UnsupportedEncodingException) {
            this.code = RestApiCode.RestApi_Internal_UnsupportedEncodingException;
        }

        if (e instanceof JSONException) {
            this.code = RestApiCode.RestApi_Internal_JSONException;
        }

        if (e instanceof UnknownHostException) {
            this.code = RestApiCode.RestApi_Internal_UnknownHostException;
        }

        if (_listener != null) {
            _listener.onError(this, e);
        }
    }

    protected void onFail() {
        super.onFail();
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Fail", getInfoMap());

        if (_listener != null) {
            _listener.onFailed(BaseRestApi.this, code, msg);
        }
    }

    protected void doSuccess(JSONObject response) {
        if (this.code == RestApiCode.RestApi_OK) {
            this.responseData = response;
            this.onSuccess();
            MyLog.e(Tag,response.toString());
        } else if (this.code == RestApiCode.RestApi_InvalidToken) {
            //令牌无效，跳转到登录界面
            this.onInvalidToken();
        } else {
            this.onFail();
        }

    }


    // endregion

    // region mock

    protected String mockFile() {
        return null;
    }

    protected JSONObject mockData() {
        return null;
    }

    protected boolean isMock() {
        return false;
    }

    protected void mockSleep() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

    }

    // endregion

    // region inner interface

    // Rest Api 监听器
    public interface BaseRestApiListener {

        void onSuccessed(BaseRestApi object);

        void onFailed(BaseRestApi object, RestApiCode code,
                      String message);

        void onError(BaseRestApi object, Exception e);

        void onTimeout(BaseRestApi object);

        void onCancelled(BaseRestApi object);
    }

    public static String readAssets(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
