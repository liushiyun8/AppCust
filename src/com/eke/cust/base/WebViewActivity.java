package com.eke.cust.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;



/**
 * Created by wfpb on 15/6/26.
 * WebView 父类
 */
public abstract class WebViewActivity extends BaseActivity {

    protected WebView _webView;
    private String _title;
    private boolean _firstResume = true;
    private boolean _loadSuccess = false;

    protected abstract String getUrl();

    protected void goBack() {
        finish();
    }

    protected void goNext() {
        _webView.reload();
    }

    @Override
    protected void setTitle(String title) {
        _title = title;
        super.setTitle(title);
    }

    @Override
    protected View onCreateContentView() {
        _webView = new WebView(this);
        _webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        return _webView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FIXME  设置左右按钮

        WebSettings settings = _webView.getSettings();
        settings.setJavaScriptEnabled(true);

        _webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading("正在加载页面…");
                _loadSuccess = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();

                if (_loadSuccess) {
                    if (_title != null) {
                        setTitle(view.getTitle());
                    }
                } else {
                    //ToastManager.manager.show(WebViewActivity.this, "加载页面失败, 请刷新重试");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                _loadSuccess = false;
                hideLoading();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (_firstResume) {
            _webView.loadUrl(getUrl());
            _firstResume = false;
        }
    }

    public void onBackPressed() {
        if (_webView.canGoBack()) {
            _webView.goBack();
        } else {
            goBack();
        }
    }
}
