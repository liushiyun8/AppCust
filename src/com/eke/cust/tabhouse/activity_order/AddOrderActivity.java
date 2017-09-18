package com.eke.cust.tabhouse.activity_order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Contract;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.Order;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.utils.StringTools;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.util.intent.IntentFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class AddOrderActivity extends BaseActivity implements View.OnClickListener {
    @InjectBundleExtra(key = "data")
    private HouseSource houseSource;
    //房屋名
    @InjectView(id = R.id.txt_house_name)
    private TextView mTxtHouseName;
    //房源编号
    @InjectView(id = R.id.txt_propertyno)
    private TextView mTxtPropertyno;
    //签约助理
    @InjectView(id = R.id.txt_empname)
    private TextView mTxtEmpname;
    //房屋信息
    @InjectView(id = R.id.txt_house_info)
    private TextView mTxtHouseInfo;
    //类别
    @InjectView(id = R.id.txt_trade)
    private TextView mTxtTrade;
    //租金
    @InjectView(id = R.id.txt_zj_price)
    private TextView mTxtZjPrice;
    //首付
    @InjectView(id = R.id.txt_sf_price)
    private TextView mTxtSfPrice;
    //押金
    @InjectView(id = R.id.txt_yj_price)
    private TextView mTxtYjPrice;
    //押金
    @InjectView(id = R.id.txt_pay_price)
    private TextView mTxtPayPrice;
    //提交订单
    @InjectView(id = R.id.bt_submit_order, click = true)
    private Button mBtSubmitOrder;
    @InjectView(id = R.id.txt_pay_hint2, click = true)
    private TextView mTxtHint;
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
                                        .equals(ServerUrl.METHOD_initPropertyOrder)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    Order order = JSONUtils.getObject(obj_data, Order.class);
                                    initView(order);


                                } else if (request_url
                                        .equals(ServerUrl.METHOD_insertContract)) {
                                    String contractid = JSONUtils.getString(jsonObject, "contractid", null);
                                    if (!TextUtils.isEmpty(contractid)) {
                                        ToastUtils.show(mContext, "下单成功！");
                                        Contract contract=new Contract();
                                        contract.contractid=contractid;
                                        UIHelper.startActivity(AddOrderActivity.this, PayOrderActivity.class, contract,"订单支付");
                                        finish();
                                    }


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
        mTxtSfPrice.setText("首      付: " + order.rentfirstpay);
        mTxtYjPrice.setText("押      金: " + order.rentdepositpay);

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
        return inflateContentView(R.layout.activity_add_order);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("订单确认");
        registerLeftImageView(R.drawable.arrow_back);
        registerRightImageView(R.drawable.icon_reresh);
        mTxtHint.setText(StringTools.getColorString("#ff525252", "#99ccff", getResourceString(R.string.pay_hint_phone), getResourceString(R.string.pay_hint2)));
        getOrderInfo();
    }

    @Override
    protected void goNext() {
        super.goNext();
        getOrderInfo();
    }

    //region  添加订单
    private void addOrder() {
        // TODO Auto-generated method stub
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("propertyid", houseSource.propertyid);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_insertContract, obj.toString(), mHandler);
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

    //region  获取订单信息
    private void getOrderInfo() {
        // TODO Auto-generated method stub
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("propertyid", houseSource.propertyid);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_initPropertyOrder, obj.toString(), mHandler);
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
        if (v.getId() == R.id.bt_submit_order) {
            addOrder();
        } else if (v.getId() == R.id.txt_pay_hint2) {
            Intent intent = IntentFactory.getDialIntent("4001234567");
            startActivity(intent);

        }

    }
}
