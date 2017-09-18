package com.eke.cust;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.chat.ConversationActivity;
import com.eke.cust.helper.HuanXingHelper;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabmine.collect.MineCollectActivity;
import com.eke.cust.tabmine.contract.MineContractActivity;
import com.eke.cust.tabmine.profile_activity.ProfileActivity;
import com.eke.cust.tabmore.house_register_activity.house_add.HouseHistoryActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import foundation.base.fragment.BaseFragment;
import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;
import foundation.status.StatusBarUtil;
import foundation.toast.ToastUtil;
import foundation.widget.imageview.CircleImageView;

public class FragmentMenuMine extends BaseFragment implements OnClickListener {
    private static final String TAG = "FragmentMenuMine";
    //头像
    @InjectView(id = R.id.iv_head)
    private CircleImageView mIvHead;
    //名字
    @InjectView(id = R.id.tv_name)
    private TextView mTxtName;
    //合同
    @InjectView(id = R.id.txt_contractNum)
    private TextView mTxtContractNum;
    //收藏
    @InjectView(id = R.id.txt_collectNum)
    private TextView mTxtCollectNum;
    //业主
    @InjectView(id = R.id.txt_owerNum)
    private TextView mTxtOwerNum;
    @InjectView(id = R.id.txt_empname_message)
    private TextView mTxtEmpnameMessage;


    //region 接口调用
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

                        if (request_url.equals(ServerUrl.METHOD_initCustCenter)) {
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                String result = jsonObject.optString("result", "");
                                if (result.equals(Constants.RESULT_SUCCESS)) {
                                    JSONObject obj_data = jsonObject.getJSONObject("data");
                                    int collectNum = JSONUtils.getInt(obj_data, "collectNum", 0);
                                    int owerNum = JSONUtils.getInt(obj_data, "owerNum", 0);
                                    int contractNum = JSONUtils.getInt(obj_data, "contractNum", 0);
                                    mTxtContractNum.setText("" + contractNum);
                                    mTxtCollectNum.setText("" + collectNum);
                                    mTxtOwerNum.setText("" + owerNum);
                                }

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(mContext.getApplicationContext(),
                                        "请求出错!", Toast.LENGTH_SHORT).show();
                            }

                        } else if (request_url.equals(ServerUrl.METHOD_EmployeeIcon)) {

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

    //endregion
    @Override
    protected View onCreateContentView() {
        View view = inflateContentView(R.layout.fragment_menu_mine);
        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    //region  初始化 view
    private void initView(View view) {

        view.findViewById(R.id.layout_contract).setOnClickListener(this);
        view.findViewById(R.id.rl_head).setOnClickListener(this);
        view.findViewById(R.id.iv_head).setOnClickListener(this);
        view.findViewById(R.id.layout_collect).setOnClickListener(this);
        view.findViewById(R.id.layout_owerNum).setOnClickListener(this);
        view.findViewById(R.id.layout_empname).setOnClickListener(this);

    }
//endregion

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NotificationCenter.defaultCenter.addListener(NotificationKey.update_user_info, this);
//        if (AppContext.getInstance().isLogin()) {
//            JSONObject obj = new JSONObject();
//            ClientHelper clientHelper = new ClientHelper(mContext,
//                    ServerUrl.METHOD_initCustCenter, obj.toString(), mHandler);
//            clientHelper.isShowProgress(false);
//            clientHelper.setShowProgressMessage("正在获取数据...");
//            clientHelper.sendPost(true);
//            if (ChatHelper.getInstance().getContactList() != null) {
//                mTxtEmpnameMessage.setText("" + ChatHelper.getInstance().getContactList().size());
//            }
//        }

        setTitle("我的");
        initUserInfo();

    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();

        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(mContext,
                ServerUrl.METHOD_initCustCenter, obj.toString(), mHandler);
        clientHelper.isShowProgress(!this.isHidden());
        clientHelper.setShowProgressMessage("正在获取数据...");
        clientHelper.sendPost(true);
        mTxtEmpnameMessage.setText("" + HuanXingHelper.getInstance().getContactSize());

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //region 初始化用户信息
    private void initUserInfo() {
        CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
        if (currentUser != null) {
            String nickname = currentUser.nickname;
            String ekeicon = currentUser.ekeicon;
//            ToastUtil.showToast(currentUser.custname );
            mTxtName.setText(!StringCheckHelper.isEmpty(nickname) ? nickname : "");

            if (!StringCheckHelper.isEmpty(ekeicon)) {
                mIvHead.setImageBitmap(BitmapUtils.stringtoBitmap(ekeicon));
            } else {
                mIvHead.setImageResource(R.drawable.head_gray);
            }
        }
    }

    //endregion


    //region 点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //我的资料
            case R.id.rl_head:
            case R.id.iv_head: {
                UIHelper.startActivity(getActivity(), ProfileActivity.class);
            }
            break;
            //我的合同
            case R.id.layout_contract: {
                UIHelper.startActivity(getActivity(), MineContractActivity.class);
            }
            break;
            //房源收藏
            case R.id.layout_collect: {
                UIHelper.startActivity(getActivity(), MineCollectActivity.class);
            }
            break;

            //我是业主
            case R.id.layout_owerNum: {
                UIHelper.startActivity(getActivity(), HouseHistoryActivity.class);

            }
            break;

            //助理消息
            case R.id.layout_empname: {
                UIHelper.startActivity(getActivity(), ConversationActivity.class);
            }
            break;

        }
    }

    //endregion
    @Override
    public boolean onNotification(NotificationListener.Notification notification) {
        if (notification.key.equals(NotificationKey.update_user_info)) {
            initUserInfo();
            return true;
        }
        return super.onNotification(notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter.removeListener(NotificationKey.update_user_info, this);
    }

//    loadImage();
//        NotificationCenter.defaultCenter.addListener(NotificationKey.update_user_head, this);
}