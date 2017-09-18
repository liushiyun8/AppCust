package com.eke.cust.tabmore.house_register_activity.house_add;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Ekefeature;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.Picture;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.share.utils.ShareHelper;
import com.eke.cust.tabhouse.HouseDetailActivity;
import com.eke.cust.tabhouse.upload_activity.UploadActivity;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.widget.BottomView;

import org.droidparts.annotation.inject.InjectResource;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.util.intent.IntentFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import foundation.widget.imageview.CheckableImageView;

public class HouseHistoryActivity extends BaseActivity {
    @InjectView(id = R.id.listview_history)
    private ListView mListView;
    private List<HouseHistory> mHouseHistories = new ArrayList<HouseHistory>();
    private CommonListAdapter<HouseHistory> houseAdapter;


    private int mItemWidth = 0;
    // 条目背景移动开始位置
    private int startX = 0;

    private CommonListAdapter<Picture> cmmonListAdapter;

    private int imageIndex;

    @InjectResource(value = R.array.fundtion)
    private String fundtions[];
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
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_getMyAllProperty)) {
                                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                                    mHouseHistories = JSONUtils.getObjectList(jsonArray, HouseHistory.class);
                                    initAdapter(mHouseHistories);

                                } else if (request_url.equals(ServerUrl.METHOD_updatePropertyDJ)
                                        || request_url.equals(ServerUrl.METHOD_findPropertyDJ)) {
                                    finish();

                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(
                                        HouseHistoryActivity.this
                                                .getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(
                                    HouseHistoryActivity.this
                                            .getApplicationContext(),
                                    "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(
                                HouseHistoryActivity.this.getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(
                                HouseHistoryActivity.this.getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };
    //endregion

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_house_history);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitle("我是业主");
        registerLeftImageView(R.drawable.arrow_back);
        refreshDate();

    }


    private void initAdapter(List<HouseHistory> mHouseHistories) {
        houseAdapter = new CommonListAdapter<HouseHistory>(
                getApplicationContext(), R.layout.item_house_history,
                mHouseHistories) {

            @Override
            public void convert(ViewHolder holder, HouseHistory houseHistory,
                                int position) {
                // TODO Auto-generated method stub
                TextView mTxtHouseNo = holder.findViewById(R.id.tv_house_no);
                TextView mTxtTime = holder.findViewById(R.id.tv_time);
                TextView mTxtInfo = holder.findViewById(R.id.tv_info);
                TextView mTxtType = holder.findViewById(R.id.tv_type);
                TextView mTxtPrice = holder.findViewById(R.id.tv_price);
                TextView mTxtStatus = holder.findViewById(R.id.tv_status);
                TextView mTxtRemark = holder.findViewById(R.id.tv_remark);
                ImageView img_1 = holder.findViewById(R.id.img_1);
                ImageView img_2 = holder.findViewById(R.id.img_2);

                mTxtHouseNo.setText(houseHistory.propertyno);

                if (!TextUtils.isEmpty(houseHistory.trade)){
                    switch (houseHistory.trade){
                        case "出租":
                            mTxtPrice.setText(houseHistory.rentprice+"元/月");
                            break;
                        case "出售":
                            mTxtPrice.setText(houseHistory.price+"万元");
                            break;
                    }
                }
                mTxtType.setText(houseHistory.countf+"室"+houseHistory.countt+"厅"+houseHistory.countw+"卫"+houseHistory.county+"阳台"
                +"  "+houseHistory.square+"平" + "  "+houseHistory.propertydirection);

                mTxtInfo.setText(houseHistory.buildingname+houseHistory.cellname+houseHistory.roomno);
                mTxtStatus.setText(houseHistory.status);
                mTxtTime.setText(houseHistory.estatename);


                if (TextUtils.equals(houseHistory.flagnoneagencyrent,"true")){
                    img_1.setVisibility( View.GONE );
                }else {
                    img_1.setVisibility( View.VISIBLE );
                }

                //FIXME  房源编号
                //FIXME  带电查询
//                if (!StringCheckHelper.isEmpty(houseHistory.propertyno)) {
//                    mTxtHouseNo.setText("房源编号:" + houseHistory.propertyno);
//                }
//
//                if (!StringCheckHelper.isEmpty(houseHistory.regdate)) {
//                    mTxtTime.setText("登记日期:" + DateUtil.getDateToString1(Long.parseLong(houseHistory.regdate)));
//                }
//                StringBuilder houseDetail = new StringBuilder();
//                if (!StringCheckHelper.isEmpty(houseHistory.estatename)) {
//                    houseDetail.append(houseHistory.estatename);
//                }
//
//                if (!StringCheckHelper.isEmpty(houseHistory.roomno)) {
//                    houseDetail.append(houseHistory.roomno + "栋");
//                }
//
//                mTxtInfo.setText("位置信息: " + houseDetail.toString());
//
//                if (!StringCheckHelper.isEmpty(houseHistory.trade)) {
//                    mTxtType.setText("性        质:" + houseHistory.trade);
//                }
//                StringBuilder stringBuilder = new StringBuilder();
//                if (houseHistory.trade.contains("租")) {
//                    if (!StringCheckHelper.isEmpty(houseHistory.rentprice)) {
//                        stringBuilder.append(houseHistory.rentprice);
//                    }
//                    if (!StringCheckHelper.isEmpty(houseHistory.rentunitname)) {
//                        stringBuilder.append(houseHistory.rentunitname + "   ");
//                    }
//                } else {
//                    if (!StringCheckHelper.isEmpty(houseHistory.price)) {
//                        stringBuilder.append(houseHistory.price);
//                    }
//                    if (!StringCheckHelper.isEmpty(houseHistory.unitname)) {
//                        stringBuilder.append(houseHistory.unitname + "   ");
//                    }
//
//                }
//
//                if (!StringCheckHelper.isEmpty(houseHistory.status)) {
//                    stringBuilder.append(houseHistory.status);
//                }
//                mTxtPrice.setText(stringBuilder.toString());
//
//
//                if (!StringCheckHelper.isEmpty(houseHistory.remark)) {
//                    mTxtRemark.setText("备        注:" + houseHistory.remark);
//                }
//
//                if (!StringCheckHelper.isEmpty(houseHistory.status)) {
//                    mTxtStatus.setText("状        态:" + houseHistory.status);
//                }
            }

        };
        mListView.setAdapter(houseAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // FIXME 点击
                HouseHistory history = (HouseHistory) parent.getItemAtPosition(position);
//                showTePanLiShiDlg(history);
                showFunction(history);
            }
        });
    }
    // endregion

