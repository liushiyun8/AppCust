package com.eke.cust.tabmine.contract;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseListActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Contract;
import com.eke.cust.model.Emp;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.activity_order.PayOrderActivity;
import com.eke.cust.tabhouse.activity_order.RefundActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.StringCheckHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.droidparts.annotation.inject.InjectResource;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foundation.notification.NotificationCenter;
import foundation.widget.imageview.CircleImageView;

import static com.eke.cust.R.style.dialog;
import static com.eke.cust.net.ServerUrl.METHOD_getEstateEmp;
import static com.eke.cust.net.ServerUrl.METHOD_getEstateEmpByTel;
import static com.eke.cust.net.ServerUrl.METHOD_updateMyContractEmp;

//房屋收藏
public class MineContractActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {

    @InjectView(id = R.id.listview)
    private PullToRefreshListView refreshListView;

    @InjectResource(value = R.array.contract_function)
    private String[] str_contracts;
    private CommonListAdapter<Contract> mListAdapter;
    private ArrayList<Contract> mItems = new ArrayList<Contract>();
    private ArrayList<Emp> emps;
    private CommonListAdapter<Emp> EmpAdapter;
    private String[] fundtion = {"详情", "继续支付", "申请退款", "专属助理"};

    private String contract_id;

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        _RefreshState = RefreshState.LS_Refresh;
        kPage = 0;
        getList(false);

    }

    //上拉加载更多
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        _RefreshState = RefreshState.LS_LoadMore;
        kPage++;
        getList(false);
    }

    //region  接口调用
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
            hideLoading();
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
                                        .equals(ServerUrl.METHOD_getMyContract)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    int totalPages = JSONUtils.getInt(obj_data, "totalPages", 0);
                                    setRefreshLoadedState(totalPages);
                                    JSONArray jsonArray_listrent_data = JSONUtils.getJSONArray(obj_data, "data", null);
                                    if (jsonArray_listrent_data != null) {
                                        mItems = JSONUtils.getObjectList(jsonArray_listrent_data, Contract.class);
                                        if (_RefreshState != RefreshState.LS_LoadMore) {
                                            initListAdapter();
                                        } else {
                                            ArrayList<Contract> mItems = JSONUtils.getObjectList(jsonArray_listrent_data, Contract.class);
                                            mListAdapter.addList(mItems);
                                        }

                                    }
                                    onComplete();
                                } else if (request_url.equals(METHOD_getEstateEmp)) {
                                    JSONArray jsonArray = JSONUtils.getJSONArray(jsonObject, "data", null);
                                    if (jsonArray != null) {
                                        emps = JSONUtils.getObjectList(jsonArray, Emp.class);
                                        showList(emps);
                                    }

                                } else if (request_url.equals(METHOD_updateMyContractEmp)) {
                                    ToastUtils.show(mContext, "设置成功");

                                } else if (request_url.equals(METHOD_getEstateEmpByTel)) {
                                    JSONArray jsonArray = JSONUtils.getJSONArray(jsonObject, "data", null);
                                    if (jsonArray != null) {
                                        emps = JSONUtils.getObjectList(jsonArray, Emp.class);
                                        if (emps.size() > 0) {
                                            EmpAdapter.notifyDataSetChanged();
                                        } else {
                                            emps.clear();
                                            ToastUtils.show(mContext, "抱歉，未检索到对应助理");
                                        }
                                        EmpAdapter.notifyDataSetChanged();

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
                            onComplete();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        onComplete();
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        onComplete();
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };
    //endregion

    private void onComplete() {
        refreshListView
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshListView
                                .onRefreshComplete();
                    }
                }, 300);


    }

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_find_house_result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的合同");
        registerLeftImageView(R.drawable.arrow_back);
        NotificationCenter.defaultCenter.addListener(NotificationKey.pay_success, this);
        refreshListView.setOnRefreshListener(this);
        setRefreshLoadedState(1);
        _RefreshState = RefreshState.LS_INIT;
        getList(true);
    }

    private void getList(boolean isShow) {
        JSONObject obj = new JSONObject();
        try {

            obj.put("currentPage", kPage);
            obj.put("pageSize", kPageSize);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getMyContract, obj.toString(), mHandler);
            clientHelper.isShowProgress(isShow);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter.removeListener(NotificationKey.pay_success, this);
    }

    //region 初始化适配器
    private void initListAdapter() {
        mListAdapter = new CommonListAdapter<Contract>(this, R.layout.item_contract, mItems) {
            @Override
            public void convert(ViewHolder viewHolder, final Contract contract, int position) {
//                "contractid": "260061EE04CC479496F2D4087D97A0D6",
//                        "estatename": "千蒲花园",
//                        "propertyno": "11000193",
//                        "propertyid": "020601214135B9B7AD8EE9D89E9613A8",
//                        "contractno": "2015163",
//                        "contractdate": 1488630865000,
//                        "trade": "出售",
//                        "empname": null,
//                        "empno": null,
//                        "price": null,
//                        "payprice": 0.0,
                //"status": "待支付"
                // 房源编号
                TextView mTxtPropertyid = viewHolder.findViewById(R.id.txt_propertyno);
                //房源信息
                TextView mTxtEstatename = viewHolder.findViewById(R.id.txt_estatename);

                //金额
                TextView tv_price = viewHolder.findViewById(R.id.txt_price);
                //合同生成
                TextView mTxtContractdate = viewHolder.findViewById(R.id.txt_contractdate);
                //支付进度
                TextView mTxtPayStatus = viewHolder.findViewById(R.id.txt_pay_state);
                TextView mTxtStatus = viewHolder.findViewById(R.id.txt_status);
                ImageView mIvHouseTag = viewHolder.findViewById(R.id.iv_house_tag);

                String propertyno = contract.propertyno;
                String estatename = contract.estatename;
                String trade = contract.trade;
                String price = !StringCheckHelper.isEmpty(contract.price) ? contract.price : "0";
                String payprice = !StringCheckHelper.isEmpty(contract.payprice) ? contract.payprice : "0";
                String contractdate = contract.contractdate;
                String status = contract.status;

                if (!StringCheckHelper.isEmpty(propertyno)) {
                    mTxtPropertyid.setText(String.format("房源编号:%s", "  " + propertyno));
                }

                if (!StringCheckHelper.isEmpty(estatename)) {
                    mTxtEstatename.setText(String.format("房        源:%s", "  " + estatename));
                }

                if (trade.contains("租")) {
                    mIvHouseTag.setImageResource(R.drawable.icon_resent_tag);
                    if (!StringCheckHelper.isEmpty(price)) {
                        tv_price.setText(String.format("金        额:%s元/月", "  " + price));
                    }
                } else {
                    //售
                    mIvHouseTag.setImageResource(R.drawable.icon_sell_tag);
                    if (!StringCheckHelper.isEmpty(price)) {
                        tv_price.setText(String.format("金        额:%s万", "  " + price));
                    }
                }

                if (!StringCheckHelper.isEmpty(contractdate)) {
                    mTxtContractdate.setText(String.format("合同生成:%s", "  " + DateUtil.getDateToString(Long.parseLong(contractdate))));
                }

                mTxtPayStatus.setText(String.format("支付进度:%s", "  " + payprice + "/" + price));
                if (!StringCheckHelper.isEmpty(status)) {
                    mTxtStatus.setText(String.format("支付状态:%s", "  " + status));
                }

            }
        };
        refreshListView.setAdapter(mListAdapter);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contract contract = (Contract) parent.getItemAtPosition(position);
                showList(contract);
            }
        });
    }

    //endregion
    //region  listview  设置
    private void setRefreshLoadedState(int totalPages) {
        refreshListView.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在加载");
        refreshListView.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        refreshListView.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("释放开始加载");
        if (kPage < totalPages) {
            refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        }


    }
    //endregion

    // region显示功能
    private void showList(final Contract contract) {
        final int imageIds[] = new int[]{R.drawable.icon_contract_detail,
                R.drawable.icon_pay, R.drawable.icon_continue,
                R.drawable.icon_advistory

        };
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < imageIds.length; i++) {
            arrayList.add(imageIds[i]);
        }
        if (arrayList.size() < 9) {
            for (int i = imageIds.length; i < 9; i++) {
                arrayList.add(-1);
            }
        }


        final Dialog dlg = new Dialog(mContext, dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(
                R.layout.dlg_tab_gridview, null);
        final GridView listview = (GridView) viewContent
                .findViewById(R.id.gridview_action);
        listview.setAdapter(new CommonListAdapter<Integer>(mContext, R.layout.layout_tab_houselist_action_list_item, arrayList) {
            @Override
            public void convert(ViewHolder holder, Integer integer, int position) {

                TextView mIvFunction = holder.findViewById(R.id.txt_action);
                TextView mTxtName = holder.findViewById(R.id.txt_name);
                if (position < imageIds.length) {
                    mIvFunction.setBackgroundResource(integer);
                    mTxtName.setText(fundtion[position]);
                }


            }

        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView mTxtFunction = (TextView) view.findViewById(R.id.txt_action);
                if (!mTxtFunction.isEnabled()) {
                    return;
                }
                dlg.cancel();
                switch (position) {
                    case 0://详情
                    {
                        UIHelper.startActivity(MineContractActivity.this, PayOrderActivity.class, contract);

                    }
                    break;

                    case 1:// 继续支付
                    {
                        UIHelper.startActivity(MineContractActivity.this, PayOrderActivity.class, contract);

                    }
                    break;
                    case 2://申请退款
                    {
                        String status = contract.status;
                        if (StringCheckHelper.isEmpty(status) || status.equals("待支付")) {
                            ToastUtils.show(mContext, "订单还未支付");
                            return;
                        }
                        UIHelper.startActivity(MineContractActivity.this, RefundActivity.class, contract);
                        break;
                    }

                    case 3:// 显示助理
                    {
                        ShowAdvistory(contract);

                    }
                    break;


                }
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
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = dm.widthPixels * 2 / 3;
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        window.setAttributes(attributes);
        dlg.show();
    }
    //endregion

    // region 显示助理
    private void ShowAdvistory(Contract contract) {
        contract_id = contract.contractid;
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", contract.propertyid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    METHOD_getEstateEmp, obj.toString(), mHandler);
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateMyContractEmp(Emp emp) {

        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("contractid", contract_id);
            obj.put("empid", emp.empid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    METHOD_updateMyContractEmp, obj.toString(), mHandler);
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showList(final ArrayList<Emp> emps) {
        final Dialog dlg = new Dialog(this, dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater
                .inflate(R.layout.dlg_contract_emp, null);
        final ListView listview = (ListView) viewContent
                .findViewById(R.id.recycleview);
        final EditText mEdtContent = (EditText) viewContent
                .findViewById(R.id.edt_content);
        final ImageView mIvSearch = (ImageView) viewContent
                .findViewById(R.id.iv_search);
        EmpAdapter = new CommonListAdapter<Emp>(this, R.layout.item_mine_assistant_manager, emps) {
            @Override
            public void convert(ViewHolder holder, Emp emp, int position) {
                CircleImageView headImage = holder.findViewById(R.id.iv_head);
                TextView mTxtName = holder.findViewById(R.id.txt_name);
                TextView mTxtEmpId = holder.findViewById(R.id.txt_number);
                TextView mTxtExperience = holder.findViewById(R.id.txt_phone_number);
                if (!StringCheckHelper.isEmpty(emp.ekeicon)) {
                    headImage.setImageBitmap(BitmapUtils.stringtoBitmap(emp.ekeicon));
                } else {
                    headImage.setImageResource(R.drawable.head_gray);
                }

                if (!StringCheckHelper.isEmpty(emp.empname)) {
                    mTxtName.setText(emp.empname);
                }
                if (!StringCheckHelper.isEmpty(emp.empno)) {
                    mTxtEmpId.setText(emp.empno);
                }
                if (!StringCheckHelper.isEmpty(emp.tel)) {
                    mTxtExperience.setText(emp.tel);
                }

            }
        };
        listview.setAdapter(EmpAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Emp emp = (Emp) parent.getItemAtPosition(position);
                updateMyContractEmp(emp);

                //设置助理
//                Intent intent = new Intent();
//                intent.setClass(MineContractActivity.this, ChatActivity.class);
//                intent.putExtra(EaseConstant.EXTRA_USER_ID, emp.empid);
//                intent.putExtra(EaseConstant.EXTRA_USER_NAME, emp.empname);
//                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
//                startActivity(intent);

                dlg.cancel();


            }
        });
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mEdtContent.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.show(mContext, "请输入手机号");
                    return;
                }
                if (!RegularUtil.isMobileNO(phone)) {
                    ToastUtils.show(mContext, "请输入正确的手机号");
                    return;
                }
                searchEmp(phone);
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

    //搜索助理
    private void searchEmp(String phone) {
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("tel", phone);
//            obj.put("ekeworkcityid", AppContext.getInstance().getAppPref().cityId());
            ClientHelper clientHelper = new ClientHelper(mContext,
                    METHOD_getEstateEmpByTel, obj.toString(), mHandler);
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //endregion
    @Override
    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.pay_success)) {
            getList(false);
            return true;
        }
        return super.onNotification(notification);
    }
}
