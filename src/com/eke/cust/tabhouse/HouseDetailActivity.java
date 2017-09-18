package com.eke.cust.tabhouse;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.ChatActivity;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.DialogHelper;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.location.LocationHelper;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.model.Ekefeature;
import com.eke.cust.model.Emp;
import com.eke.cust.model.HouseDetail;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.HouseVo;
import com.eke.cust.model.Owner;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.activity_order.AddOrderActivity;
import com.eke.cust.tabhouse.imagepage.ImagePagerActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.widget.start.ProperRatingBar;
import com.fab.FloatingActionButton;
import com.fab.ObservableScrollView;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import foundation.toast.ToastUtil;
import foundation.widget.imageview.CircleImageView;
import foundation.widget.recyclerView.RecycleViewDivider;
import foundation.widget.recyclerView.adapter.BaseRecyclerViewAdapter;
import foundation.widget.recyclerView.viewholder.RecycleviewViewHolder;

import static com.eke.cust.net.ServerUrl.METHOD_addCustFollow;
import static com.eke.cust.net.ServerUrl.METHOD_addCustFollowInsist;
import static com.eke.cust.net.ServerUrl.METHOD_findListByEstate;
import static com.eke.cust.net.ServerUrl.METHOD_getOwerMess;

/**
 * 房屋详情
 *
 * @author wujian
 */
