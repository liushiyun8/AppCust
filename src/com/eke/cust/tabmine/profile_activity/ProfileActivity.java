package com.eke.cust.tabmine.profile_activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.enums.StartType;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabmine.EditUserInfoActivity;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivity;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivityNew;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.LoadImageUtil;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMCallBack;

import org.droidparts.annotation.inject.InjectView;

import foundation.notification.NotificationCenter;
import foundation.widget.imageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements OnClickListener {
    //个人头像
    @InjectView(id = R.id.iv_head, click = true)
    private CircleImageView mIvHead;

    //昵称
    @InjectView(id = R.id.tv_name)
    private TextView mTxtName;
    //注册时间
    @InjectView(id = R.id.tv_zhuceshijian)
    private TextView mTxtRegisterDate;
    //手机
    @InjectView(id = R.id.tv_phone)
    private TextView mTxtPhone;

    //昵称
    @InjectView(id = R.id.layout_nike_name, click = true)
    private LinearLayout mLayoutNikeName;
    @InjectView(id = R.id.tv_name)
    private TextView mTxtNikeName;

    //邮箱
    @InjectView(id = R.id.rl_email, click = true)
    private LinearLayout mLayoutEmail;
    @InjectView(id = R.id.tv_email)
    private TextView mTxtEmail;

    //个人签名

    @InjectView(id = R.id.rl_about_me, click = true)
    private LinearLayout mLayoutAboutMe;
    @InjectView(id = R.id.tv_about_me)
    private TextView mTxtSign;

    @InjectView(id = R.id.tv_quit, click = true)
    private TextView mTextView_tv_quit;

    private DialogUtil mDialogUtil;


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_tab_mine_profile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("个人资料");
        registerLeftImageView(R.drawable.arrow_back);

        findViewById(R.id.rl_bg).setOnClickListener(this);

        loadImage();
    }

    private void initUserInfo() {
        CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
        if (currentUser != null) {
            String nickname = currentUser.nickname;
            String regdate = currentUser.regdate;
            String ekeicon = currentUser.ekeicon;
            String custtel = currentUser.custtel;
            String email = currentUser.email;
            String sign = currentUser.sign;
            if (!StringCheckHelper.isEmpty(ekeicon)) {
                mIvHead.setImageBitmap(BitmapUtils.stringtoBitmap(ekeicon));
            } else {
                mIvHead.setImageResource(R.drawable.head_gray);
            }
            if (!StringCheckHelper.isEmpty(nickname)) {
                mTxtName.setText(nickname);
            }
            if (!StringCheckHelper.isEmpty(regdate)) {
                mTxtRegisterDate.setText(DateUtil.getDateToString1(Long.parseLong(regdate)));
            }
            if (!StringCheckHelper.isEmpty(custtel)) {
                if (custtel.length() >= 11) {
                    custtel = custtel.replace(custtel.substring(3, 7), "****");
                }
                mTxtPhone.setText(custtel);
            }


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showLogoutDlg() {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater.inflate(R.layout.dialog_logout, null);

        Button btn_left = (Button) viewContent.findViewById(R.id.btn_left);
        Button btn_right = (Button) viewContent.findViewById(R.id.btn_right);

        btn_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mDialogUtil = new DialogUtil(ProfileActivity.this);
                mDialogUtil.setIsCanCancelAble(false);
                mDialogUtil.setProgressMessage("正在退出...");
                mDialogUtil.showProgressDialog();
                ChatHelper.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        mDialogUtil.dissmissProgressDialog();
                        AppContext.getInstance().loginout();
                        NotificationCenter.defaultCenter.postNotification(NotificationKey.update_login);
                        finish();
                    }

                    @Override
                    public void onProgress(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "退出失败!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btn_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels * 2 / 3;
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        dlg.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_quit: {
                showLogoutDlg();
            }
            break;

            case R.id.layout_nike_name: {
                //昵称
                EditUserInfoActivity.startActivity(ProfileActivity.this, StartType.SetNikeName);

            }
            break;

            case R.id.iv_head:
                //{
//                UIHelper.startShowImage(this);
//            }
//            break;
            case R.id.rl_bg: {
                Intent intent = new Intent(ProfileActivity.this, LocalImagePreviewActivityNew.class);
                intent.putExtra("from_where", LocalImagePreviewActivityNew.FROM_PROFILE_HEAD_SETUP);
                startActivity(intent);
            }
            break;
            case R.id.rl_email: {
                //邮箱
                EditUserInfoActivity.startActivity(ProfileActivity.this, StartType.SetEmail);
            }
            break;
            case R.id.rl_about_me: {
                //签名
                EditUserInfoActivity.startActivity(ProfileActivity.this, StartType.SetAboutMe);
            }
            break;

            default:
                break;
        }
    }

    @Override
    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.update_user_head)) {
            loadImage();
            return true;
        }
        return false;
    }

    private void loadImage() {
        String path =  ServerUrl.BASE_URL + ServerUrl.METHOD_EmployeeIcon + "?token=" + AppContext.getInstance().getAppPref().userToken();
//        String path = ServerUrl.BASE_URL + ServerUrl.METHOD_EmployeeIconShow + AppContext.getInstance().getAppPref().userPhone();
        LoadImageUtil.getInstance().clear(path);
        LoadImageUtil.getInstance().displayImage(path,
                mIvHead);
    }
}
