package com.eke.cust.auth;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.CountDownHelper;
import com.eke.cust.helper.HuanXingHelper;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MD5;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMCallBack;

import org.droidparts.annotation.inject.InjectView;
import org.droidparts.fragment.DialogFragment;
import org.droidparts.util.ResourceUtils;
import org.json.JSONException;
import org.json.JSONObject;

import foundation.notification.NotificationCenter;
import foundation.toast.ToastManager;
import foundation.toast.ToastUtil;
import foundation.util.ThreadUtils;

import static org.droidparts.Injector.getApplicationContext;

/**
 * 登录界面
 *
 * @author wujian
 */

public class LoginDialog extends DialogFragment implements View.OnClickListener {
    //region  view 初始化
    // 手机号
    @InjectView(id = R.id.edt_phone)
    private EditText mEdtPhone;
    // 手机验证码
    @InjectView(id = R.id.layout_message_code)
    private LinearLayout mLayoutMessageCode;
    @InjectView(id = R.id.edt_message_code)
    private EditText mEdtMessageCode;
    @InjectView(id = R.id.txt_send_message_code, click = true)
    private TextView mTxtMessageCode;
    @InjectView(id = R.id.bt_login, click = true)
    private Button mBtLogin;
    @InjectView(id = R.id.bt_forget_password, click = true)
    private Button mBtForgetPassword;

