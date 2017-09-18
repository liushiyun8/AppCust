package com.eke.cust.api;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import app.BaseAppContext;
import foundation.callback.ICallback1;
import foundation.enums.RestApiCode;
import foundation.toast.ToastManager;
import foundation.util.StringUtil;
import foundation.util.ThreadUtils;
import okhttp3.OkHttpClient;

import static foundation.enums.RestApiCode.RestApi_Unknown;


/**
 * Created by wu_jian on 2015/6/19.
 */
public class ApiHelper {
    public static final String TAG = "api";
    //region  初始化，必须初始化
    public static void initHttpClient(Application context) {
        //必须调用初始化

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .hostnameVerifier(new HostnameVerifier()
                {
                    @Override
                    public boolean verify(String hostname, SSLSession session)
                    {
                        return true;
                    }
                })
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数

    }
    //endregion
    public static <T extends BaseRestApi> void callApi(final T apiObject, final ICallback1<T> callback) {
        if (!isConnected(BaseAppContext.getInstance())) {
            callback.callback(apiObject);
            ToastManager.manager.show(R.string.tip_network_error);
            return;
        }
        apiObject.call();
        apiObject.setListener(new BaseRestApi.BaseRestApiListener() {
            @Override
            public void onSuccessed(BaseRestApi object) {
                callback.callback(apiObject);
            }

            @Override
            public void onFailed(BaseRestApi object, RestApiCode code, String message) {
                callback.callback(apiObject);
                ToastManager.manager.show(BaseAppContext.getInstance(), message);
            }

            @Override
            public void onError(BaseRestApi object, Exception e) {
                callback.callback(apiObject);
                ToastManager.manager.show(BaseAppContext.getInstance(), e.getMessage().toString());

            }

            @Override
            public void onTimeout(BaseRestApi object) {
                callback.callback(apiObject);
                ToastManager.manager.show(BaseAppContext.getInstance(), R.string.api_timeout);
            }

            @Override
            public void onCancelled(BaseRestApi object) {
                callback.callback(apiObject);
                ToastManager.manager.show(BaseAppContext.getInstance(), R.string.api_cancle);
            }
        });

    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }



    public static boolean filterError(BaseRestApi apiObject) {
        return filterError(apiObject, apiObject.msg);
    }


    public static boolean filterError(final BaseRestApi apiObject, final String message) {

        if (apiObject.isTimeout()) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastManager.manager.show(AppContext.getInstance(), R.string.api_timeout);
                }
            });
            return false;
        } else if (apiObject.isCancelled()) {
            return false;
        } else if (apiObject.exception() != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastManager.manager.show(AppContext.getInstance(), R.string.api_error);
                }
            });
            return false;
        } else if (apiObject.isSuccessed()) {
            return true;
        } else if (apiObject.code == RestApi_Unknown) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastManager.manager.show(AppContext.getInstance(), message);
                }
            });
        }

        return false;

    }


    public static boolean filterError(final BaseRestApi apiObject, final String message, final boolean showToast) {

        if (apiObject.isTimeout()) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastManager.manager.show(AppContext.getInstance(), R.string.api_timeout);
                }
            });
            return false;
        } else if (apiObject.isCancelled()) {
            return false;
        } else if (apiObject.exception() != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastManager.manager.show(AppContext.getInstance(), R.string.api_error);
                }
            });
            return false;
        } else if (apiObject.isSuccessed()) {
            return true;
        } else {
            if (apiObject.code != RestApiCode.RestApi_UnCompatible) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showToast) {
                            ToastManager.manager.show(AppContext.getInstance(), !StringUtil.isEmpty(message) ? message : AppContext.getInstance().getString(R.string.api_error));

                        }
                    }
                });
            }
            return false;
        }
    }



}
