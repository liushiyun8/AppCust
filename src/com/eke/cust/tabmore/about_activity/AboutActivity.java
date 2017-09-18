package com.eke.cust.tabmore.about_activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 关于页面
 *
 * @author: lichunyu
 * @since: 2017-08-24 17:26
 */
public class AboutActivity extends BaseActivity {
    private static final String TAG = "MyAboutActivity";
    /* @InjectView(id = R.id.webView)*/
    private WebView webView;
    private String url = null;

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
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
                            url = jsonObject.optString("data", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_getAboutUs1)) {
                                    /*if (url != null && !url.contains("http")) {
                                        "http://".concat(url);
                                    }*/
                                    webView.loadUrl(url);
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
        return inflateContentView(R.layout.activity_tab_more_about);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        registerLeftImageView(R.drawable.arrow_back);
        initData();
    }

    public String getVersion(){
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    private void initData() {
        ((TextView)this.findViewById(R.id.version)).setText("当前版本：V"+getVersion());
        webView = (WebView) this.findViewById(R.id.webView);

        loadData();
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true); // 设置显示缩放按钮
        setting.setSupportZoom(true); // 支持缩放

        setting.setAllowFileAccess(true); // 允许访问文件
        setting.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口

        setting.setDisplayZoomControls(false);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//支持内容重新布局
        setting.setUseWideViewPort(true);//将图片调整到适合webview的大小
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }

        /*webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });*/
    }


    private void loadData() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("paramname", "AppCust_About");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ClientHelper clientHelper = new ClientHelper(AboutActivity.this, ServerUrl.METHOD_getAboutUs1, obj.toString(), handler);
                clientHelper.setShowProgressMessage("正在获取数据...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(false);
            }
        }, 200);
    }

}