    //密码
    @InjectView(id = R.id.layout_password)
    private LinearLayout mLayoutPassword;
    @InjectView(id = R.id.edt_password)
    private EditText mEdtPassword;
    //endregion
    //"逻辑见图visitor  不存在 register 存在 ，默认密码没改 quality 存在，已完善资料"
    private String custType;
    CountDownHelper helper;
    //region  接口调用
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
                                switch (request_url){
                                    //判断用户属性
                                    case ServerUrl.METHOD_checkCustType:
                                        custType = JSONUtils.getString(jsonObject, "custType", null);
                                        //保存用户类型
                                        AppContext.getInstance().getAppPref().setCustType(custType);


//                                    custType = "quality";
                                        if (custType.equals("visitor") || custType.equals("register")) {
                                            //普通
                                            mLayoutMessageCode.setVisibility(View.VISIBLE);
                                            mLayoutPassword.setVisibility(View.GONE);
                                        } else if (custType.equals("quality")) {
                                            //优质用户
                                            mLayoutPassword.setVisibility(View.VISIBLE);
                                            mLayoutMessageCode.setVisibility(View.GONE);
                                        }
                                        break;
                                    case ServerUrl.METHOD_sendPhoneCode:
                                        //验证码发送成功
                                        ToastUtil.showToast("验证码已发送");
                                        break;
                                    //用户登录成功  优质用户 普通用户
                                    case ServerUrl.METHOD_checkPhoneCode://普通用户
                                        JSONObject data = JSONUtils.getJSONObject(jsonObject, "data", null);
                                        if (data != null) {
                                            CurrentUser user = JSONUtils.getObject(data, CurrentUser.class);
                                            AppContext.getInstance().onLogin(user);

                                            HuanXingHelper.getInstance().Login(getActivity(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    LoginDialog.this.getDialog().cancel();
                                                    NotificationCenter.defaultCenter.postNotification(NotificationKey.update_login);
                                                    HuanXingHelper.getInstance().loginSuccess(getApplicationContext());
                                                    ToastUtil.showToast("登录成功");
                                                    if (helper != null) {
                                                        helper.stop();

                                                    }
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

                                        break;
                                    case  ServerUrl.METHOD_login: //优质用户密码登录
                                        JSONObject data1 = JSONUtils.getJSONObject(jsonObject, "data", null);
                                        if (data1 != null) {
                                            CurrentUser user = JSONUtils.getObject(data1, CurrentUser.class);
                                            AppContext.getInstance().onLogin(user);
////                                            //优质用户密码
                                            String phone = mEdtPhone.getText().toString().trim();
                                            String password = mEdtPassword.getText().toString().trim();
                                            //存入密码
                                            password = MD5.md5(password).toUpperCase();
                                            AppContext.getInstance().getAppPref().setMePassword(password) ;
                                            HuanXingHelper.getInstance().Login(getActivity(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    LoginDialog.this.getDialog().cancel();
                                                    NotificationCenter.defaultCenter.postNotification(NotificationKey.update_login);
                                                    HuanXingHelper.getInstance().loginSuccess(getApplicationContext());

                                                    ToastUtil.showToast("登录成功");
                                                    if (helper != null) {
                                                        helper.stop();

                                                    }
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

                                        break;
                                    //优质用户找回密码
                                    case ServerUrl.METHOD_findCustPwd:
                                        JSONObject data2 = JSONUtils.getJSONObject(jsonObject, "data", null);
                                        if (data2 != null) {
                                            CurrentUser user = JSONUtils.getObject(data2, CurrentUser.class);
                                            AppContext.getInstance().onLogin(user);
////                                            //优质用户密码
                                            String phone = mEdtPhone.getText().toString().trim();
                                            String password = mEdtPassword.getText().toString().trim();
                                            //存入密码
                                            password = MD5.md5(password).toUpperCase();
                                            AppContext.getInstance().getAppPref().setMePassword(password) ;
                                            HuanXingHelper.getInstance().Login(getActivity(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    LoginDialog.this.getDialog().cancel();
                                                    NotificationCenter.defaultCenter.postNotification(NotificationKey.update_login);
                                                    HuanXingHelper.getInstance().loginSuccess(getApplicationContext());

                                                    ToastUtil.showToast("登录成功");
                                                    if (helper != null) {
                                                        helper.stop();

                                                    }
                                                }

                                                @Override
                                                public void onError(int i, String s) {
                                                    ThreadUtils.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastManager.manager.show("即时通讯登录失败");
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                        }
                                        break;



                                }
//                                if (request_url.equals(ServerUrl.METHOD_login)) {
//                                    JSONObject objout = jsonObject.optJSONObject("data");
//                                    CurrentUser user = JSONUtils.getObject(objout, CurrentUser.class);
//                                    AppContext.getInstance().onLogin(user);
//                                AppContext.getInstance().getAppPref().setUserPassword(mEdtPassword.getText().toString());
//
//                                }

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
    private boolean isCheck = false;
    private String PhoneNumber = "";

    //endregion
    @Override
    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.login_dialog, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth(getActivity()) - ResourceUtils.dpToPx(getActivity(), 30), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    // region onViewCreated
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.bt_login).setOnClickListener(this);
        getDialog().setCanceledOnTouchOutside(false);
        view.findViewById(R.id.layout_forget_password).setOnClickListener(this);
        view.findViewById(R.id.txt_send_message_code).setOnClickListener(this);
        mLayoutMessageCode.setVisibility(View.GONE);
        mLayoutPassword.setVisibility(View.GONE);

        //根据手机号输入判断是注册还是优质客户
        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (RegularUtil.isMobileNO(phone)) {
                    mBtLogin.setEnabled(true);
                    //如果是手机号检查
                    checkCustType(phone);
                    PhoneNumber = phone;
                } else {
                    if (isCheck){
                        isCheck = false;
                        helper.stop();
                        helper = null;
                        mTxtMessageCode.setEnabled(true);
                        mTxtMessageCode.setText("验证码");
                    }
                    mLayoutMessageCode.setVisibility(View.GONE);
                    mLayoutPassword.setVisibility(View.GONE);
                    mBtLogin.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
//endregion

    // region 检查用户属性
    public void checkCustType(String phone) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNumber", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_checkCustType, jsonObject.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    //endregion
    //region 按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_forget_password: //忘记密码登录
                findCustPwd();
                break;
            case R.id.bt_login: //普通登录
                if (!StringCheckHelper.isEmpty(custType))
                    switch (custType){
                        case "quality"://优质用户
                            loginByPassword();//优质用户
                            break;
                        default:
                            checkPhoneCode(); //普通用户
                            break;
                    }
                break;
            case R.id.txt_send_message_code:
                //FIXME 验证码
                sendMessageCode();

                break;
            case R.id.layout_forget_password:
                //FIXME 忘记密码
                mTxtMessageCode.setBackgroundResource(R.drawable.shape_get_password_message_code);
                mBtLogin.setVisibility(View.GONE);
                mBtForgetPassword.setVisibility(View.VISIBLE);
                mLayoutMessageCode.setVisibility(View.VISIBLE);
                mBtForgetPassword.setEnabled(true);
                break;

            default:
                break;
        }

    }

    //找回密码登录
    private void findCustPwd() {

        JSONObject jsonObject = new JSONObject();
        String phone = mEdtPhone.getText().toString().trim();
        String phoneCode = mEdtMessageCode.getText().toString().trim();
        String Password  =  mEdtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(AppContext.getInstance(), "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            ToastUtils.show(AppContext.getInstance(), "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(phoneCode)) {
            ToastUtils.show(AppContext.getInstance(), "请输入短信验证码");
            return;
        }
//        if (!TextUtils.isEmpty(Password)){
//            Password = MD5.md5(phone.substring(5, phone.length())+Password).toUpperCase();
//        }
        Password = MD5.md5(Password).toUpperCase();

        try {
            jsonObject.put("custtel", phone);
            jsonObject.put("pwd", Password);
            jsonObject.put("phonecode", phoneCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_findCustPwd, jsonObject.toString(), mHandler);
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    //endregion
    // region 发送短信验证码
    private void sendMessageCode() {
        JSONObject jsonObject = new JSONObject();
        String phone = mEdtPhone.getText().toString();
        try {
            jsonObject.put("phoneNumber", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_sendPhoneCode, jsonObject.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);

        isCheck = true;
        helper = new CountDownHelper(mTxtMessageCode,
                "验证码","确定", 90, 1);

        helper.setOnFinishListener(new CountDownHelper.OnFinishListener() {

            @Override
            public void finish() {
                mTxtMessageCode.setText("验证码");
            }
        });
        helper.start();
    }

    //endregion
    // region用户登录
    private void checkPhoneCode() {
        JSONObject jsonObject = new JSONObject();
        String phone = mEdtPhone.getText().toString();
        String phoneCode = mEdtMessageCode.getText().toString();
        if (TextUtils.isEmpty(phoneCode)) {
            ToastUtils.show(AppContext.getInstance(), "请输入短信验证码");
            return;
        }
        try {
            jsonObject.put("phoneNumber", phone);
            jsonObject.put("phoneCode", phoneCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_checkPhoneCode, jsonObject.toString(), mHandler);
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    //endregion
    //endregion
    // region用户 优质用户
    private void loginByPassword() {
        JSONObject jsonObject = new JSONObject();
        String phone = mEdtPhone.getText().toString();
        String password = mEdtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(AppContext.getInstance(), "请输入密码");
            return;
        }

        password = MD5.md5(password).toUpperCase();
        try {
            jsonObject.put("custtel", phone);
            jsonObject.put("pwd", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_login, jsonObject.toString(), mHandler);
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    //endregion
}
