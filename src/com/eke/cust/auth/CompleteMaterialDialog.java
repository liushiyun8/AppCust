package com.eke.cust.auth;

import org.droidparts.annotation.inject.InjectView;
import org.droidparts.fragment.DialogFragment;
import org.droidparts.util.ResourceUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.CountDownHelper;
import com.eke.cust.model.User;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.MD5;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.StringCheckHelper;

/**
 * 完善资料
 *
 * @author wujian
 */

public class CompleteMaterialDialog extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    //region  view 初始化
    // 手机号
    @InjectView(id = R.id.edt_phone)
    private TextView mTxtPhone;
    @InjectView(id = R.id.edt_password)
    private EditText mEdtPassword;
    @InjectView(id = R.id.edt_password1)
    private EditText mEdtPassword1;
    @InjectView(id = R.id.edt_email)
    private EditText mEdtEmail;
    @InjectView(id = R.id.edt_name)
    private EditText mEdtName;
    @InjectView(id = R.id.radio_group)
    private RadioGroup mRadioGroup;
    @InjectView(id = R.id.layout_first)
    private LinearLayout mLayoutFirst;
    @InjectView(id = R.id.layout_second)
    private LinearLayout mLayoutSecond;
    @InjectView(id = R.id.bt_next, click = true)
    private Button mBtNext;
    @InjectView(id = R.id.layout_next, click = true)
    private LinearLayout mLayouNext;
    @InjectView(id = R.id.bt_last, click = true)
    private Button mBtLast;
    @InjectView(id = R.id.bt_ok, click = true)
    private Button mBtOk;

    private User user;
    private int step;


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
                                //判断用户属性
                                if (request_url
                                        .equals(ServerUrl.METHOD_perfectedPwdEmail)) {
                                    ToastUtils.show(getActivity(), "完善成功！");
                                    CompleteMaterialDialog.this.getDialog().cancel();

                                }

                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(AppContext.getInstance(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(AppContext.getInstance(), "请求出错!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(AppContext.getInstance(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(AppContext.getInstance(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    //endregion
    @Override
    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.complete_material_dialog, null);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (step == 0) {
                        return true;
                    } else {
                        changeLayout();
                    }

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth(getActivity()) - ResourceUtils.dpToPx(getActivity(), 30), ViewGroup.LayoutParams.WRAP_CONTENT);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.radio_man);

    }

    // region onViewCreated
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
        view.findViewById(R.id.layout_forget_password).setOnClickListener(this);
        String phone = AppContext.getInstance().getAppPref().userPhone();
        if (!StringCheckHelper.isEmpty(phone)) {
            if (phone.length() > 11) {
                phone = phone.replace(phone.substring(3, 7), "****");
            }
            mTxtPhone.setText(phone);
        }
    }


    //region 按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                submitInfo();
                break;
            case R.id.bt_next:
                goNext();
                break;
            case R.id.bt_last:
                changeLayout();
                break;


            default:
                break;
        }

    }


    private void submitInfo() {
        String email = mEdtEmail.getText().toString();
        String name = mEdtName.getText().toString();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.show(getActivity(), "请输入邮箱");
        }
        if (!RegularUtil.checkEmail(email)) {
            ToastUtils.show(getActivity(), "邮箱输入不正确");
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(getActivity(), "请输入用户名");
        }
        user.setEmail(email);
        user.setName(name);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("psw", MD5.md5(user.getPassword()).toUpperCase());
            jsonObject.put("email", user.getSex());
            jsonObject.put("sex", user.getSex());
            jsonObject.put("name", user.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_perfectedPwdEmail, jsonObject.toString(), mHandler);
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    private void goNext() {
        String password = mEdtPassword.getText().toString();
        String password1 = mEdtPassword1.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(getActivity(), "请输入密码");
            return;
        }
        if (password.length() < 6 || password.length() > 12) {
            ToastUtils.show(getActivity(), "请输入5到12位密码");
            return;
        }
        if (TextUtils.isEmpty(password1)) {
            ToastUtils.show(getActivity(), "请确认密码");
            return;
        }
        if (!password.equals(password1)) {
            ToastUtils.show(getActivity(), "两次密码输入不一致");
        }
        user = new User();
        user.setPassword(password);
        changeLayout();
    }


    public void changeLayout() {
        if (step == 0) {
            step = 1;
            mLayoutFirst.setVisibility(View.VISIBLE);
            mLayoutSecond.setVisibility(View.GONE);
            mBtOk.setVisibility(View.VISIBLE);
            mLayouNext.setVisibility(View.GONE);
        } else {
            step = 0;
            mLayoutFirst.setVisibility(View.GONE);
            mLayoutSecond.setVisibility(View.VISIBLE);
            mBtOk.setVisibility(View.GONE);
            mLayouNext.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_man) {
            user.setSex("先生");
        } else if (checkedId == R.id.radio_woman) {
            user.setSex("女士");
        }

    }


}
