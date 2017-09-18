package com.eke.cust.tabmore.feedback_activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.StringCheckHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends BaseActivity {
    private EditText mEt_content;

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
                                if (request_url.equals(ServerUrl.METHOD_insert)) {
                                    Toast.makeText(getApplicationContext(), "发送成功!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
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
        return inflateContentView(R.layout.activity_tab_more_feedback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("意见反馈");
        registerLeftImageView(R.drawable.arrow_back);
        initActivity();

    }

    private void initActivity() {
        mEt_content = (EditText) findViewById(R.id.et_content);
    }

    public void UploadFeedback(View view) {
        String content = mEt_content.getText().toString().trim();

        if (StringCheckHelper.isEmpty(content)) {
            Toast.makeText(this, "请输入您的意见，谢谢!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length() < 6) {
            Toast.makeText(this, "反馈内容至少为6个字", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("detail", content);

            ClientHelper clientHelper = new ClientHelper(FeedbackActivity.this,
                    ServerUrl.METHOD_insert, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在发送...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
