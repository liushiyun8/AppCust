package com.eke.cust.tabmore.barcode_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

import com.eke.cust.net.ServerUrl;
import com.hyphenate.util.DensityUtil;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 微信公众账号
 */
public class WechatActivity extends BaseActivity {
    @InjectView(id= R.id.iv_barcode)
    private ImageView mIv_barcode;
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
                                if (request_url.equals(ServerUrl.METHOD_getAboutUsUrl)) {
                                    JSONObject obj_data = jsonObject.getJSONObject("data");
                                    String APPEmpUrl = obj_data.optString("APPEmpUrl", "");

                                    Bitmap qrcodeBitmap = QRCodeUtil.createQRImage(APPEmpUrl, DensityUtil.dip2px(WechatActivity.this, 360),
                                            DensityUtil.dip2px(WechatActivity.this, 360), BitmapFactory.decodeResource(getResources(), R.drawable.icon_wechat_code), null);
                                    if (qrcodeBitmap != null) {
                                        mIv_barcode.setImageBitmap(qrcodeBitmap);
                                    }
                                }
//
                            }
                            else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_wechat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLeftImageView(R.drawable.arrow_back);
        setTitle("微信公众账号");
        Bitmap qrcodeBitmap = QRCodeUtil.createQRImage("12345", DensityUtil.dip2px(WechatActivity.this, 360),
                DensityUtil.dip2px(WechatActivity.this, 360), BitmapFactory.decodeResource(getResources(), R.drawable.icon_wechat_code), null);
        if (qrcodeBitmap != null) {
            mIv_barcode.setImageBitmap(qrcodeBitmap);
        }
//        JSONObject obj = new JSONObject();
//        ClientHelper clientHelper = new ClientHelper(WechatActivity.this,
//                ServerUrl.METHOD_getAboutUsUrl, obj.toString(), mHandler);
//        clientHelper.setShowProgressMessage("正在获取数据...");
//        clientHelper.isShowProgress(true);
//        clientHelper.sendPost(true);

    }
}