public class HouseDetailActivity extends BaseActivity implements
        OnClickListener {

    @InjectBundleExtra(key = "data")
    private HouseSource house;
    @InjectView(id = R.id.scroll_view)
    private ObservableScrollView scrollView;
    // 助理
    @InjectView(id = R.id.iv_advisory, click = true)
    private FloatingActionButton fbButton;
    // 下单
    @InjectView(id = R.id.txt_add_order, click = true)
    private TextView mTxtAddOrder;
    // 加入收藏
    @InjectView(id = R.id.txt_add_colloect, click = true)
    private TextView mTxtAddCollect;
    // 房屋图片
    @InjectView(id = R.id.iv_house_image, click = true)
    private ImageView mIvImage;
    // 更新时间
    @InjectView(id = R.id.txt_update_time)
    private TextView mTxtUpdateTime;
    // 入住时间
    @InjectView(id = R.id.text_stay_time)
    private TextView mTxtStayTime;
    // 出租或者出售
    @InjectView(id = R.id.text_price_title)
    private TextView mTxtPriceTitle;
    // 出租或者出售方式
    @InjectView(id = R.id.text_type)
    private TextView mTxtType;
    // 价格
    @InjectView(id = R.id.text_rent_price)
    private TextView mTxtRentPrice;
    // 支付方式
    @InjectView(id = R.id.text_pay_type)
    private TextView mTxtPayType;
    // 房源id
    @InjectView(id = R.id.text_propertid)
    private TextView mTxtPropertid;
    // 面积
    @InjectView(id = R.id.text_square)
    private TextView mTxtSquare;
    // 户型
    @InjectView(id = R.id.text_apartment)
    private TextView mTxtApartment;
    // 朝向
    @InjectView(id = R.id.text_propertydirection)
    private TextView mTxtPropertydirection;
    // 装修
    @InjectView(id = R.id.text_propertydecoration)
    private TextView mTxtPropertydecoration;
    // 楼层
    @InjectView(id = R.id.text_floor)
    private TextView mTxtFloor;
    // 家私
    @InjectView(id = R.id.text_propertyfurniture)
    private TextView mTxtPropertyfurniture;
    // 房源特点
    @InjectView(id = R.id.layout_feature)
    private LinearLayout mLayoutFeature;
    // 房源特点
    @InjectView(id = R.id.txt_feature)
    private TextView mTxtFeature;
    // 楼盘名
    @InjectView(id = R.id.txt_house_name)
    private TextView mHouseName;

    // ---------------房源特点-----------------
    @InjectView(id = R.id.txt_house_type)
    private TextView mHouseType;
    @InjectView(id = R.id.text_house_rent_price)
    private TextView mHousePrice;
    // 区域
    @InjectView(id = R.id.text_house_area)
    private TextView mHouseArea;
    // 建筑类型
    @InjectView(id = R.id.text_build_type)
    private TextView mHouseBuildType;

    // 物业类型
    @InjectView(id = R.id.text_property_type)
    private TextView mHousePropertyType;
    // 建筑时间
    @InjectView(id = R.id.text_create_time)
    private TextView mHouseCreateTime;
    // 物业公司
    @InjectView(id = R.id.text_property_company)
    private TextView mPropertyCompany;
    // 物业费
    @InjectView(id = R.id.text_property_costs)
    private TextView mPropertyCosts;
    // 开发商
    @InjectView(id = R.id.text_developers)
    private TextView mDevelopers;
    // 总户数
    @InjectView(id = R.id.text_total_number)
    private TextView mTotalNumber;
    // 地址
    @InjectView(id = R.id.text_address)
    private TextView mTxtAddress;
    // 商业配套
    @InjectView(id = R.id.txt_business_title)
    private TextView mTxtBusinessTitle;
    @InjectView(id = R.id.txt_business)
    private TextView mTxtBusiness;
    // 人文环境
    @InjectView(id = R.id.txt_environment_title)
    private TextView mTxtEnvironmentTitle;
    @InjectView(id = R.id.txt_environment)
    private TextView mTxtEnvironment;
    // 交通配套
    @InjectView(id = R.id.txt_transportation_title)
    private TextView mTxtTransportationTitle;
    @InjectView(id = R.id.txt_transportation)
    private TextView mTxtTransportation;
    @InjectView(id = R.id.layout_bottom)
    private LinearLayout mLayoutBottom;


    //助理
    @InjectView(id = R.id.layout_advisory)
    private RelativeLayout mLayoutAdvisory;
    //助理头像
    @InjectView(id = R.id.iv_head)
    private CircleImageView mAdvisoryHead;
    //助理名字
    @InjectView(id = R.id.txt_name)
    private TextView mTxtName;
    //是否认证
    @InjectView(id = R.id.iv_authstate)
    private ImageView mIvAuthstate;
    //等级
    @InjectView(id = R.id.ratingBar)
    private ProperRatingBar mRatigBar;

    //老头
    @InjectView(id = R.id.layout_oldman)
    private RelativeLayout mLayoutOldMan;
    @InjectView(id = R.id.layout_head)
    private RelativeLayout mLayoutOldManHead;
    @InjectView(id = R.id.layout_content)
    private RelativeLayout mLayoutOldManContent;

    @InjectView(id = R.id.tv_mgtcompany)
    private TextView mTxtCompany;
    @InjectView(id = R.id.tv_ownername)
    private TextView mTxtOwnername;
    @InjectView(id = R.id.tv_tel)
    private TextView mTxtTel;
    private HouseDetail houseDetail;
    //随机助理
    private Emp emp;
    public double latitude = 0.0;
    public double longitude = 0.0;
    private LocationHelper locateHelper;
    private LocationHelper.LocationListener mLocationListener;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
            hideLoading();
            if (msg != null) {
                switch (msg.what) {
                    case Constants.DEFAULT:
                        final TextView mTxtSubscrebe = (TextView) subscribeDialog.findViewById(R.id.txt_subscrebe);
                        //预约
                        final LinearLayout mLayoutSubscrebe = (LinearLayout) subscribeDialog.findViewById(R.id.layout_change);
                        //提示
                        final TextView mSubscrebe = (TextView) subscribeDialog.findViewById(R.id.txt_subscreve_hint);
                        mSubscrebe.setVisibility(View.VISIBLE);
                        mTxtSubscrebe.setVisibility(View.GONE);
                        mLayoutSubscrebe.setVisibility(View.VISIBLE);
                        break;
                    case Constants.NO_NETWORK:
                        break;

                    case Constants.TAG_SUCCESS:
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            MyLog.i( TAG,jsonObject.toString() );
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                switch (request_url){
                                    case ServerUrl.METHOD_getPropertyById://房源详情
                                        JSONObject obj_data = jsonObject.getJSONObject("data");
                                        HouseDetail houseDetail = JSONUtils.getObject( obj_data, HouseDetail.class);
                                        initData(houseDetail);
                                        break;
                                    case ServerUrl.METHOD_queryRandDL://楼盘首推助理
                                        mLayoutAdvisory.setVisibility(View.VISIBLE);
                                        JSONObject query_data = jsonObject.getJSONObject("data");
                                        emp = JSONUtils.getObject(query_data, Emp.class);
                                        initEmp(emp);
                                        hideLoading();
                                        break;
                                    case ServerUrl.METHOD_insertEkecustcollect://收藏成功
                                        ToastUtils.show(mContext, "收藏成功！");
                                        break;
                                    case ServerUrl.METHOD_insertContract:
                                        ToastUtils.show(mContext, "下单成功！");//下单成功
                                        break;
                                    case ServerUrl.METHOD_findListByEstate://详情-取助理列表
                                        hideLoading();
                                        JSONArray jsonArray = JSONUtils.getJSONArray( jsonObject, "data", null);
                                        if (jsonArray != null) {
                                            ArrayList<Emp> emps = JSONUtils.getObjectList(jsonArray, Emp.class);
                                            showList(emps);
                                        }
                                        break;
                                    case ServerUrl.METHOD_getOwerMess://获取业主联系方式
                                        JSONObject OwerMess_data = jsonObject.getJSONObject("data");
                                        Owner owner = JSONUtils.getObject(OwerMess_data, Owner.class);
                                        showOwner(owner);
                                        break;
                                    case ServerUrl.METHOD_addCustFollow://助理详情-预约
                                        hideLoading();
                                        if (subscribeDialog != null) {
                                            subscribeDialog.cancel();
                                            ToastUtils.show(AppContext.getInstance(), "预约成功");

                                        }
                                        break;
                                    case ServerUrl.METHOD_addCustFollowInsist://助理详情-坚持预约
                                        int data = JSONUtils.getInt(jsonObject, "data", 0);
                                        hideLoading();
                                        if (data == 0) {
                                            addCustFollow();
                                        } else {
                                            mHandler.sendEmptyMessage(Constants.DEFAULT);
                                        }
                                        break;


                                }

                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                hideLoading();
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                ToastUtil.showToast( errorMsg );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoading();
                            ToastUtil.showToast( "出错!" );
                        }

                        break;

                    case Constants.TAG_FAIL:
                        hideLoading();
                        ToastUtil.showToast( "请求出错!" );
                        break;
                    case Constants.TAG_EXCEPTION:
                        hideLoading();
                        ToastUtil.showToast( "请求出错!" );
                        break;
                }

            }
        }

    };
    private String ContentStr = "你好";

    //初始单个助理
    private void initEmp(Emp emp) {
        this.emp = emp;
        int star = emp.star;
        mRatigBar.setRating(star);
        if (!StringCheckHelper.isEmpty(emp.ekeicon)) {
            Bitmap bitmap = BitmapUtils.stringtoBitmap(emp.ekeicon);
            mAdvisoryHead.setImageBitmap(bitmap);
        } else {
            mAdvisoryHead.setImageResource(R.drawable.head_gray);
        }
        String empname = !StringCheckHelper.isEmpty(emp.empname) ? emp.empname : "";
        String authstate = !StringCheckHelper.isEmpty(emp.authstate) ? emp.authstate : "";
        if (StringCheckHelper.isEmpty(authstate)) {
            mIvAuthstate.setVisibility(View.GONE);
        } else {
            if (authstate.equals("已认证")) {
                mIvAuthstate.setImageResource(R.drawable.icon_authstate);
            } else {
                mIvAuthstate.setVisibility(View.GONE);
            }
        }
        mTxtName.setText(empname);


    }

    // region 显示业主信息
    private void showOwner(Owner owner) {
        mLayoutOldManContent.setVisibility(View.VISIBLE);
        mLayoutOldManHead.setVisibility(View.GONE);
        if (!StringCheckHelper.isEmpty(owner.mgtcompany)) {
            mTxtCompany.setText("物        业:" + owner.mgtcompany + " "
                    + owner.buildno + "  " + owner.roomno);
        }
        if (!StringCheckHelper.isEmpty(owner.ownername)) {
            mTxtOwnername.setText("业        主:" + owner.ownername);
        }
        if (!StringCheckHelper.isEmpty(owner.ownertel)) {
            mTxtTel.setText("联系方式:" + owner.ownertel);

        }

    }

    // endregion

    @Override
    protected View onCreateContentView() {
        // TODO Auto-generated method stub
        return inflateContentView(R.layout.activity_house_detail_layout);
    }

    // 初始化
    protected void initData(HouseDetail houseDetail) {
        mIvImage.setMaxWidth(ScreenUtils.getScreenWidth(this));
        mIvImage.setMaxHeight((int) (ScreenUtils.getScreenWidth(this) * 2 / 3));//
        this.houseDetail = houseDetail;
        if (houseDetail.followtime != 0) {
            mTxtUpdateTime.setText(DateUtil.getDateDiff(houseDetail.followtime));
        }

        if (!StringCheckHelper.isEmpty(houseDetail.square)) {
            mTxtSquare.setText(houseDetail.square + "平米");
        }

        Date now = new Date();
        String time = DateUtil
                .getDateToString3(houseDetail.handoverdate);
        if (houseDetail.handoverdate == 0
                || houseDetail.handoverdate < now.getTime()) {
            mTxtStayTime.setText("随时入住");
        } else if (time.contains("2099")) {
            mTxtStayTime.setText("待定");
        } else {
            mTxtStayTime.setText(time + "前入住");
        }
        mTxtType.setText(houseDetail.trade);
        String countf = !StringCheckHelper.isEmpty(houseDetail.countf) ? houseDetail.countf
                : "0";
        String countt = !StringCheckHelper.isEmpty(houseDetail.countt) ? houseDetail.countt
                : "0";
        String countw = !StringCheckHelper.isEmpty(houseDetail.countw) ? houseDetail.countw
                : "0";
        String county = !StringCheckHelper.isEmpty(houseDetail.county) ? houseDetail.county
                : "0";

        mTxtApartment.setText(countf + "室" + countt + "厅" + countw + "卫"
                + county + "阳台");
        if (!StringCheckHelper.isEmpty(houseDetail.propertydirection)) {
            mTxtPropertydirection.setText(houseDetail.propertydirection);
        }
        if (!StringCheckHelper.isEmpty(houseDetail.propertydecoration)) {
            mTxtPropertydecoration.setText(houseDetail.propertydecoration);
        }
        if (!StringCheckHelper.isEmpty(houseDetail.floor)) {
            mTxtFloor.setText(houseDetail.floor + "/" + houseDetail.floorall
                    + "层");
        }
        if (!StringCheckHelper.isEmpty(houseDetail.propertyfurniture)) {
            mTxtPropertyfurniture.setText(houseDetail.propertyfurniture);
        }
        if (houseDetail.listEkefeature != null
                && houseDetail.listEkefeature.size() > 0) {
            for (int i = 0; i < houseDetail.listEkefeature.size(); i++) {
                Ekefeature ekeFeature = houseDetail.listEkefeature.get(i);
                if (ekeFeature != null) {
                    TextView textView = (TextView) inflateContentView(R.layout.item_feature);
                    if (i < 2) {
                        if (i % 2 == 0) {
                            textView.setBackgroundResource(R.drawable.maidian1);
                        } else {
                            textView.setBackgroundResource(R.drawable.maidian2);
                        }
                    } else {
                        textView.setBackgroundResource(R.drawable.maidian3);

                    }
                    if (!StringCheckHelper.isEmpty(ekeFeature.detail)) {
                        textView.setText(ekeFeature.detail);
                        LayoutParams liaParams = new LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        liaParams.setMargins(5, 0, 5, 0);// 设置了左右上下边距，
                        mLayoutFeature.addView(textView, liaParams);
                    }

                }

            }
        } else {
            mTxtFeature.setVisibility(View.GONE);

        }

        // 房源

        HouseVo houseVo = houseDetail.vo;
        if (houseVo != null) {
            mHouseCreateTime
                    .setText(String.format("%s年", houseVo.completeyear));
        }
        if (houseDetail.trade.equals("出租")) {
            String rentprice = !StringCheckHelper.isEmpty(houseDetail.rentprice) ? houseDetail.rentprice : "0";
            mTxtPriceTitle.setText("租金:");

            mTxtRentPrice.setText(rentprice + "元/月");
            mTxtPayType.setVisibility(View.GONE);

            // 房源特点
            mHouseType.setText("租         金:");
            mHousePrice.setText(rentprice + "元/月");

        } else {
            mTxtPayType.setVisibility(View.GONE);
            mTxtPriceTitle.setText("售价:");
            String price = !StringCheckHelper.isEmpty(houseDetail.price) ? houseDetail.price : "0";


            mTxtRentPrice.setText(price + "万");

            mTxtApartment.setText(countf + "室" + countt + "厅" + countw + "卫"
                    + county + "阳台");

            // 房源特点
            mHouseType.setText("售         价:");
            mHousePrice.setText(price + "万");

        }
        String propertyno = !StringCheckHelper.isEmpty(houseDetail.propertyno) ? houseDetail.propertyno
                : "";
        mTxtPropertid.setText(propertyno);

        String areaname = houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.areaname) ? houseVo.areaname
                : "";
        mHouseArea.setText(areaname);
        String propertyusage = houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.propertyusage) ? houseVo.propertyusage
                : "";
        mHousePropertyType.setText(propertyusage);
        String mgtprice = houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.mgtprice) ? houseVo.mgtprice
                : "";

        mPropertyCosts.setText(mgtprice + "元/平米/月");
        String room = houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.room) ? houseVo.room
                : "0";
        mTotalNumber.setText(String.format("约%s户", room));
        String mgtcompany = houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.mgtcompany) ? houseVo.mgtcompany
                : "";
        mPropertyCompany.setText(mgtcompany);

        if (houseVo != null && !StringCheckHelper.isEmpty(houseVo.devcompany)) {
            mDevelopers.setText(houseVo.devcompany);

        }
        if (houseVo != null && !StringCheckHelper.isEmpty(houseVo.address)) {
            mTxtAddress.setText(houseVo.address);

        }
        if (houseVo != null && !StringCheckHelper.isEmpty(houseVo.business)) {
            mTxtBusiness.setText(houseVo.business);
        } else {
            mTxtBusiness.setVisibility(View.GONE);
            mTxtBusinessTitle.setVisibility(View.GONE);
        }
        if (houseVo != null && !StringCheckHelper.isEmpty(houseVo.environment)) {
            mTxtEnvironment.setText(houseVo.environment);
        } else {
            mTxtEnvironment.setVisibility(View.GONE);
            mTxtEnvironmentTitle.setVisibility(View.GONE);
        }
        if (houseVo != null
                && !StringCheckHelper.isEmpty(houseVo.transportation)) {
            mTxtTransportation.setText(houseVo.transportation);

        } else {
            mTxtTransportation.setVisibility(View.GONE);
            mTxtTransportationTitle.setVisibility(View.GONE);
        }
        mLayoutBottom.setVisibility(View.VISIBLE);

        if (houseDetail.trade.contains("售")) {
            //助理
            queryRandDLByIds(false);
            mLayoutOldMan.setVisibility(View.GONE);
        } else {
            if (!houseDetail.flagnoneagencyrent) {
                //助理
                queryRandDLByIds(false);
                mLayoutOldMan.setVisibility(View.GONE);
            } else {
                mLayoutAdvisory.setVisibility(View.GONE);
                mLayoutOldMan.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle(house.estatename);
        mHouseName.setText(house.estatename);
        registerLeftImageView(R.drawable.arrow_back);
        if (house.ekeheadpic != null && !house.ekeheadpic.equals("")) {
            mIvImage.setImageBitmap(BitmapUtils
                    .stringtoBitmap(house.ekeheadpic));
        } else {
            mIvImage.setImageResource(R.drawable.house_defacult_bg);
        }
        fbButton.attachToScrollView(scrollView);

        findViewById(R.id.txt_more).setOnClickListener(this);
        findViewById(R.id.layout_advisory).setOnClickListener(this);
        findViewById(R.id.layout_oldman).setOnClickListener(this);

        locateHelper = new LocationHelper.Builder(getApplicationContext())
                .setScanSpan(0)
                .setIsNeedLocationDescribe(true).build();

        mLocationListener = new LocationHelper.LocationListener() {
            @Override
            public void onReceiveLocation(LocationHelper.LocationEntity location) {
                System.out.println(location);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locateHelper.stop();

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(" throwable " + e);
            }
        };
        locateHelper.registerLocationListener(mLocationListener);
        locateHelper.start();
        //
        getHouseDetailByIds();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locateHelper != null) {
            locateHelper.stop();
        }
    }

    private void getHouseDetailByIds() {
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", house.propertyid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getPropertyById, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //随机查询代理
    private void queryRandDLByIds(boolean showProgress) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("estateid", houseDetail.estateid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_queryRandDL, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(showProgress);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.txt_add_order://立即下单
                addOrder();
                break;
            case R.id.txt_add_colloect://加入收藏
                addCollect();
                break;
            case R.id.txt_more://更多
                ShowAdvistory();
                break;
            case R.id.layout_advisory://预约看房dialog
                showSubscribe(emp, false);

                break;
            case R.id.layout_oldman:
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.startToLogin(this);
                } else {
                    ShowAdvistory();
                }
                break;
            case R.id.iv_house_image:
                HouseSource houseSource = new HouseSource();
                houseSource.propertyid = houseDetail.propertyid;
                UIHelper.startActivity(HouseDetailActivity.this,
                        ImagePagerActivity.class, houseSource);

                break;
            default:
                break;
        }

    }




    //0  助理  1 卖家
    private void ShowAdvistory() {
        showLoading();
        JSONObject obj = new JSONObject();
        try {

            String serverUrl = null;
            if (!houseDetail.flagnoneagencyrent) {
                if (houseDetail != null) {
                    obj.put("estateid", houseDetail.estateid);
                }
                serverUrl = ServerUrl.METHOD_findListByEstate;
            } else {
                serverUrl = ServerUrl.METHOD_getOwerMess;
                if (houseDetail != null) {
                    obj.put("propertyid", houseDetail.propertyid);
                }
            }
            ClientHelper clientHelper = new ClientHelper(mContext, serverUrl,
                    obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("提交...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // 添加收藏
    private void addCollect() {
        // TODO Auto-generated method stub
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("propertyid", house.propertyid);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_insertEkecustcollect, obj.toString(),
                        mHandler);
                clientHelper.setShowProgressMessage("提交...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(true);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // region 显示助理
    Dialog subscribeDialog = null;

    public void showSubscribe(final Emp emp, boolean isBusy) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater.inflate(R.layout.layout_subscribe, null);
        if (!isBusy) {
            subscribeDialog = new Dialog(this, R.style.dialog);
            subscribeDialog.setContentView(viewContent);
            subscribeDialog.setCanceledOnTouchOutside(true);
            subscribeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

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

            Window window = subscribeDialog.getWindow();
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels - DensityUtil.dip2px(this, 80);
            lp.dimAmount = 0.5f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);
            subscribeDialog.show();
        }
        //头像
        CircleImageView headImage = (CircleImageView) viewContent.findViewById(R.id.iv_user_head);
        //名字
        TextView mTxtName = (TextView) viewContent.findViewById(R.id.txt_name);
        //公司
        TextView mTxtCompany = (TextView) viewContent.findViewById(R.id.txt_company);
        //星级
        ProperRatingBar ratingBar = (ProperRatingBar) viewContent.findViewById(R.id.ratingBar);
        //预约
        final TextView mTxtSubscrebe = (TextView) viewContent.findViewById(R.id.txt_subscrebe);
        //预约
        final LinearLayout mLayoutSubscrebe = (LinearLayout) viewContent.findViewById(R.id.layout_change);
        //更换
        final TextView mChangeSubscrebe = (TextView) viewContent.findViewById(R.id.txt_change_subscrebe);
        //提示
        final TextView mSubscrebe = (TextView) viewContent.findViewById(R.id.txt_subscreve_hint);


        final ImageView mIvChat = (ImageView) viewContent.findViewById(R.id.iv_chat);
        //不更换
        TextView mInsist = (TextView) viewContent.findViewById(R.id.txt_insist);
        if (emp != null) {
            String enterprisename = !StringCheckHelper.isEmpty(emp.enterprisename) ? emp.enterprisename : "";
            String empname = !StringCheckHelper.isEmpty(emp.empname) ? emp.empname : "";

            if (!StringCheckHelper.isEmpty(emp.ekeicon)) {
                headImage.setImageBitmap(BitmapUtils
                        .stringtoBitmap(emp.ekeicon));
            } else {
                headImage.setImageResource(R.drawable.head_gray);
            }
            mTxtName.setText(empname);
            mTxtCompany.setText(enterprisename);

            if (emp.star != 0) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(emp.star);
            } else {
                ratingBar.setVisibility(View.GONE);
            }

            mTxtSubscrebe.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppContext.getInstance().isLogin()) {
                        UIHelper.startToLogin(HouseDetailActivity.this);
                    } else {
                        getEmpAppointResp();
                    }
                    subscribeDialog.cancel();

                }
            });
            mIvChat.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppContext.getInstance().isLogin()) {
                        UIHelper.startToLogin(HouseDetailActivity.this);
                    } else {
                        //跳转到聊天
                        Intent intent = new Intent();
                        intent.setClass(HouseDetailActivity.this, ChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, emp.empid);
                        intent.putExtra(EaseConstant.EXTRA_USER_NAME, emp.empname);
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                        startActivity(intent);

//                        sendWXTempToEmp();
                    }
                    subscribeDialog.cancel();

                }
            });
        }

        mChangeSubscrebe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeDialog.cancel();
                showLoading();
                queryRandDLByIds(false);
            }
        });
        mInsist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustFollow();
                subscribeDialog.cancel();

            }
        });


    }

    // endregion
    // 下单

    private void addOrder() {
        // TODO Auto-generated method stub
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.startToLogin(this);
        } else {
            UIHelper.startActivity(mContext, AddOrderActivity.class, house);
        }

    }


    //预约看房

    public void showList(final ArrayList<Emp> emps) {
        RecyclerView recycleView = new RecyclerView(mContext);
        recycleView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        final AlertDialog alertDialog = DialogHelper.getSelectDialog(mContext, recycleView);
        recycleView.setAdapter(new BaseRecyclerViewAdapter(mContext, emps) {
            @Override
            public void setUpData(RecyclerView.ViewHolder viewholder, int position, int viewType, Object data) {
                RecycleviewViewHolder holder = (RecycleviewViewHolder) viewholder;
                final Emp emp = (Emp) data;
                if (emp != null) {
                    CircleImageView headImage = holder.getView(R.id.iv_head);
                    TextView mTxtName = holder.getView(R.id.txt_name);
                    ImageView mIvAuthstate = holder.getView(R.id.iv_authstate);
                    ProperRatingBar mRatingBar = holder.getView(R.id.ratingBar);
                    if (!StringCheckHelper.isEmpty(emp.ekeicon)) {
                        headImage.setImageBitmap(BitmapUtils
                                .stringtoBitmap(emp.ekeicon));
                    } else {
                        headImage.setImageResource(R.drawable.head_gray);
                    }
                    String authstate = !StringCheckHelper.isEmpty(emp.authstate) ? emp.authstate : "";
                    if (StringCheckHelper.isEmpty(authstate)) {
                        mIvAuthstate.setVisibility(View.GONE);
                    } else {
                        if (authstate.equals("已认证")) {
                            mIvAuthstate.setImageResource(R.drawable.icon_authstate);
                        } else {
                            mIvAuthstate.setVisibility(View.GONE);
                        }
                    }
                    if (!StringCheckHelper.isEmpty(emp.empname)) {
                        mTxtName.setText(emp.empname);
                    }
                    if (emp.star != 0) {
                        mRatingBar.setVisibility(View.VISIBLE);
                        mRatingBar.setRating(emp.star);
                    } else {
                        mRatingBar.setVisibility(View.GONE);
                    }
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initEmp(emp);
                            showSubscribe(emp, false);
                            alertDialog.cancel();
                        }
                    });

                }

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecycleviewViewHolder(inflateContentView(R.layout.item_house_emp));
            }
        });
        alertDialog.show();

    }


    //添加预约
    private void addCustFollow() {
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", emp.empid);
            obj.put("estateid", houseDetail.estateid);
            obj.put("propertyid", houseDetail.propertyid);
            obj.put("custposition", latitude + "," + longitude);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    METHOD_addCustFollow, obj.toString(),
                    mHandler);
            clientHelper.setShowProgressMessage("提交...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //添加预约
    private void getEmpAppointResp() {
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", emp.empid);
            obj.put("estateid", houseDetail.estateid);
            obj.put("propertyid", houseDetail.propertyid);
            obj.put("custposition", latitude + "," + longitude);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    METHOD_addCustFollowInsist, obj.toString(),
                    mHandler);
            clientHelper.setShowProgressMessage("提交...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
