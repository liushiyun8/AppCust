package com.eke.cust.tabhouse.activity_order;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.enums.PayType;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Contract;
import com.eke.cust.model.Order;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.pay.alipay.Alipay;
import com.eke.cust.pay.alipay.server.AlipayServer;
import com.eke.cust.pay.wechat.WXPay;
import com.eke.cust.share.TPManager;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import foundation.notification.NotificationCenter;


/**
 * 选择支付
 */
public class PayOrderActivity extends BaseActivity implements View.OnClickListener {

    @InjectBundleExtra(key = "data")
    private Contract contract;
    @InjectBundleExtra(key = "data1")
    private String title;

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
//    //租金
//    @InjectView(id = R.id.txt_zj_price)
//    private TextView mTxtZjPrice;

    //押金
    @InjectView(id = R.id.txt_yj_price)
    private TextView mTxtYjPrice;

    //押金
    @InjectView(id = R.id.txt_other_price)
    private TextView mTxtOtherPrice;
    //已支付
    @InjectView(id = R.id.pay_price)
    private TextView mTxtPayPrice;
    //支付金额
    @InjectView(id = R.id.edt_price)
    private EditText mEdtPayPrice;
    //微信支付
    @InjectView(id = R.id.pay_weixin, click = true)
    private TextView mTxtPayWeiXin;
    //支付宝
    @InjectView(id = R.id.pay_alipay, click = true)
    private TextView mTxtPayAlipay;
    //支付宝
    @InjectView(id = R.id.txt_cancle, click = true)
    private TextView mTxtCanclePay;


    private PayType payType;

    private Order order;
    /**
     * 替换自己的支付宝回调地址
     */
    private String alipaycallback = "www.ekeae.com";
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
                                    order = JSONUtils.getObject(obj_data, Order.class);
                                    initView(order);


                                } else if (request_url
                                        .equals(ServerUrl.METHOD_startPay)) {

                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    doWeiXin(obj_data);


                                } else if (request_url
                                        .equals(ServerUrl.METHOD_getPayNo)) {

                                    String out_trade_no = JSONUtils.getString(jsonObject, "out_trade_no");
                                    doAlipay(out_trade_no);


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
//                mTxtZjPrice.setText("售      价: " + order.price + "万");
                mTxtPayPrice.setText("" + order.price + "万");

            } else {
//                mTxtZjPrice.setText("租     金: " + order.price + "元/月");
                mTxtPayPrice.setText("" + (order.rentdepositpay + order.rentdepositpay) + "元");

            }
        }
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
        return inflateContentView(R.layout.activity_pay_order);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(title);
        registerLeftImageView(R.drawable.arrow_back);
        registerRightImageView(R.drawable.icon_reresh);
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
                obj.put("contractid", contract.contractid);
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
            case R.id.pay_weixin:
                buildOrder("weixin");
                payType = PayType.WEIXINGPAY;
                break;
            case R.id.pay_alipay:
//                doAlipay(getOutTradeNo());
                startAliPay();
                break;
            case R.id.txt_cancle:
                finish();
                break;
        }

    }

    //支付类型  "tradeway";
    private void buildOrder(String tradeway) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            String payfee = mEdtPayPrice.getText().toString().trim();

            if (TextUtils.isEmpty(payfee)) {
                ToastUtils.show(mContext, "请输入价格");
                return;
            }
//            if (Integer.parseInt(payfee) <= 0) {
//                ToastUtils.show(mContext, "价格不能为0");
//                return;
//            }
            try {
                obj.put("contractid", contract.contractid);
                obj.put("tradeway", tradeway);
                obj.put("payfee", payfee);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_startPay, obj.toString(), mHandler);
                clientHelper.setShowProgressMessage("提交...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(true);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    //支付类型  "tradeway";
    private void doWeiXin(JSONObject jsonObject) {
        JSONObject paramJson = new JSONObject();
        try {
            String appId = TPManager.getInstance().getWXAppId();
            String prepayId = JSONUtils.getString(jsonObject, "prepay_id", "");
            String sign = JSONUtils.getString(jsonObject, "sign", "");
            String timestamp = JSONUtils.getString(jsonObject, "timestamp", "");
            String noncestr = JSONUtils.getString(jsonObject, "noncestr", "");
            paramJson.put("prepayid", prepayId);
            paramJson.put("sign", sign);
            paramJson.put("timestamp", timestamp);
            paramJson.put("partnerid", WXPay.PARTNERID);
            paramJson.put("noncestr", noncestr);
            paramJson.put("appid", appId);
            paramJson.put("package", WXPay.PACKAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String wx_appid = TPManager.getInstance().getWXAppId();     //替换为自己的appid
        WXPay.init(getApplicationContext(), wx_appid);      //要在支付前调用
        WXPay.getInstance().doPay(paramJson.toString(), new WXPay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                ToastUtils.show(getApplication(), "支付成功");
                NotificationCenter.defaultCenter.postNotification(NotificationKey.pay_success);
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case WXPay.NO_OR_LOW_WX:
                        ToastUtils.show(getApplication(), "未安装微信或微信版本过低");
                        break;

                    case WXPay.ERROR_PAY_PARAM:
                        ToastUtils.show(getApplication(), "参数错误");
                        break;

                    case WXPay.ERROR_PAY:
                        ToastUtils.show(getApplication(), "支付失败");
                        break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startAliPay() {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            String payfee = mEdtPayPrice.getText().toString().trim();
            if (TextUtils.isEmpty(payfee)) {
                ToastUtils.show(mContext, "请输入价格");
                return;
            }
//            if (Integer.parseInt(payfee) <= 0) {
//                ToastUtils.show(mContext, "价格不能为0");
//                return;
//            }
            try {
                obj.put("contractid", contract.contractid);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_getPayNo, obj.toString(), mHandler);
                clientHelper.setShowProgressMessage("提交...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(true);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void doAlipay(String obj_data) {
        //在服务器生成订单信息
        String price = mEdtPayPrice.getText().toString();

        String payInfo = AlipayServer.getPayInfo(obj_data, order.propertyno, order.propertyno, price, ServerUrl.METHOD_alipaynotify);

        //客户端调起支付宝支付
        Alipay alipay = new Alipay(PayOrderActivity.this, payInfo, new Alipay.AlipayResultCallBack() {
            @Override
            public void onSuccess() {

                ToastUtils.show(mContext,"支付成功!");
               Log.d(TAG, "支付成功");

            }

            @Override
            public void onDealing() {
                Log.d(TAG, "支付中");
            }

            @Override
            public void onError(int error_code) {
                Log.d(TAG, "支付错误" + error_code);
                ToastUtils.show(mContext,"支付错误!");

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "支付取消");
                ToastUtils.show(mContext,"支付取消!");

            }
        });
        alipay.doPay();
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }


}