//    private void showTePanLiShiDlg(final HouseHistory history) {
//
//        final Dialog dlg = new Dialog(HouseHistoryActivity.this, R.style.dialog);
//        LayoutInflater inflater = LayoutInflater
//                .from(HouseHistoryActivity.this);
//        View viewContent = inflater.inflate(R.layout.dlg_house_history, null);
//
//        final ListView listview = (ListView) viewContent
//                .findViewById(R.id.recycleview);
//
//        listview.setAdapter(new CommonListAdapter<String>(this, R.layout.layout_tab_search_house_action_list_item, Arrays.asList(fundtions)) {
//            @Override
//            public void convert(ViewHolder holder, String string, int position) {
//                CheckedTextView mTxtName = holder.findViewById(R.id.tv_action);
//                mTxtName.setEnabled(true);
//                if (position == 0 || position == 3) {
//                    if (history.status.contains("暂存")) {
//                        mTxtName.setEnabled(false);
//                    }
//                }
//
//                mTxtName.setText(string);
//
//
//            }
//        });
//
//
//        listview.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                TextView mTxtFunction = (TextView) view.findViewById(R.id.tv_action);
//                if (!mTxtFunction.isEnabled()) {
//                    return;
//                }
//                dlg.dismiss();
//                switch (position) {
//                    //查看
//                    case 0:
//                        HouseSource house = new HouseSource();
//                        house.estatename = history.estatename;
//                        house.propertyid = history.propertyid;
//                        UIHelper.startActivity(mContext,
//                                HouseDetailActivity.class, house);
//                        break;
//                    case 1:
//                        //分享
//                        showShare(history);
//                        break;
//                    case 2:
//                        //房间图
////                        UIHelper.startActivity(HouseHistoryActivity.this, HousePictureActivity.class, history);
//
//                        Intent intent = new Intent(HouseHistoryActivity.this,
//                                UploadActivity.class);
//                        intent.putExtra("propertyid", history.propertyid);
//                        intent.putExtra("foreignId", history.propertyid);
//
//                        intent.putExtra(
//                                "estateinfo",
//                                "【" + history.estatename + "】"
//                                        + history.buildno + history.roomno);
//                        intent.putExtra("type", "house_history");
//                        startActivity(intent);
//                        break;
//                    case 3:
//                        //继续登记
//                        dengji(0, history.propertyid);
//                        break;
//                    case 4:
//                        dengji(1, history.propertyid);
//                        //删除、撤回
//                        break;
//                }
//                dlg.dismiss();
//
//            }
//        });
//
//        dlg.setContentView(viewContent);
//        dlg.setCanceledOnTouchOutside(true);
//        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode,
//                                 KeyEvent event) {
//                // TODO Auto-generated method stub
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                }
//                return false;
//            }
//        });
//
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        Window window = dlg.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = dm.widthPixels - DensityUtil.dip2px(this, 80);
//        lp.dimAmount = 0.5f;
//        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(lp);
//        dlg.show();
//    }
    @InjectResource(value = R.array.house_fundtion_list)
    private String[] house_fundtion;


    private void showFunction(final HouseHistory history) {
//        final String empId = AppContext.getInstance().getAppPref().EmpId();
        int imageIds[] = new int[]{R.drawable.icon_houtai_bg, R.drawable.icon_maidian_bg,
                R.drawable.icon_genfang_bg, R.drawable.icon_share_bg,R.drawable.icon_qcode_bg,
                R.drawable.icon_genjin_bg,0,0,0
//                R.drawable.icon_fk_bg,
//                 R.drawable.icon_yaoshi_bg,
//                 R.drawable.icon_guanzhu_bg,


        };
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < imageIds.length; i++) {
            arrayList.add(imageIds[i]);
        }


        final Dialog dlg = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater.inflate(
                R.layout.dlg_tab_gridview, null);
        final GridView listview = (GridView) viewContent
                .findViewById(R.id.gridview_action);
        listview.setAdapter(new CommonListAdapter<Integer>(this, R.layout.layout_tab_houselist_action_list_item, arrayList) {
            @Override
            public void convert(ViewHolder holder, Integer integer, int position) {

                TextView mIvFunction = holder.findViewById(R.id.txt_action);
                TextView mTxtName = holder.findViewById(R.id.txt_name);
                mTxtName.setText(house_fundtion[position]);
                mIvFunction.setBackgroundResource(integer);

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
                    case 0:// 申请跟房
                   HouseSource house = new HouseSource();
                        house.estatename = history.estatename;
                        house.propertyid = history.propertyid;
                        UIHelper.startActivity(mContext,
                                HouseDetailActivity.class, house);
                    break;

                    case 1:// 房源修改
                        //当这个条件是true时可用
                        if (TextUtils.equals(history.flagnoneagencyrent,"true")){

                        }
                    break;
                    case 2://房屋图
                        //当这个条件是true时可用
                        if (TextUtils.equals(history.flagnoneagencyrent,"true")){
                            Intent intent = new Intent(HouseHistoryActivity.this,
                                    UploadActivity.class);
                            intent.putExtra("propertyid", history.propertyid);
                            intent.putExtra("foreignId", history.propertyid);
                            intent.putExtra( "estateinfo", "【" + history.estatename + "】"
                                            + history.buildno + history.roomno);
                            intent.putExtra("type", "house_history");
                            startActivity(intent);
                        }else {

                        }

                    break;
                    case 3:////分享
                        showShare(history);

                    break;

                    case 4:// 二维码

                        //当这个条件是true时可用
                        if (TextUtils.equals(history.flagnoneagencyrent,"true")){
                        }
                    break;
                    case 5:// 换封面
                        //当这个条件是true时可用
                        if (TextUtils.equals(history.flagnoneagencyrent,"true")){
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
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = dm.widthPixels * 2 / 3;
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        window.setAttributes(attributes);
        dlg.show();
    }


    private void refreshDate() {
        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(this,
                ServerUrl.METHOD_getMyAllProperty, obj.toString(),
                mHandler);
        clientHelper.setShowProgressMessage("正在获取数据...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);

    }

    private void dengji(int index, String propertyid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", propertyid);
            String serverUrl = null;
            if (index == 0) {
                serverUrl = ServerUrl.METHOD_findPropertyDJ;
            } else {
                serverUrl = ServerUrl.METHOD_updatePropertyDJ;

            }

            ClientHelper clientHelper = new ClientHelper(this, serverUrl,
                    obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在提交...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    int share_type = 0;

    private void showShare(final HouseHistory history) {
        final BottomView bottomView = new BottomView(this,
                R.style.loading_dialog, R.layout.share_entrust);
        final RelativeLayout selectLayout = (RelativeLayout) inflateContentView(R.layout.item_share_select);
        final RelativeLayout shareLayout = (RelativeLayout) bottomView.getView().findViewById(R.id.layout_share);
        final RelativeLayout Layout = (RelativeLayout) bottomView.getView().findViewById(R.id.layout);
        final ImageView mIvHouse = (ImageView) bottomView.getView().findViewById(R.id.iv_house_image);
        final EditText mEdtTitle = (EditText) bottomView.getView().findViewById(R.id.edt_title);
        final EditText mEdtContent = (EditText) bottomView.getView().findViewById(R.id.edt_content);
        final CheckableImageView mIvWechatmoments = (CheckableImageView)bottomView.getView().findViewById(R.id.iv_wechatmoments);
        final CheckableImageView wechat = (CheckableImageView) bottomView.getView().findViewById(R.id.iv_wechat);
        RelativeLayout mRadioGroup = (RelativeLayout) bottomView.getView().findViewById(R.id.share_layout);
        final CheckableImageView shortmessage = (CheckableImageView) bottomView.getView().findViewById(R.id.iv_shortmessage);
        bottomView.setAnimation(R.style.BottomToTopAnim);
        bottomView.showBottomView(true);
        mIvWechatmoments.setChecked(true);
        Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomView.dismissBottomView();
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringCheckHelper.isEmpty(history.estatename)) {
            stringBuilder.append(history.estatename + ",");
        }
        if (!StringCheckHelper.isEmpty(history.square)) {
            stringBuilder.append(history.square + "平,");
        }
        if (!StringCheckHelper.isEmpty(history.countf)) {
            stringBuilder.append(history.countf + "房");
        }
        if (!StringCheckHelper.isEmpty(history.countt)) {
            stringBuilder.append(history.countt + "厅");
        }
        if (!StringCheckHelper.isEmpty(history.countw)) {
            stringBuilder.append(history.countw + "卫,");
        }

        if (!StringCheckHelper.isEmpty(history.propertydirection)) {
            stringBuilder.append(history.propertydirection + "向,");
        }
        if (history.trade.equals("租")) {
            if (!StringCheckHelper.isEmpty(history.rentprice)) {
                stringBuilder.append(history.rentprice);
            }
            if (!StringCheckHelper.isEmpty(history.rentunitname)) {
                stringBuilder.append(history.rentunitname + ",");
            }
        } else {
            if (!StringCheckHelper.isEmpty(history.price)) {
                stringBuilder.append(history.price);
            }
            if (!StringCheckHelper.isEmpty(history.unitname)) {
                stringBuilder.append(history.unitname + ",");
            }
        }
        if (history.listEkefeature != null && history.listEkefeature.size() > 0) {
            for (int i = 0; i < history.listEkefeature.size(); i++) {
                Ekefeature ekefeature = history.listEkefeature.get(i);
                stringBuilder.append(ekefeature.detail + ",");
            }
        }
        final String shareContent = stringBuilder.toString();
        mEdtContent.setText(shareContent.substring(0, shareContent.lastIndexOf(",")) + "....");

        mIvWechatmoments
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share_type = 0;
                        startX = mItemWidth;
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        MyLog.d(TAG, "Wechatmoments x=" + location[0]);
                        mIvWechatmoments.setChecked(true);
                        wechat.setChecked(false);
                        shortmessage.setChecked(false);
                        mIvHouse.setVisibility(View.VISIBLE);
                        mEdtTitle.setVisibility(View.VISIBLE);
                        MyLog.d(TAG, "Wechatmoments x=" + mIvWechatmoments.getX());
                        MyLog.d(TAG, "startX =" + startX);
                    }
                });
        wechat
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        share_type = 1;
                        mIvHouse.setVisibility(View.VISIBLE);
                        mEdtTitle.setVisibility(View.VISIBLE);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        MyLog.d(TAG, "wechat x=" + location[0]);
                        mIvWechatmoments.setChecked(false);
                        wechat.setChecked(true);
                        shortmessage.setChecked(false);
                        startX = mItemWidth;
                        MyLog.d(TAG, "startX =" + startX);

                    }
                });
        shortmessage
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share_type = 2;
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        MyLog.d(TAG, "shortmessage x=" + location[0]);
                        mIvWechatmoments.setChecked(false);
                        wechat.setChecked(false);
                        shortmessage.setChecked(true);
                        startX = mItemWidth * 2;

                        MyLog.d(TAG, "startX =" + startX);
                        mIvHouse.setVisibility(View.GONE);
                        mEdtTitle.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.INVISIBLE);

                    }
                });
        final String title = mEdtTitle.getText().toString();
        final String content = mEdtContent.getText().toString();
        final String imagepath = history.ekeheadpic;

        bottomView.getView().findViewById(R.id.bt_share)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomView.dismissBottomView();
                        switch (share_type) {
                            case 0:
                                ShareHelper.showShareHouse(mContext, WechatMoments.NAME, title, content, imageIndex, ServerUrl.buildShareHouseHistoryUrl(history.propertyid));
                                break;
                            case 1:

                                ShareHelper.showShareHouse(mContext, Wechat.NAME, title, content, imageIndex, ServerUrl.buildShareHouseHistoryUrl(history.propertyid));

                                break;
                            case 2:
                                Intent intent = IntentFactory.getSendSMSIntent(mEdtContent.getText().toString() + ServerUrl.buildShareHouseHistoryUrl(history.propertyid));
                                startActivity(intent);

                                break;
                        }

                    }
                });


        mIvHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareLayout.getVisibility() == View.INVISIBLE) {
                    shareLayout.setVisibility(View.VISIBLE);
                } else {
                    shareLayout.setVisibility(View.INVISIBLE);
                }


            }
        });


        //图片
        ImageView mIvHouse1 = (ImageView) bottomView.getView().findViewById(R.id.iv_house1);
        ImageView mIvHouse2 = (ImageView) bottomView.getView().findViewById(R.id.iv_house2);
        ImageView mIvHouse3 = (ImageView) bottomView.getView().findViewById(R.id.iv_house3);
        ImageView mIvHouse4 = (ImageView) bottomView.getView().findViewById(R.id.iv_house4);
        ImageView mIvHouse5 = (ImageView) bottomView.getView().findViewById(R.id.iv_house5);
        ImageView mIvHouse6 = (ImageView) bottomView.getView().findViewById(R.id.iv_house6);

        mIvHouse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=0;
                mIvHouse.setImageResource(R.drawable.icon_house1);
            }
        });
        mIvHouse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=1;
                mIvHouse.setImageResource(R.drawable.icon_house2);

            }
        });
        mIvHouse3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=2;
                mIvHouse.setImageResource(R.drawable.icon_house3);

            }
        });
        mIvHouse4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=3;
                mIvHouse.setImageResource(R.drawable.icon_house4);

            }
        });
        mIvHouse5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=4;
                mIvHouse.setImageResource(R.drawable.icon_house5);

            }
        });
        mIvHouse6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex=5;
                mIvHouse.setImageResource(R.drawable.icon_default);
            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareLayout.getVisibility() == View.INVISIBLE) {
                    bottomView.dismissBottomView();
                }
            }
        });
    }


}
