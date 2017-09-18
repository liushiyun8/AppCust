package com.eke.cust.tabmine;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.OperatingHouseActivity;
import com.eke.cust.utils.MyLog;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import foundation.base.fragment.BaseFragment;
import foundation.notification.NotificationCenter;

/**
 * 委托
 *
 * @author wujian
 */
public class EntrustFragment extends BaseFragment {
    @InjectView(id = R.id.nav_bar_right_txt)
    private TextView mTxtRight;
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
                                if (request_url
                                        .equals(ServerUrl.METHOD_addOwnerapplyreg)) {
                                    Toast.makeText(mContext.getApplicationContext(),
                                            "提交成功！", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(mContext.getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(mContext.getApplicationContext(), "出错!",
                                    Toast.LENGTH_SHORT).show();
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

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.fragment_entrust);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 电话委托
        getView().findViewById(R.id.bt_phone_register).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showHelp();


                    }
                });
        getView().findViewById(R.id.bt_user_register).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (AppContext.getInstance().isLogin()) {
                            UIHelper.startActivity(getActivity(), OperatingHouseActivity.class);

                        } else {

                            UIHelper.startToLogin(getActivity());

                        }

                    }
                });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle("租售委托");
        registerLeftImageView(R.drawable.arrow_back);
        NotificationCenter.defaultCenter.addListener(NotificationKey.update_login, this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter.removeListener(NotificationKey.update_login, this);

    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        showButton();

    }

    private void showButton() {
        if (AppContext.getInstance().isLogin()) {
            mTxtRight.setVisibility(View.GONE);
        } else {
            setRightTitle("登录");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MyLog.d(TAG, "onResume");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MyLog.d(TAG, "onHiddenChanged");
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MyLog.d(TAG, "onVisible");

    }

    @Override
    protected void goNext() {
        super.goNext();
        UIHelper.startToLogin(getActivity());

    }


    private void showHelp() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dialog_dengji, null);

        Button btn_left = (Button) viewContent.findViewById(R.id.btn_left);
        Button btn_right = (Button) viewContent.findViewById(R.id.btn_right);


        btn_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    addOwnerapplyreg();
                    dlg.dismiss();
                } else {
                    UIHelper.startToLogin(getActivity());
                }

            }
        });

        btn_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels * 2 / 3;
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void addOwnerapplyreg() {
        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(mContext,
                ServerUrl.METHOD_addOwnerapplyreg, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在获取房源数据...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    @Override
    protected void goBack() {
        getActivity().finish();
    }

    @Override
    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.update_login)) {
            MyLog.d(TAG, "onNotification");
            showButton();
            return true;
        }
        return super.onNotification(notification);
    }
}
