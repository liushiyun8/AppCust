package com.eke.cust.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.utils.AbAppUtil;
import com.eke.cust.utils.MyLog;
import com.eke.cust.widget.dialog.SweetAlertDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ClientHelper {

    protected final String TAG = "ClientHelper";
    Context mContext;
    String url;
    RequestParams params;
    Handler mHandler;
    HttpUtils client;
    MyRequestClient myRestClient;
    SweetAlertDialog progressdialog;
    private boolean isshowProgress = true;
    private String message = "正在加载...";

    public ClientHelper(Context mContext, String url, String jsonStr, Handler mHandler) {
        this.mContext = mContext;
        this.url = url;
        this.params = new RequestParams("UTF-8");
        this.mHandler = mHandler;
        try {
            this.params.setBodyEntity(new StringEntity(jsonStr, "UTF-8"));
            this.params.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (this.mHandler != null)
                this.mHandler.sendEmptyMessage(Constants.TAG_EXCEPTION);
            return;
        }
        MyLog.e(TAG, "url:" + this.url + ", " + jsonStr);
        myRestClient = new MyRequestClient(mContext);
        if (isshowProgress) {
            progressdialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE).setTitleText(message);
            progressdialog.setCancelable(false);
        }
    }

    public ClientHelper(Context mContext, String url, String jsonStr, Handler mHandler, boolean isshowProgress) {
        this.mContext = mContext;
        this.url = url;
        this.params = new RequestParams("UTF-8");
        this.mHandler = mHandler;
        this.isshowProgress = isshowProgress;
        try {
            this.params.setBodyEntity(new StringEntity(jsonStr, "UTF-8"));
            this.params.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (this.mHandler != null)
                this.mHandler.sendEmptyMessage(Constants.TAG_EXCEPTION);
            return;
        }

        MyLog.e(TAG, "url: " + this.url + ", " + jsonStr);

        myRestClient = new MyRequestClient(mContext);
        if (isshowProgress) {
            progressdialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE).setTitleText(message);
            progressdialog.setCancelable(false);
        }
    }

    public ClientHelper(Context mContext, String url, RequestParams params,Handler mHandler) {
        this.mContext = mContext;
        this.url = url;
        this.params = params;
        this.mHandler = mHandler;

        myRestClient = new MyRequestClient(mContext);
        if (isshowProgress) {
            progressdialog = new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE).setTitleText(message);
            progressdialog.setCancelable(false);
        }
    }

    @SuppressWarnings("static-access")
    public void sendPost(boolean isHasToken) {
        final Message msg = new Message();
        if (!AbAppUtil.isNetworkAvailable(mContext)) {
            msg.what = Constants.NO_NETWORK;
            msg.obj = "没有连接网络!";
            if (mHandler != null)
                mHandler.sendMessage(msg);
            return;
        }
        myRestClient.Post(isHasToken?(this.url + "?token=" + AppContext.getInstance().getAppPref().userToken()):this.url,
                this.params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        MyLog.i(TAG, "返回结果是："+arg0.result);
                        if (StringUtils.isNotBlank(arg0.toString()) && StringUtils.isNotBlank(arg0.result)) {
                            msg.what = Constants.TAG_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString("request_url", url);
                            bundle.putString("resp", arg0.result);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(arg0.result);
                                String result = jsonObject.optString("result", "");
//                                if (result.equals("outLogin")) {
//                                    AppContext.getInstance().getAppPref().setUserToken("");
//                                    UIHelper.startToLogin((Activity) mContext);
//
//                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            msg.setData(bundle);
                        } else {
                            msg.what = Constants.TAG_FAIL;
                        }
                        if (isshowProgress && progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        if (mHandler != null)
                            mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException ex,String arg1) {
                        msg.what = Constants.TAG_FAIL;
                        MyLog.i(TAG, "url=" + this.getRequestUrl() + ", errorcode = " + ex.getExceptionCode());
                        if (isshowProgress && progressdialog != null) {
                            progressdialog.setTitleText("请求失败!" + ex.getExceptionCode())
                                    .setConfirmText("确认")
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                        MyLog.i(TAG, ex + ", " + arg1);
                    }
                });
    }

    @SuppressWarnings("static-access")
    public void sendGet(boolean isHasToken) {
        final Message msg = new Message();
        if (!AbAppUtil.isNetworkAvailable(mContext)) {
            msg.what = Constants.NO_NETWORK;
            msg.obj = "没有连接网络!";
            mHandler.sendMessage(msg);
            return;
        }
        if (isshowProgress) {
            progressdialog.show();
        }
        myRestClient.get(isHasToken ? (this.url + "?token=" + AppContext
                        .getInstance().getAppPref().userToken()) : this.url,
                this.params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        MyLog.i(TAG, arg0.result);
                        if (StringUtils.isNotBlank(arg0.toString())
                                && StringUtils.isNotBlank(arg0.result)) {
                            msg.what = Constants.TAG_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString("request_url", url);
                            bundle.putString("resp", arg0.result);
                            msg.setData(bundle);

                        } else {
                            msg.what = Constants.TAG_FAIL;
                        }
                        if (isshowProgress && progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        if (mHandler != null)
                            mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        msg.what = Constants.TAG_FAIL;
                        // progressdialog.dismiss();
                        if (isshowProgress && progressdialog != null) {
                            progressdialog
                                    .setTitleText("请求失败!")
                                    .setConfirmText("确认")
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                        arg0.printStackTrace();
                        MyLog.e(TAG, arg1);
                    }
                });
    }

    /* 是否显示 加载 */
    public void isShowProgress(boolean isshowProgress) {
        this.isshowProgress = isshowProgress;
    }

    /* 加载显示文字 */
    public void setShowProgressMessage(String message) {
        this.message = message;
    }

}
