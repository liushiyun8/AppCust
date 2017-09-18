package com.eke.cust.tabhouse.activity_order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Contract;
import com.eke.cust.model.Order;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.utils.StringTools;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectResource;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.util.intent.IntentFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * 申请退款
 */
public class RefundActivity extends BaseActivity implements View.OnClickListener {


    @InjectBundleExtra(key = "data")
    private Contract contract;

    //房屋名
    @InjectView(id = R.id.txt_house_name)
    private TextView mTxtHouseName;
    //签约助理
    @InjectView(id = R.id.txt_empname)
    private TextView mTxtEmpname;
    //房源编号
    @InjectView(id = R.id.txt_propertyno)
    private TextView mTxtPropertyno;

    //房屋信息
    @InjectView(id = R.id.txt_house_info)
    private TextView mTxtHouseInfo;
    //类别
    @InjectView(id = R.id.txt_trade)
    private TextView mTxtTrade;
    //租金
    @InjectView(id = R.id.txt_zj_price)
    private TextView mTxtZjPrice;
    //已付金额
    @InjectView(id = R.id.txt_pay_price)
    private TextView mTxtPayPrice;
    //申请退款金额
    @InjectView(id = R.id.txt_reund_price)
    private TextView mTxtReundPrice;
    //退款原因
    @InjectView(id = R.id.refund_cause, click = true)
    private TextView mTxtReundCause;
    //申请退款
    @InjectView(id = R.id.txt_reund, click = true)
    private TextView mTxtReund;
    //申请退款
    @InjectView(id = R.id.txt_back, click = true)
    private TextView mTxtBack;
    @InjectView(id = R.id.txt_pay_hint2, click = true)
    private TextView mTxtHint;
    //退款原因
    @InjectResource(value = R.array.cause)
    private String[] cause;
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
                                        .equals(ServerUrl.METHOD_getOrderData)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    Order order = JSONUtils.getObject(obj_data, Order.class);
                                    initView(order);


                                } else if (request_url
                                        .equals(ServerUrl.METHOD_updateContractTK)) {

                                    ToastUtils.show(mContext, "申请成功！");

                                    finish();


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


    //初始化界面
    private void initView(Order order) {

        String cityname = order.cityname;
        String districtname = order.districtname;
        String estatename = order.estatename;
        StringBuilder address = new StringBuilder();
        if (!StringCheckHelper.isEmpty(cityname)) {
            address.append(cityname);
        }
        if (!StringCheckHelper.isEmpty(districtname)) {
            address.append(districtname);
        }
        if (!StringCheckHelper.isEmpty(estatename)) {
            address.append(estatename);
        }
        mTxtHouseName.setText(String.format("楼盘地址:%s", address.toString()));
        String propertyno = order.propertyno;
        if (!StringCheckHelper.isEmpty(propertyno)) {
            mTxtPropertyno.setText("房源编号: " + propertyno);
        }
        String empno = order.empno;
        if (!StringCheckHelper.isEmpty(empno)) {
            mTxtEmpname.setText("签约助理: " + empno);
        }
        String trade = order.trade;
        if (!StringCheckHelper.isEmpty(trade)) {
            mTxtTrade.setText("类       别: " + trade);
        }

        if (!StringCheckHelper.isEmpty(trade)) {
            if (trade.equals("出售")) {
                mTxtZjPrice.setText("售      价: " + order.price + "万");
                mTxtPayPrice.setText("" + order.price + "万");

            } else {
                mTxtZjPrice.setText("租     金: " + order.price + "元/月");
                mTxtPayPrice.setText("" + (order.rentdepositpay + order.rentdepositpay) + "元");

            }
        }


        StringBuilder houseDetail = new StringBuilder();
        houseDetail.append(order.countf + "房");
        houseDetail.append(order.countt + "厅");
        if (!StringCheckHelper.isEmpty(order.square)) {
            houseDetail.append(order.square + "平米");
        }
        mTxtHouseInfo.setText("房源信息: " + houseDetail.toString());

    }


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_refund);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("申请退款");
        registerLeftImageView(R.drawable.arrow_back);
        registerRightImageView(R.drawable.icon_reresh);
        mTxtReundCause.setText(cause[0]);
        mTxtHint.setText(StringTools.getColorString("#ff525252", "#99ccff", getResourceString(R.string.pay_hint_phone), getResourceString(R.string.pay_hint2)));
        getOrderInfo();
    }

    @Override
    protected void goNext() {
        super.goNext();
        getOrderInfo();
    }


    //region  获取订单信息
    private void getOrderInfo() {
        // TODO Auto-generated method stub
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("contractid", contract.contractid);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_getOrderData, obj.toString(), mHandler);
                clientHelper.setShowProgressMessage("提交...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(true);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    //endregion

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_pay_hint2:
                Intent intent = IntentFactory.getDialIntent("4001234567");
                startActivity(intent);
                break;
            case R.id.refund_cause:
                //
                showCause();

                break;
            case R.id.txt_reund:
                //申请退款
                String password=AppContext.getInstance().getAppPref().userPassword();
                if(TextUtils.isEmpty(password)){
                    UIHelper.showComplete(this);
                }else{
                    submitRenud();
                }


                break;
            case R.id.txt_back:
                finish();

                break;
        }

    }

    //region  申请退款
    private void submitRenud() {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("contractid", contract.contractid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_updateContractTK, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("提交...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        }

    }
    //endregion

    // region 退款原因
    private void showCause() {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater
                .inflate(R.layout.dlg_contract, null);
        final ListView listview = (ListView) viewContent
                .findViewById(R.id.recycleview);
        listview.setAdapter(new CommonListAdapter<String>(this, R.layout.layout_tab_house_action_list_item, Arrays.asList(cause)) {
            @Override
            public void convert(ViewHolder holder, String string, int position) {
                TextView mTxtName = holder.findViewById(R.id.tv_action);
                mTxtName.setText(string);
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String cause = (String) parent.getItemAtPosition(position);
                mTxtReundCause.setText(cause);
                dlg.cancel();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                }
                return false;
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(this, 80);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }
    //endregion
}
