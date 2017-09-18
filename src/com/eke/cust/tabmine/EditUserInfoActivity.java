package com.eke.cust.tabmine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.enums.StartType;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import foundation.notification.NotificationCenter;

/**
 * 编辑用户信息
 */
public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener {
    @InjectBundleExtra(key = "startType")
    private StartType _startType;
    @InjectView(id = R.id.edit_content)
    private EditText mEdtContent;
    @InjectView(id = R.id.tv_save, click = true)
    private TextView mTxtSave;
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
                        if (request_url.equals(ServerUrl.METHOD_updateEkecust)) {
                            CurrentUser user = AppContext.getInstance().getAppPref().getUser();
                            if (_startType == StartType.SetNikeName) {
                                user.nickname = mEdtContent.getText().toString();
                            } else if (_startType == StartType.SetEmail) {
                                user.email = mEdtContent.getText().toString();
                            } else if (_startType == StartType.SetAboutMe) {
                                user.sign = mEdtContent.getText().toString();
                            }
                            AppContext.getInstance().getAppPref().setUser(user);
                            NotificationCenter.defaultCenter.postNotification(NotificationKey.update_user_info);
                            finish();
                            ToastUtils.show(mContext, "修改成功!");
                        }


                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };


    public static void startActivity(Context context, StartType startType) {
        Intent intent = new Intent(context, EditUserInfoActivity.class);
        intent.putExtra("startType", startType);
        context.startActivity(intent);

    }


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_edit_user_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLeftImageView(R.drawable.arrow_back);
        CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
        if (_startType == StartType.SetNikeName) {
            setTitle("昵称");
            mEdtContent.setHint("请输入昵称");
            if (currentUser != null) {
                String nickname = currentUser.nickname;
                if (!StringCheckHelper.isEmpty(nickname)) {
                    mEdtContent.setText(nickname);
                }
            }
            mEdtContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        } else if (_startType == StartType.SetEmail) {
            setTitle("安全邮箱");
            if (currentUser != null) {
                String email = currentUser.email;
                if (!StringCheckHelper.isEmpty(email)) {
                    mEdtContent.setText(email);
                }
            }
            mEdtContent.setHint("请输入邮箱地址");
            mEdtContent.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        } else if (_startType == StartType.SetAboutMe) {
            setTitle("个人签名");
            if (currentUser != null) {
                String sign = currentUser.sign;
                if (!StringCheckHelper.isEmpty(sign)) {
                    mEdtContent.setText(sign);
                }
            }
            mEdtContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            mEdtContent.setHint("请输入签名");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTxtSave) {
            submitInfo();
        }

    }

    private void submitInfo() {
        JSONObject obj = new JSONObject();
        String content = mEdtContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            if (_startType == StartType.SetNikeName) {
                ToastUtils.show(mContext, "请输入昵称");
            } else if (_startType == StartType.SetEmail) {
                ToastUtils.show(mContext, "请输入邮箱");
            } else if (_startType == StartType.SetAboutMe) {
                ToastUtils.show(mContext, "请输入签名");

            }
            return;
        } else {
            try {
                if (_startType == StartType.SetNikeName) {
                    obj.put("nickname", content);
                } else if (_startType == StartType.SetEmail) {
                    if (!RegularUtil.checkEmail(content)) {
                        ToastUtils.show(mContext, "请输入正确的邮箱");
                        return;
                    }
                    obj.put("email", content);

                } else if (_startType == StartType.SetAboutMe) {
                    obj.put("sign", content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ClientHelper clientHelper = new ClientHelper(mContext, ServerUrl.METHOD_updateEkecust, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在提交...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }
}
