package com.eke.cust.api;


import android.text.TextUtils;
import android.util.Log;

import com.eke.cust.AppContext;
import com.eke.cust.utils.*;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import app.BaseAppContext;
import foundation.enums.RestApiCode;
import foundation.util.JSONUtils;
import foundation.util.StringUtil;
import okhttp3.Call;
import okhttp3.MediaType;


public abstract class RestApi {

    // region 常量
    protected static final String Tag = "RestAPI";
    protected static final int TIMEOUT = 30000;
    protected HashMap<String, String> headers;

    protected HttpMethod _method; // 请求方法 默认post
    public String _url;
    private boolean _callCancel = false;
    protected boolean _isCancelled;
    protected boolean _isTimeout;
    protected boolean _isSuccessed;
    protected boolean _isCompleted;
    protected boolean _isNoNet;
    protected Exception _exception;

    private RequestType requestType;
    public RestApiCode code;
    public String msg;
    // endregion

    // region verbose


    protected void printResponse(String responseString) {
        Log.d("response: %s", responseString);
    }


    // endregion

    // region 构造API

    protected RestApi(String url, HttpMethod method, RequestType requestType) {
        headers = new HashMap<String, String>();
        this.requestType = requestType;
        String token = AppContext.getInstance().getAppPref().userToken();
        if (!TextUtils.isEmpty(token)) {
            _url = url + "?token=" + token;
        } else {
            _url = url;
        }
        _method = method;
        //设置全局公共参数
    }

    private String tag = "";

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
    // region 调用API

    public void call() {
        this.call(true);
    }

    public void call(boolean async) {
        this.call(async, TIMEOUT);
    }

    public void call(boolean async, final int timeout) {

        switch (_method) {
            case POST:
                if (requestType == RequestType.JsonQuest) {
                    try {
                        OkHttpUtils.postString().url(_url).content(requestJson().toString()).tag(this).headers(requestHeaders())
                                .mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(getStrijngCallBack());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (requestType == RequestType.ParmQuest) {
                    OkHttpUtils.post().url(_url).params(requestParams()).tag(this).build().execute(getStrijngCallBack());
                }
                break;
            case GET:
                if (requestType == RequestType.JsonQuest) {
                    OkHttpUtils.get().url(_url).tag(this).headers(requestHeaders()).tag(this).build().execute(getStrijngCallBack());
                } else if (requestType == RequestType.ParmQuest) {
                    OkHttpUtils.get().url(_url).tag(this).headers(requestHeaders()).tag(this).build().execute(getStrijngCallBack());
                }
                break;

            default:
                throw new RestApiException("不支持的HTTP方法");
        }


    }

    protected void onInvalidToken() {
        this.onFail();

    }

    public void cancel() {
        _callCancel = true;


    }

    public boolean isCancelled() {
        return _isCancelled;
    }

    public boolean isNoNet() {
        return _isNoNet;
    }

    public boolean isTimeout() {
        return _isTimeout;
    }

    public boolean isSuccessed() {
        return _isSuccessed;
    }

    public boolean isCompleted() {
        return _isCompleted;
    }

    public Exception exception() {
        return _exception;
    }

    // endregion

    // region 调用结果
    protected void doSuccess(JSONObject baseResponse) {
        this.onSuccess();
    }

    protected void doFailure(Exception e) {

        do {
            this._isCompleted = true;
            this._exception = e;
            if (e instanceof TimeoutException || e instanceof ConnectException
                    || e instanceof SocketTimeoutException) {
                this._isTimeout = true;
                this._exception = null;
                this.onTimeout();
            } else if (_callCancel) {
                this._isCancelled = true;
                this.onCancel();
            } else {
                this.onError(e);
            }
        } while (false);
    }

    protected void onSuccess() {
        _isCompleted = true;
        _isSuccessed = true;
        HashMap<String, String> map = new HashMap<>();
        map.put("action", this.getClass().getName());
        //友盟统计
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Success",
                map);
    }


    protected JSONObject requestJson() throws JSONException {
        return new JSONObject();
    }

    protected void onFail() {
        _isCompleted = true;
    }

    protected void onCancel() {
        _isCompleted = true;
        _isCancelled = true;
        HashMap<String, String> map = new HashMap<>();
        map.put("action", this.getClass().getName());
        //友盟统计
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Cancel",
                map);
    }

    protected void onTimeout() {
        _isCompleted = true;
        _isTimeout = true;
        HashMap<String, String> map = new HashMap<>();
        map.put("action", this.getClass().getName());
        //友盟统计
        MobclickAgent.onEvent(BaseAppContext.getInstance(), "Restful_API_Timeout",
                map);

    }

    protected void onError(final Exception e) {
        _isCompleted = true;
        _exception = e;

    }

    // endregion

    // region




    protected Map<String, String> requestHeaders() {

        return Headers.getHttpHeaders();
    }

    protected Map<String, String> requestParams() {
        return new HashMap<>();
    }


    public enum HttpMethod {
        POST, DELETE, GET, PUT, OPTIONS, HEAD, PATCH, TRACE, CONNECT
    }

    public enum RequestType {
        ParmQuest, JsonQuest
    }



    //json回调
    public StringCallback getStrijngCallBack() {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                doFailure(e);

            }
            @Override
            public void onResponse(String responseString, int i) {
                try {
                    JSONObject json = new JSONObject(responseString);
                    printResponse(responseString);
                    String result = JSONUtils.getString(json, "result", "");
                    if (JSONUtils.hasNonNull(json, "errorMsg")) {
                        RestApi.this.msg = JSONUtils.getString(json, "errorMsg", "获取成功");
                    } else {
                        RestApi.this.msg = "获取成功";
                    }
                    int code = !StringUtil.isEmpty(result) && !result.equals("error") ? 0 : 1;
                    RestApi.this.code = RestApiCode.createCode(code);
                    doSuccess(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                    doFailure(e);

                }
            }


        };
        return stringCallback;
    }

}
