package com.eke.cust.tabhouse;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.api.BaseRestApi;
import com.eke.cust.api.more.MoreApiHelper;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.bean.HouseReference;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.DialogHelper;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.listener.SimpleTextWatcher;
import com.eke.cust.model.House;
import com.eke.cust.model.OpenCity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabmine.safty_activity.MapDialogFragment;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivity;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivityNew;
import com.eke.cust.tabmore.house_register_activity.house_add.CustomGridViewAdapter;
import com.eke.cust.tabmore.house_register_activity.house_add.CustomSelectableGridViewAdapter;
import com.eke.cust.tabmore.house_register_activity.house_add.FangYuanAdapter;
import com.eke.cust.tabmore.house_register_activity.house_add.HouseHistoryActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.ClickUtils;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import foundation.base.adapter.CommonListAdapter;
import foundation.base.adapter.ViewHolder;
import foundation.callback.ICallback1;
import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;
import foundation.toast.ToastUtil;
import foundation.util.JSONUtils;
import foundation.widget.recyclerView.adapter.BaseRecyclerViewAdapter;
import foundation.widget.recyclerView.decoration.GridSpaceItemDecoration;
import foundation.widget.recyclerView.decoration.SpaceItemDecoration;
import foundation.widget.recyclerView.viewholder.RecycleviewViewHolder;

import static com.eke.cust.AppContancts.PROPERTY_DECORATION;
import static com.eke.cust.AppContancts.PROPERTY_DIRECTION;
import static com.eke.cust.AppContancts.PROPERTY_FURNITURE;
import static com.eke.cust.AppContancts.PROPERTY_LOOK;
import static com.eke.cust.AppContancts.PROPERTY_TAX;


/**
 * 房屋登记
 */
public class OperatingHouseActivity extends BaseActivity implements View.OnClickListener, NotificationListener, ICallback1<BaseRestApi> {
    @InjectBundleExtra(key = "data")
    private HouseSourceNodeInfo houseSourceNodeInfo;


    @InjectView(id = R.id.city_Rel)
    private RelativeLayout city_Rel;


    @InjectView(id = R.id.city_name_tv)
    private TextView city_name_tv;
    //房源名称
    @InjectView(id = R.id.edt_house_name)
    private TextView mTxtHouseName;
    @InjectView(id = R.id.txt_house_name_mandatory)
    private TextView mTxtHouseNameMandatory;
    //栋座
    @InjectView(id = R.id.ListBuilding_tv)
    private TextView ListBuilding_tv;
    @InjectView(id = R.id.ListBuilding_tv_red)
    private TextView ListBuilding_tv_red;
    // 单元
    @InjectView(id = R.id.queryListEkecell_tv)
    private TextView queryListEkecell_tv;
    @InjectView(id = R.id.queryListEkecell_tv_red)
    private TextView queryListEkecell_tv_red;

    //租售方式
    @InjectView(id = R.id.rg_action)
    private RadioGroup mRgAction;
    //是否直租
    @InjectView(id = R.id.rg_zhizu_select)
    private RadioGroup zhizu_select;

    //房屋租售方式
    @InjectView(id = R.id.txt_zujin)
    private TextView mTxtZuJin;
    //租金或者售价
    @InjectView(id = R.id.layout_chuzu)
    private LinearLayout mLayoutChuZu;
    @InjectView(id = R.id.shuojia_Linear)
    private LinearLayout shuojia_Linear;
    @InjectView(id = R.id.fenzu_linear)
    private LinearLayout fenzu_linear;


    @InjectView(id = R.id.et_zujin)
    private TextView mEdtZuJin;
    @InjectView(id = R.id.tv_zujin_danwei)
    private TextView mTxtZuJinDanWei;
    @InjectView(id = R.id.txt_zushou_mandatory)
    private TextView mTxtZushouMandatory;

    //售价
//    @InjectView(id = R.id.layout_shoujia)
//    private LinearLayout mLayoutShoujIa;
//    @InjectView(id = R.id.et_shoujia)
//    private EditText mEdtShoujia;
//    @InjectView(id = R.id.txt_shoujia_mandatory)
//    private TextView mTxtShouJiaMandatory;

    //支付方式
//    @InjectView(id = R.id.layout_pay_type)
//    private RelativeLayout mLayoutPayType;
    //押金
//    @InjectView(id = R.id.et_yajin)
//    private EditText mEdtYaJin;
    //售价
    @InjectView(id = R.id.edt_price)
    private EditText mEdtprice;
    //    @InjectView(id = R.id.txt_pay_type_mandatory)
//    private TextView mTxtPayTypeMandatory;
    //税费说明
    @InjectView(id = R.id.txt_shuifei_select)
    private TextView mTxtPropertytax;
    @InjectView(id = R.id.txt_shuifei_mandatory)
    private TextView mTxtShuiFeiMandatory;
    //面积
    @InjectView(id = R.id.et_mianji)
    private EditText mEdtMianji;
    @InjectView(id = R.id.txt_mianji_mandatory)
    private TextView mTxtMianJiMandatory;
    //楼层
    @InjectView(id = R.id.et_ceng)
    private EditText mEdtCeng;
    @InjectView(id = R.id.et_ceng_count)
    private EditText mEdtCengCount;
    @InjectView(id = R.id.txt_louceng_mandatory)
    private TextView mTxtLoucengMandatory;

    //分租
//    @InjectView(id = R.id.layout_fenzu)
//    private LinearLayout mLayoutFenZu;
    //出租间
    @InjectView(id = R.id.txt_chuzujian_select)
    private TextView mTxtChuzujian;

    //合租
    @InjectView(id = R.id.et_hezu)
    private EditText mEdtHezu;
    //分租条件
    @InjectView(id = R.id.txt_fenzutiaojian_select)
    private TextView mTxtFenzutiaojian;

    //房号
    @InjectView(id = R.id.et_fanghao)
    private EditText mEdtFanghao;
    @InjectView(id = R.id.txt_fanghao_mandatory)
    private TextView mTxtFanghaoMandatory;
    //看房方式
    @InjectView(id = R.id.txt_kanfang_type)
    private TextView mTxtKanFangType;
    @InjectView(id = R.id.txt_kanfang_type_select)
    private TextView mTxtPropertylook;
    @InjectView(id = R.id.txt_kanfang_mandatory)
    private TextView mTxtKanFangMandatory;


    //户型
    //室
    @InjectView(id = R.id.et_huxing_shi_num)
    private EditText mEdtShi;
    @InjectView(id = R.id.et_huxing_ting_num)
    private EditText mEdtTing;
    @InjectView(id = R.id.et_huxing_wei_num)
    private EditText mEdtWei;
    @InjectView(id = R.id.et_huxing_yangtai_num)
    private EditText mEdtYangTai;
    @InjectView(id = R.id.txt_huxing_mandatory)
    private TextView mTxtMandatory;
    //朝向
    @InjectView(id = R.id.txt_txt_chaoxiang_select)
    private TextView mTxtPropertydirection;
    @InjectView(id = R.id.txt_chaoxiang_mandatory)
    private TextView mTxtChaoXiangMandatory;

    //装修
    @InjectView(id = R.id.txt_zhuangxi_select)
    private TextView mTxtPropertydecoration;
    @InjectView(id = R.id.txt_zhuangxi_mandatory)
    private TextView mTxtZhuangxiuMandatory;

    //配套设施
    @InjectView(id = R.id.txt_peitao_select)
    private TextView mTxtPropertyfurniture;


    //房源特点
    @InjectView(id = R.id.txt_tedian_add)
    private TextView mTxtTeDianAdd;
    @InjectView(id = R.id.layout_feature)
    private LinearLayout mlayoutFeature;

    //房源图
    @InjectView(id = R.id.gridview)
    private GridView mGridFangYuanTu;
    //房源说明
    @InjectView(id = R.id.et_qita_content, click = true)
    private EditText mEdtContent;

    //保存还是修改
//    @InjectView(id = R.id.layout_add)
//    private LinearLayout mLayoutAdd;
    @InjectView(id = R.id.btn_lijifabu, click = true)
    private Button mBtLijifabu;
    @InjectView(id = R.id.btn_daifabu, click = true)
    private Button mBtdaifabu;
//    @InjectView(id = R.id.btn_edit, click = true)
//    private Button mBtEdit;

    //检测是否重号
    @InjectView(id = R.id.Bn_ropertyInvalid,click = true)
    private Button BnropertyInvalid;

    //下划线
//    @InjectView(id = R.id.iv_line)
//    private View mLine;
    //    @InjectView(id = R.id.iv_line2)
//    private View mLine2;
//    @InjectView(id = R.id.iv_line3)
//    private View mLine3;
    @InjectView(id = R.id.view_line1)
    private View mLine1;

    //房源图
    private List<String> mFangYuanTuList = new ArrayList<String>();
    private FangYuanAdapter mFangYuanAdapter;
    // 楼盘
    private House house;
    private int houseType = 0;
    private String trade = "出租";
    //交房日期
    private String mStrFromDate = null;
    private String mStrToDate = null;
    private int mFromYear = 0;
    private int mFromMonth = 0;
    private int mFromDay = 0;

    public static final int ADD_ZHUZHAI_FANGYUANTU = 100;
    public static final int LOPAN_NAME = 101;
    private MoreApiHelper moreApiHelper;
    //PropertyTax
    private ArrayList<HouseReference> PropertyTax;
    private ArrayList<HouseReference> PropertyLook;
    private ArrayList<HouseReference> PropertyDirection;
    private ArrayList<HouseReference> PropertyDecoration;
    private ArrayList<HouseReference> PropertyFurniture;
    private ArrayList<HouseReference> ListBuilding;
    private ArrayList<HouseReference> Listcellid;

    //是否有注册emp tip
    String emptip  = "后台检测到您同时注册有助理端账号，为保障您的利益，请使用待命名助理端登记房源...";
    String emptipbn  = "返回";
    // 房源添加成功tip
    String  suretip = "房源已登记成功\n您可在我的 - 我是业主 下查看已登记房源";
    String  bntip = "好的";
    private String flagvalue =  "";//是否重号

    // 已经开放的城市
    private ArrayList<OpenCity> cities = new ArrayList<OpenCity>();


    //region 接口调用
    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
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

                                switch (request_url){
                                    case ServerUrl.METHOD_queryListCity://城市查询
                                        JSONArray jsonArray = com.eke.cust.utils.JSONUtils.getJSONArray(jsonObject, "data", null);
                                        if (jsonArray != null) {
                                            cities = com.eke.cust.utils.JSONUtils.getObjectList(jsonArray, OpenCity.class);
                                            //  initCity();
                                        }
                                        break;

                                    case ServerUrl.METHOD_insertPropertyByWT:
                                    case ServerUrl.METHOD_insertPropertyDj:
                                        showDialog(suretip,bntip);//房源添加成功
//                                        ToastUtils.show(mContext, "房源添加成功！");
                                        break;
                                    case ServerUrl.METHOD_queryCityListPage:
                                        break;
                                    case ServerUrl.METHOD_getFeatureByProperty:
                                        JSONArray array_features = jsonObject.optJSONArray("data");
                                        List<MaidianNodeInfo> infors = new ArrayList<MaidianNodeInfo>();
                                        if (array_features != null) {
                                            for (int i = 0; i < array_features.length(); i++) {
                                                JSONObject object = array_features.getJSONObject(i);
                                                if (object != null) {
                                                    String featureid = object.optString("featureid");
                                                    String featurdetail = object.optString("detail");
                                                    MaidianNodeInfo info = new MaidianNodeInfo();
                                                    info.setDetail(featurdetail);
                                                    info.setFeatureid(featureid);
                                                    info.setSelected(false);
                                                    infors.add(info);
                                                }
                                            }
                                            showMaidianDlg();
                                            maidianGridViewAdapter.updateBgColors(infors.size());
                                            mMaidianNodeInfos.clear();
                                            mMaidianNodeInfos.addAll(infors);
                                            String text = mTxtTeDianAdd.getText().toString();
                                            if (text.equals("点击添加")) {
                                                maidianGridViewAdapter.notifyDataSetChanged();
                                            } else {
                                                maidianGridViewAdapter.updateBgColors(text.split(" "));
                                            }

                                        }
                                        break;
                                    case  ServerUrl.METHOD_getUserIsEmp://是否注册emp
                                        String data_isemp = jsonObject.optString("data","");

                                        if (!TextUtils.isEmpty(data_isemp)){
                                            switch (data_isemp){
                                                case "false":
                                                    break;
                                                case "true":
                                                    showDialog(emptip,emptipbn);
                                                    break;
                                                default:
                                                    break;
                                            }

                                        }
                                        break;
                                    case ServerUrl.METHOD_queryListBuilding://根据楼盘获取栋座
                                        JSONArray queryList = jsonObject.optJSONArray("data");
                                        if (queryList !=null){
                                            ListBuilding = JSONUtils.getObjectList(queryList, HouseReference.class);
                                        }else {
                                            ToastUtil.showToast("该房源没有信息");
                                        }
                                        break;
                                    case ServerUrl.METHOD_queryListEkecell://根据栋座获取单元
                                        JSONArray cellidList = jsonObject.optJSONArray("data");
                                        if (cellidList !=null){
                                            Listcellid = JSONUtils.getObjectList(cellidList, HouseReference.class);
                                        }else {
                                            ToastUtil.showToast("该房源没有信息");
                                        }
                                        break;

                                    case  ServerUrl.METHOD_getPropertyInvalid://重复房源检测
                                        String flag = jsonObject.optString("data", "");
                                        JSONObject myJsonObject = new JSONObject(flag);
                                        flagvalue = myJsonObject.getString("flag");
                                        if (!TextUtils.isEmpty(flagvalue)){
                                            switch (flagvalue){
                                                case "false":
                                                    if (isCheck){
                                                        initData();
                                                    }else {
                                                        ToastUtil.showToast("该房间未被登记");
                                                    }

                                                    break;
                                                case "true":
                                                    ToastUtil.showToast("该房间已登记");
                                                    break;
                                            }
                                        }

                                        break;
                                }

                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(mContext.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    //提交信息
    private void initData() {
        JSONObject jsonString = new JSONObject();
        String roomno = mEdtFanghao.getText().toString().trim();//房间号
        String square =   mEdtMianji.getText().toString().trim();//面积
        String floor  = mEdtCeng.getText().toString().trim();//第几层
        String floorall  = mEdtCengCount.getText().toString().trim();//总共楼层
        String propertylook = mTxtPropertylook.getText().toString().trim();//看房方式

        //户型
        String countf = mEdtShi.getText().toString();
        String countt = mEdtTing.getText().toString();
        String countw = mEdtWei.getText().toString();
        String county = mEdtYangTai.getText().toString();
        //
        ////朝向
        String  propertydirection =  mTxtPropertydirection.getText().toString().trim();
        //装修程度
        String propertydecoration = mTxtPropertydecoration.getText().toString().trim();
        //配套设施
        String propertyfurniture = mTxtPropertyfurniture.getText().toString();
        //房源特点
        String ekefeature = mTxtTeDianAdd.getText().toString();

        switch (houseType){
            case 0: //出租
                String rentprice = mEdtZuJin.getText().toString().trim();//租金
                try {
                    jsonString.put("estateid",estateid);//楼盘id
                    jsonString.put("buildingid",buildingid);//栋座id
                    jsonString.put("cellid",cellid);//单元id
                    jsonString.put("roomno",roomno);//房间号
                    jsonString.put("trade",trade);//方式
                    jsonString.put("rentprice",rentprice);//租金
                    jsonString.put("square",square);//面积
                    jsonString.put("floor",floor);//第几层
                    jsonString.put("floorall",floorall);//总共楼层
                    jsonString.put("propertylook",propertylook);//看房方式
                    jsonString.put("countf", countf);//室
                    jsonString.put("countt", countt);//厅
                    jsonString.put("countw", countw);//卫
                    jsonString.put("county", county);//阳台
                    jsonString.put("propertydirection",propertydirection);//朝向
                    jsonString.put("propertydecoration",propertydecoration);//装修程度
                    jsonString.put("propertyfurniture",propertyfurniture);////配套设施
                    jsonString.put("ekefeature",ekefeature);//房源特点
                    jsonString.put("flagnoneagencyrent", flagnoneagencyrent);
//                    jsonString.put("list","");
                    JSONArray listjson1 = new JSONArray();
                    for (int i = 0; i < mFangYuanTuList.size() - 1; i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("filePath", Base64.encodeToString(
                                    BitmapUtils.getBitmapByte(mFangYuanTuList.get(i)),
                                    Base64.DEFAULT));
                            listjson1.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    jsonString.put("list", listjson1);
                    if (!TextUtils.isEmpty(estateid)&&!TextUtils.isEmpty(buildingid)&&!TextUtils.isEmpty(cellid)&&!TextUtils.isEmpty(roomno)
                            &&!TextUtils.isEmpty(trade)&&!TextUtils.isEmpty(rentprice)&&!TextUtils.isEmpty(square)&&!TextUtils.isEmpty(floor)
                            &&!TextUtils.isEmpty(floorall)&&!TextUtils.isEmpty(propertylook)&&!TextUtils.isEmpty(countf)&&!TextUtils.isEmpty(countt)&&!TextUtils.isEmpty(countw)
                            &&!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(propertydirection)&&!TextUtils.isEmpty(propertydecoration)&&!TextUtils.isEmpty(propertyfurniture)
                            &&!TextUtils.isEmpty(flagnoneagencyrent)){
                        ClientHelper clientHelper = new ClientHelper(mContext,
                                ServerUrl.METHOD_insertPropertyByWT, jsonString.toString(), mHandler);
                        clientHelper.setShowProgressMessage("正在提交数据...");
                        clientHelper.isShowProgress(true);
                        clientHelper.sendPost(true);
                    }else {
                        ToastUtil.showToast("请填写完整的信息！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                String rentprice1 = mEdtZuJin.getText().toString().trim();//租金
                String subletnumber = mEdtHezu.getText().toString().trim();//合租方式
                String subletroom = mTxtChuzujian.getText().toString().trim();//分租房型
                String subletcondition = mTxtFenzutiaojian.getText().toString().trim();//分租条件
                try {
                    jsonString.put("estateid",estateid);//楼盘id
                    jsonString.put("buildingid",buildingid);//栋座id
                    jsonString.put("cellid",cellid);//单元id
                    jsonString.put("roomno",roomno);//房间号
                    jsonString.put("trade",trade);//方式
                    jsonString.put("rentprice",rentprice1);//租金

                    jsonString.put("flagsublet","true");
                    jsonString.put("subletnumber",subletnumber);//合租方式

                    jsonString.put("subletroom",subletroom);//分租房型
                    jsonString.put("subletcondition",subletcondition);//分租条件

                    jsonString.put("square",square);//面积
                    jsonString.put("floor",floor);//第几层
                    jsonString.put("floorall",floorall);//总共楼层
                    jsonString.put("propertylook",propertylook);//看房方式
                    jsonString.put("countf", countf);//室
                    jsonString.put("countt", countt);//厅
                    jsonString.put("countw", countw);//卫
                    jsonString.put("county", county);//阳台
                    jsonString.put("propertydirection",propertydirection);//朝向
                    jsonString.put("propertydecoration",propertydecoration);//装修程度
                    jsonString.put("propertyfurniture",propertyfurniture);////配套设施
                    jsonString.put("ekefeature",ekefeature);//房源特点
                    jsonString.put("flagnoneagencyrent", flagnoneagencyrent);
//                    jsonString.put("list","");
                    JSONArray listjson1 = new JSONArray();
                    for (int i = 0; i < mFangYuanTuList.size() - 1; i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("filePath", Base64.encodeToString(
                                    BitmapUtils.getBitmapByte(mFangYuanTuList.get(i)),
                                    Base64.DEFAULT));
                            listjson1.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    jsonString.put("list", listjson1);
                    if (!TextUtils.isEmpty(estateid)&&!TextUtils.isEmpty(buildingid)&&!TextUtils.isEmpty(cellid)&&!TextUtils.isEmpty(roomno)
                            &&!TextUtils.isEmpty(trade)&&!TextUtils.isEmpty(rentprice1)&&!TextUtils.isEmpty(subletnumber)&&!TextUtils.isEmpty(subletroom)&&!TextUtils.isEmpty(subletcondition)
                            &&!TextUtils.isEmpty(square)&&!TextUtils.isEmpty(floor)
                            &&!TextUtils.isEmpty(floorall)&&!TextUtils.isEmpty(propertylook)&&!TextUtils.isEmpty(countf)&&!TextUtils.isEmpty(countt)&&!TextUtils.isEmpty(countw)
                            &&!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(propertydirection)&&!TextUtils.isEmpty(propertydecoration)&&!TextUtils.isEmpty(propertyfurniture)
                            &&!TextUtils.isEmpty(flagnoneagencyrent)){
                        ClientHelper clientHelper = new ClientHelper(mContext,
                                ServerUrl.METHOD_insertPropertyByWT, jsonString.toString(), mHandler);
                        clientHelper.setShowProgressMessage("正在提交数据...");
                        clientHelper.isShowProgress(true);
                        clientHelper.sendPost(true);
                    }else {
                        ToastUtil.showToast("请填写完整的信息！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case 2:

                String price = mEdtprice.getText().toString().trim();//售价
                String propertytax = mTxtPropertytax.getText().toString().trim();// 税费说明
                try {
                    jsonString.put("estateid",estateid);//楼盘id
                    jsonString.put("buildingid",buildingid);//栋座id
                    jsonString.put("cellid",cellid);//单元id
                    jsonString.put("roomno",roomno);//房间号
                    jsonString.put("trade",trade);//方式
                    jsonString.put("price",price);//售价
                    jsonString.put("propertytax", propertytax);// 税费说明

                    jsonString.put("square",square);//面积
                    jsonString.put("floor",floor);//第几层
                    jsonString.put("floorall",floorall);//总共楼层
                    jsonString.put("propertylook",propertylook);//看房方式
                    jsonString.put("countf", countf);//室
                    jsonString.put("countt", countt);//厅
                    jsonString.put("countw", countw);//卫
                    jsonString.put("county", county);//阳台
                    jsonString.put("propertydirection",propertydirection);//朝向
                    jsonString.put("propertydecoration",propertydecoration);//装修程度
                    jsonString.put("propertyfurniture",propertyfurniture);////配套设施
                    jsonString.put("ekefeature",ekefeature);//房源特点
                    jsonString.put("flagnoneagencyrent", flagnoneagencyrent);
//                    jsonString.put("list","");
                    JSONArray listjson1 = new JSONArray();
                    for (int i = 0; i < mFangYuanTuList.size() - 1; i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("filePath", Base64.encodeToString(
                                    BitmapUtils.getBitmapByte(mFangYuanTuList.get(i)),
                                    Base64.DEFAULT));
                            listjson1.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    jsonString.put("list", listjson1);
                    if (!TextUtils.isEmpty(estateid)&&!TextUtils.isEmpty(buildingid)&&!TextUtils.isEmpty(cellid)&&!TextUtils.isEmpty(roomno)
                            &&!TextUtils.isEmpty(trade)&&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(propertytax)
                            &&!TextUtils.isEmpty(square)&&!TextUtils.isEmpty(floor)
                            &&!TextUtils.isEmpty(floorall)&&!TextUtils.isEmpty(propertylook)&&!TextUtils.isEmpty(countf)&&!TextUtils.isEmpty(countt)&&!TextUtils.isEmpty(countw)
                            &&!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(propertydirection)&&!TextUtils.isEmpty(propertydecoration)&&!TextUtils.isEmpty(propertyfurniture)
                            &&!TextUtils.isEmpty(flagnoneagencyrent)){
                        ClientHelper clientHelper = new ClientHelper(mContext,
                                ServerUrl.METHOD_insertPropertyByWT, jsonString.toString(), mHandler);
                        clientHelper.setShowProgressMessage("正在提交数据...");
                        clientHelper.isShowProgress(true);
                        clientHelper.sendPost(true);
                    }else {
                        ToastUtil.showToast("请填写完整的信息！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private String buildingid = "";//楼盘id
    private String cellname = "";
    private String cellid = "";//栋座id
    private String estateid = "";//单元id
    private boolean isCheck = false;

    private void showdialogBuilding(final int index, ArrayList<HouseReference> listBuilding) {
        View view = inflateContentView(R.layout.layout_house_building);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.build_recycleview);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_5)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new GridSpaceItemDecoration(3, getResources().getDimensionPixelSize(R.dimen.dimen_5), true));
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false));

        final AlertDialog alertDialog = DialogHelper.getViewDialog(this, view);

        recyclerView.setAdapter(new BaseRecyclerViewAdapter(this, listBuilding) {
            @Override
            public void setUpData(RecyclerView.ViewHolder holder, int position, int viewType, Object data) {
                RecycleviewViewHolder recycleviewViewHolder = (RecycleviewViewHolder) holder;
                final HouseReference houseReference = (HouseReference) data;
                TextView mTxtHouseName = recycleviewViewHolder.getView(R.id.tv_maidian);

                switch (index){
                    case 0:
                        if (houseReference != null) {
                            String name = !StringCheckHelper.isEmpty(houseReference.buildingname) ? houseReference.buildingname : "";
                            String buildingpromotion = !StringCheckHelper.isEmpty(houseReference.buildingpromotion) ? houseReference.buildingpromotion : "";
                            mTxtHouseName.setText(name +"("+buildingpromotion+")");
                        }
                        break;
                    case 1:
                        if (houseReference != null) {
                            String cellname = !StringCheckHelper.isEmpty(houseReference.cellname) ? houseReference.cellname : "";
                            mTxtHouseName.setText(cellname);
                        }
                        break;
                }

                recycleviewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (index){
                            case 0:
                                buildingid = houseReference.buildingid;
                                ListBuilding_tv.setText(houseReference.buildingname+"("+houseReference.buildingpromotion+")");
                                ListBuilding_tv_red.setVisibility(View.INVISIBLE);
                                inintqueryListEkecell();//查询单元

                                break;
                            case 1:
                                cellname = houseReference.cellname;
                                cellid = houseReference.cellid;
                                queryListEkecell_tv.setText(cellname);
                                queryListEkecell_tv_red.setVisibility(View.INVISIBLE);
                                break;
                        }
                        alertDialog.cancel();

                    }
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecycleviewViewHolder(inflateContentView(R.layout.dlg_maidian_item));
            }
        });
        alertDialog.show();
    }

    private void inintqueryListEkecell() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("buildingid",buildingid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_queryListEkecell, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //是否有注册emp
    private void showDialog(String emptip, String emptipbn) {

        final Dialog dlg = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater.inflate(R.layout.dialog_set_emp, null);

        TextView tv_tip = (TextView) viewContent.findViewById(R.id.tv_tip);
        Button btn_left = (Button) viewContent.findViewById(R.id.btn_left);
        tv_tip.setText(emptip);
        btn_left.setText(emptipbn);

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperatingHouseActivity.this.finish();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(false);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
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
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_house_register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLeftImageView(R.drawable.arrow_back);
        registerRightImageView(R.drawable.icon_house_hitory);

        findViewById(R.id.layout_house_name).setOnClickListener(this);
        findViewById(R.id.layout_kanfang).setOnClickListener(this);
        findViewById(R.id.layout_chaoxiang).setOnClickListener(this);
        findViewById(R.id.layout_zhuangxiu).setOnClickListener(this);
        findViewById(R.id.layout_peitao).setOnClickListener(this);
        findViewById(R.id.layout_feature).setOnClickListener(this);
        findViewById(R.id.ListBuilding_Rel).setOnClickListener(this);
        findViewById(R.id.queryListEkecell_rel).setOnClickListener(this);

        findViewById(R.id.layout_chuzujian).setOnClickListener(this);
        findViewById(R.id.layout_fenzutiaojian).setOnClickListener(this);
        findViewById(R.id.layout_shuifei).setOnClickListener(this);
        findViewById(R.id.city_Rel).setOnClickListener(this);




        NotificationCenter.defaultCenter.addListener(NotificationKey.selectLoupan, this);
        if (houseSourceNodeInfo == null) {
            initHouseDataByAdd();
        } else {
            initHouseDataByEdit();
        }
        dataChange();
        moreApiHelper = new MoreApiHelper(this);

        //判断cust手机是否同时注册emp
        getUserIsEmp();
        getCityList();//开放城市

        mRgAction.setOnCheckedChangeListener(mRgActionOnCheckedChang);
        zhizu_select.setOnCheckedChangeListener(zhizu_selectOnCheckedChang);

    }
    private void getCityList() {
        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(this,
                ServerUrl.METHOD_queryListCity, obj.toString(), mHandler, true);
        clientHelper.isShowProgress(false);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.sendPost(true);
    }
    //是否直租
    private String flagnoneagencyrent = "";
    RadioGroup.OnCheckedChangeListener zhizu_selectOnCheckedChang = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            switch (checkedId){
                case R.id.rg_no_zhi:
                    flagnoneagencyrent = "false";
                    break;
                case R.id.rg_yes_zhi:
                    flagnoneagencyrent = "true";
                    break;
            }
        }
    };

    RadioGroup.OnCheckedChangeListener mRgActionOnCheckedChang = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId){
                case R.id.rb_zhengzu:
                    houseType = 0;
                    trade = "出租";
                    break;
                case R.id.rb_fenzu:
                    trade = "出租";
                    houseType = 1;
                    break;
                case R.id.rb_chushou:
                    trade = "出售";
                    houseType = 2;
                    break;
            }
            ZuShouChage();
        }
    };

    private void getUserIsEmp() {
        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(mContext,
                ServerUrl.METHOD_getUserIsEmp, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }
    // region初始化房源数据
    private void initHouseDataByEdit() {
        setTitle("房源修改");
        String hosueName = !StringCheckHelper.isEmpty(houseSourceNodeInfo.estatename) ? houseSourceNodeInfo.estatename : "";
        mTxtHouseName.setText(hosueName);
//        mBtEdit.setVisibility(View.VISIBLE);
    }

    private void initHouseDataByAdd() {
        setTitle("房屋登记");
//        mLayoutAdd.setVisibility(View.VISIBLE);
        mFangYuanTuList.add("add");
        mFangYuanAdapter = new FangYuanAdapter(this, mFangYuanTuList);
        mGridFangYuanTu.setAdapter(mFangYuanAdapter);
        mGridFangYuanTu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == mFangYuanTuList.size() - 1) {
                    Intent intent = new Intent(mContext, LocalImagePreviewActivityNew.class);
                    intent.putExtra("from_where", LocalImagePreviewActivityNew.FROM_ADD_HOUSE_FNAGYUANTU);
                    startActivityForResult(intent, ADD_ZHUZHAI_FANGYUANTU);
                }
            }
        });
    }
    //endregion

    //检查输入
    private boolean checkInput() {
        String name = mTxtHouseName.getText().toString().trim();
        if (StringCheckHelper.isEmpty(name)) {
            mTxtHouseNameMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请选择楼盘");
            return false;
        }
        if (trade.equals("出租") || trade.equals("分租")) {
            String rentprice = mEdtZuJin.getText().toString().trim();
            if (TextUtils.isEmpty(rentprice) || Integer.parseInt(rentprice) <= 0) {
                mTxtZushouMandatory.setVisibility(View.VISIBLE);
                mEdtZuJin.requestFocus();
                ToastUtils.show(mContext, "请输入正确的租金");
                return false;
            }
//            String ekerentpaymodedeposit = mEdtYaJin.getText().toString().trim();
//            String ekerentpaymodecash = mEdtShouFu.getText().toString().trim();
//            if (TextUtils.isEmpty(ekerentpaymodedeposit) || Integer.parseInt(ekerentpaymodedeposit) <= 0) {
////                mTxtPayTypeMandatory.setVisibility(View.VISIBLE);
//                mEdtYaJin.requestFocus();
//                ToastUtils.show(mContext, "请输入正确的押金");
//                return false;
//            }
//            if (TextUtils.isEmpty(ekerentpaymodecash) || Integer.parseInt(ekerentpaymodecash) <= 0) {
////                mTxtPayTypeMandatory.setVisibility(View.VISIBLE);
//                mEdtShouFu.requestFocus();
//                ToastUtils.show(mContext, "请输入正确的首付金额");
//                return false;
//            }
        }
//        else if (trade.equals("出售")) {
//            String price = mEdtShoujia.getText().toString().trim();
//            if (TextUtils.isEmpty(price) || Integer.parseInt(price) <= 0) {
////                mTxtShouJiaMandatory.setVisibility(View.VISIBLE);
//                mEdtShoujia.requestFocus();
//                ToastUtils.show(mContext, "请输入正确的售价");
//                return false;
//            }
//            String propertytax = mTxtPropertytax.getText().toString();
//            if (propertytax.equals("请选择")) {
//                mTxtKanFangMandatory.setVisibility(View.VISIBLE);
//                ToastUtils.show(mContext, "请输入正确的税费");
//                return false;
//            }
//        }
        else if (trade.equals("租售")) {
            String rentprice = mEdtZuJin.getText().toString().trim();
            if (TextUtils.isEmpty(rentprice) || Integer.parseInt(rentprice) <= 0) {
                mTxtZushouMandatory.setVisibility(View.VISIBLE);
                mEdtZuJin.requestFocus();
                ToastUtils.show(mContext, "请输入正确的租金");
                return false;
            }
//            String price = mEdtShoujia.getText().toString().trim();
//            if (TextUtils.isEmpty(price) || Integer.parseInt(price) <= 0) {
////                mTxtShouJiaMandatory.setVisibility(View.VISIBLE);
////                mEdtShoujia.requestFocus();
////                ToastUtils.show(mContext, "请输入正确的售价");
//                return false;
//            }
        }
        String mianji = mEdtMianji.getText().toString().trim();
        if (StringCheckHelper.isEmpty(mianji) || Integer.parseInt(mianji) <= 0) {
            mTxtMianJiMandatory.setVisibility(View.VISIBLE);
            mEdtMianji.requestFocus();
            ToastUtils.show(mContext, "请输入正确的楼盘面积");
            return false;
        }
        String floor = mEdtCeng.getText().toString().trim();
        if (StringCheckHelper.isEmpty(floor) || Integer.parseInt(floor) <= 0) {
            mTxtLoucengMandatory.setVisibility(View.VISIBLE);
            mEdtCeng.requestFocus();
            ToastUtils.show(mContext, "请输入正确的楼层");
            return false;
        }
        String floorAll = mEdtCengCount.getText().toString().trim();
        if (StringCheckHelper.isEmpty(floorAll) || Integer.parseInt(floorAll) <= 0) {
            mTxtLoucengMandatory.setVisibility(View.VISIBLE);
            mEdtCengCount.requestFocus();
            ToastUtils.show(mContext, "请输入正确的楼层总数");
            return false;
        }
        if (Integer.parseInt(floor) > Integer.parseInt(floorAll)) {
            mEdtCeng.requestFocus();
            ToastUtils.show(mContext, "当前户型不能大于楼层总数");
            return false;
        }
        String houeNo = mEdtFanghao.getText().toString().trim();
        if (StringCheckHelper.isEmpty(houeNo)) {
            mTxtFanghaoMandatory.setVisibility(View.VISIBLE);
            mEdtFanghao.requestFocus();
            ToastUtils.show(mContext, "请输入正确的房号");
            return false;
        }
        String kanfangType = mTxtKanFangType.getText().toString();
        if (kanfangType.equals("请选择")) {
            mTxtKanFangMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请选择看房方式");
            return false;
        }
        String counts = mEdtShi.getText().toString();
        String countt = mEdtTing.getText().toString();
        String countw = mEdtWei.getText().toString();
        String county = mEdtYangTai.getText().toString();
        if (TextUtils.isEmpty(counts) &&
                TextUtils.isEmpty(countt)
                && TextUtils.isEmpty(countw) &&
                TextUtils.isEmpty(county)) {
            mTxtMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请检查户型是否输入正确");

            return false;
        }
        if (counts.equals("0") ||
                counts.equals("0") ||
                counts.equals("0") ||
                counts.equals("0")) {
            mTxtMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请检查户型是否输入正确");
            return false;
        }


        //装修程度
        String propertydecoration = mTxtPropertydecoration.getText().toString().trim();
        if (propertydecoration.equals("请选择")) {
            mTxtZhuangxiuMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请选择装修程度");
            return false;
        }
        //朝向
        String propertydirection = mTxtPropertydecoration.getText().toString().trim();
        if (propertydirection.equals("请选择")) {
            mTxtChaoXiangMandatory.setVisibility(View.VISIBLE);
            ToastUtils.show(mContext, "请选择朝向");
            return false;
        }

        if (TextUtils.isEmpty(flagnoneagencyrent)){
            ToastUtil.showToast("请选择是否免佣直租");
        }
        return true;
    }

    //region  数据变化时
    private void dataChange() {
        mTxtHouseName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtHouseNameMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        mTxtZuJin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtZushouMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
//        mEdtShoujia.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                super.onTextChanged(charSequence, i, i1, i2);
//                if (charSequence.toString().length() > 0) {
////                    mTxtShouJiaMandatory.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        //押金
//        mEdtYaJin.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                super.onTextChanged(charSequence, i, i1, i2);
//                String shoufu = mEdtShouFu.getText().toString();
//                if (charSequence.toString().length() > 0 && shoufu.length() > 0) {
////                    mTxtPayTypeMandatory.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        //首付
//        mEdtShouFu.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                super.onTextChanged(charSequence, i, i1, i2);
////                String yajin = mEdtYaJin.getText().toString();
////                if (charSequence.toString().length() > 0 && yajin.length() > 0) {
//////                    mTxtPayTypeMandatory.setVisibility(View.INVISIBLE);
////                }
//            }
//        });
        //面积
        mEdtMianji.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtMianJiMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        //楼层
        mEdtCeng.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                String floorAll = mEdtCengCount.getText().toString();
                if (charSequence.toString().length() > 0 && floorAll.length() > 0) {
                    mTxtLoucengMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        mEdtCengCount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                String floor = mEdtCeng.getText().toString();
                if (charSequence.toString().length() > 0 && floor.length() > 0) {
                    mTxtLoucengMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        //房号
        mEdtFanghao.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtFanghaoMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });

        //户型
        mEdtShi.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        mEdtTing.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        mEdtWei.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });
        mEdtYangTai.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.toString().length() > 0) {
                    mTxtMandatory.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    //endregion


    @Override
    protected void goNext() {
        super.goNext();
        UIHelper.startActivity(this, HouseHistoryActivity.class);

    }

    //region看方法方式
    private void showKanFangFangShiDlg() {
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(
                R.layout.dlg_house_add_kanfangfangshi, null);

        final Button btn_liushi = (Button) viewContent
                .findViewById(R.id.btn_liushi);
        btn_liushi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTxtPropertylook.setText("留匙");
                mTxtKanFangMandatory
                        .setVisibility(View.INVISIBLE);
            }
        });

        final Button btn_lianxiyezhu = (Button) viewContent
                .findViewById(R.id.btn_lianxiyezhu);
        btn_lianxiyezhu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTxtPropertylook.setText("联系业主");
                mTxtKanFangMandatory
                        .setVisibility(View.INVISIBLE);
            }
        });

        final Button btn_qita = (Button) viewContent
                .findViewById(R.id.btn_qita);
        btn_qita.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTxtPropertylook.setText("其他");
                mTxtKanFangMandatory
                        .setVisibility(View.INVISIBLE);

            }
        });
        initDialog(dlg, viewContent);
        dlg.show();
    }

    //endregion
    //region  选择朝向
    private void showChaoXiangDlg() {
        final String chaoxiang[] = getResources().getStringArray(R.array.chaoxiang);
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                mContext, chaoxiang);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dlg.dismiss();
                mTxtPropertydecoration.setText(chaoxiang[position]);
                mTxtChaoXiangMandatory.setVisibility(View.INVISIBLE);
            }
        });

        initDialog(dlg, viewContent);

        dlg.show();
    }

    //endregion
    //region 装修程度
    private void showZhuangXiuChengDuDlg() {
        final String zhuangxiuchengdu[] = getResources().getStringArray(R.array.zhuangxiu);
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                mContext, zhuangxiuchengdu);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTxtPropertydecoration
                        .setText(zhuangxiuchengdu[position]);
                mTxtZhuangxiuMandatory
                        .setVisibility(View.INVISIBLE);
            }
        });

        initDialog(dlg, viewContent);
        dlg.show();
    }

    private void initDialog(Dialog dlg, View viewContent) {
        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(mContext, 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
    }

    //endregion
    //region  配套设置
    private void showPeiTaoSheShiDlg() {
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);
        String[] peitaosheshi = getResources().getStringArray(R.array.peitaosheshi);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomSelectableGridViewAdapter adapter = new CustomSelectableGridViewAdapter(
                mContext, peitaosheshi, null);
        gridview.setAdapter(adapter);
        String peitao = mTxtPropertyfurniture.getText().toString();
        if (!peitao.equals("请选择")) {
            String select[] = mTxtPropertyfurniture.getText().toString().split(" ");
            adapter.initSelect(select);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.setSelect(position);
            }
        });

        final Button btn_confirm = (Button) viewContent
                .findViewById(R.id.btn_confirm);
        btn_confirm.setVisibility(View.VISIBLE);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                StringBuilder stringBuilder = new StringBuilder();
                ArrayList<String> select = adapter.getSelect();
                if (select.size() > 0) {
                    for (int i = 0; i < select.size(); i++) {
                        stringBuilder.append(select.get(i)).append(" ");
                    }
                    mTxtPropertyfurniture.setText(stringBuilder.toString());
                } else {
                    mTxtPropertyfurniture.setText("请选择");
                }


            }
        });
        initDialog(dlg, viewContent);
        dlg.show();
    }

    //endregion
    //region  房源特点
    private MaidianGridViewAdapter maidianGridViewAdapter;
    private List<MaidianNodeInfo> mMaidianNodeInfos = new ArrayList<MaidianNodeInfo>();

    private void showMaidianDlg() {
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_tab_house_maidian,
                null);

        final Button btn_confirm = (Button) viewContent
                .findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                StringBuilder sbBuilder = new StringBuilder();
                for (MaidianNodeInfo node : mMaidianNodeInfos) {
                    if (node.isSelected()) {
                        sbBuilder.append(node.getDetail()).append(" ");
                    }
                }
                if (sbBuilder.toString().length() > 0) {
                    sbBuilder.deleteCharAt(sbBuilder.length() - 1);
                }

                if (sbBuilder.length() == 0) {
                    mTxtTeDianAdd.setText("点击添加");
                } else {
                    mTxtTeDianAdd.setText(sbBuilder.toString());
                }
            }
        });
        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.grid_view_maidian);

        maidianGridViewAdapter = new MaidianGridViewAdapter(mContext,
                mMaidianNodeInfos);
        gridview.setAdapter(maidianGridViewAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                MaidianNodeInfo node = mMaidianNodeInfos.get(position);

                if (!node.isSelected()) {
                    if (getMaidianSelectedCount() >= 4) {
                        return;
                    }
                }

                node.setSelected(!node.isSelected());
                maidianGridViewAdapter.notifyDataSetChanged();
            }
        });

        initDialog(dlg, viewContent);


        dlg.show();
    }

    private int getMaidianSelectedCount() {
        int count = 0;
        for (int i = 0; i < mMaidianNodeInfos.size(); i++) {
            if (mMaidianNodeInfos.get(i).isSelected()) {
                count++;
            }
        }

        return count;
    }

    private int getMaidianCanSelectedCount() {
        if (mMaidianNodeInfos.size() >= 4) {
            return 4;
        }

        return mMaidianNodeInfos.size();
    }

    private void getFangyuanFeatures() {
        JSONObject obj = new JSONObject();
        try {
            String property = trade;
            if (trade.equals("整租") || trade.equals("分租")) {
                property = "出租";
            }
            obj.put("property", property);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getFeatureByProperty, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取房源特点");
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion
    //region 分租间
    private void showChuZuJianDlg() {
        final String chuzujian[] = getResources().getStringArray(R.array.chuzujian);
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                mContext, chuzujian);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dlg.dismiss();
                mTxtChuzujian.setText(chuzujian[position]);
            }
        });

        initDialog(dlg, viewContent);

        dlg.show();
    }

    //endregion
    //region 分租条件
    private void showFenZuTiaoJianDlg() {
        final Dialog dlg = new Dialog(mContext, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);
        final String[] fenzutiaojian = getResources().getStringArray(R.array.fenzutiaojian);
        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                mContext, fenzutiaojian);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dlg.dismiss();
                mTxtFenzutiaojian.setText(fenzutiaojian[position]);
            }
        });

        initDialog(dlg, viewContent);

        dlg.show();
    }

    //endregion

    //endregion
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_house_name:
            case R.id.txt_house_name:
                //房屋名字
                if (!ClickUtils.isFastDoubleClick()) {
                    MapDialogFragment mapDialogFragment = new MapDialogFragment();
                    mapDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog);
                    mapDialogFragment.show(getSupportFragmentManager(), "map");
                }
                break;

            case R.id. ListBuilding_Rel://栋座
                if (!TextUtils.isEmpty(estateid)){
                    if (ListBuilding!=null){
                        showdialogBuilding(0, ListBuilding);
                    }else {
                        break;
                    }
                }else {
                    ToastUtil.showToast("楼盘ID不能为空");
                }

                break;

            case R.id. queryListEkecell_rel://单元
                if (!TextUtils.isEmpty(buildingid)){
                    if (ListBuilding!=null){
                        showdialogBuilding(1, Listcellid);
                    }else {
                        break;
                    }
                }else {
                    ToastUtil.showToast("栋座ID不能为空");
                }


                break;
            case R.id. Bn_ropertyInvalid://是否重号
                isCheck = false;
                ropertyInvalid();
                break;

            case R.id.btn_lijifabu://发布房源
                isCheck = true;
                ropertyInvalid();// 检查是否重号再调接口

                break;
            /*case R.id.btn_daifabu:
                if (AppContext.getInstance().isLogin()) {
                    if (checkInput()) {
                        submitHouseInfo("暂存");
                    }
                } else {

                    UIHelper.startToLogin(this);
                }

                break;*/
            /*case R.id.btn_edit:
                if (AppContext.getInstance().isLogin()) {
                    if (checkInput()) {
                        submitHouseInfo("待审核");
                    }
                } else {

                    UIHelper.startToLogin(this);

                }
                break;*/
            ///   0 税费说明 1 看房方式  2 朝向  3 装修程度 4 配套设置
            case R.id.layout_shuifei:
                //税费
//                if (PropertyTax != null) {
//                    showdialog(0, PropertyTax);
//                } else {
                queryListReference(PROPERTY_TAX);
//                }
                break;
            case R.id.layout_kanfang:
//                if (PropertyLook != null) {
//                    showdialog(1, PropertyLook);
//                } else {
                queryListReference(PROPERTY_LOOK);
//                }
                break;
            case R.id.layout_chaoxiang:
//                if (PropertyDirection != null) {
//                    showdialog(2, PropertyDirection);
//                } else {
                queryListReference(PROPERTY_DIRECTION);
//                }
                break;
            case R.id.layout_zhuangxiu:
//                if (PropertyDecoration != null) {
//                    showdialog(3, PropertyDecoration);
//                } else {
                queryListReference(PROPERTY_DECORATION);
//                }
                break;
            case R.id.layout_peitao:
//                if (PropertyFurniture != null) {
//                    showdialog(4, PropertyFurniture);
//                } else {
                queryListReference(PROPERTY_FURNITURE);
//                }
                break;
            case R.id.layout_feature:
                getFangyuanFeatures();
                break;
            case R.id.layout_chuzujian:
                //出租间
                showChuZuJianDlg();
                break;
            case R.id.layout_fenzutiaojian:
                //分租
                showFenZuTiaoJianDlg();
                break;

            case R.id.city_Rel:
                showHouseList();
                break;

        }

    }

    // 房源列表
    private OpenCity city;
    public void showHouseList() {
        View view = LayoutInflater.from(this).inflate(R.layout.dlg_city, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setAnimationStyle(R.style.AnimationTopFade);//设置动画
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);   //设置外部点击关闭ppw窗口
        popWindow.setFocusable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(city_Rel, 0, 0);

        final ListView listview = (ListView) view.findViewById(R.id.recycleview);

        listview.setAdapter(new CommonListAdapter<OpenCity>(this,
                R.layout.item_search_house_menu, cities) {
            @Override
            public void convert(ViewHolder holder, OpenCity openCity,
                                int position) {
                ImageView mIvIcon = holder.findViewById(R.id.iv_city_icon);
                TextView mTxtName = holder.findViewById(R.id.txt_city_name);
                mTxtName.setText(openCity.txt);
                if (StringCheckHelper.isEmpty(openCity.cityicon)) {
                    mIvIcon.setImageBitmap(BitmapUtils.stringtoBitmap(cities.get(0).cityicon));
                } else {
                    mIvIcon.setImageBitmap(BitmapUtils.stringtoBitmap(openCity.cityicon));

                }

            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                city = (OpenCity) parent.getItemAtPosition(position);
//                cityId = city.val;
                city_name_tv.setText(city.txt);
//                getCityLocation(city.txt, city.val);
//                houseCovers = null;
                popWindow.dismiss();
            }
        });

    }



    //是否重号
    private void ropertyInvalid() {
        JSONObject obj = new JSONObject();
        try {
            String houeNo = mEdtFanghao.getText().toString().trim();
            if (TextUtils.isEmpty(estateid)){
                ToastUtil.showToast("楼盘ID不能为空");
            }else if (TextUtils.isEmpty(buildingid)){
                ToastUtil.showToast("栋座ID不能为空");
            }else if (TextUtils.isEmpty(cellid)){
                ToastUtil.showToast("单元ID不能为空");
            }else if (TextUtils.isEmpty(houeNo)){
                ToastUtil.showToast("房号不能为空");
            }else if (TextUtils.isEmpty(trade)){
                ToastUtil.showToast("请选择方式");
            }else {
                obj.put("estateid", estateid);
                obj.put("buildingid", buildingid);
                obj.put("cellid", cellid);
                obj.put("roomno", houeNo);//
                obj.put("trade", trade);
                ClientHelper clientHelper = new ClientHelper(mContext,
                        ServerUrl.METHOD_getPropertyInvalid, obj.toString(), mHandler);
                clientHelper.setShowProgressMessage("正在重盘检测");
                clientHelper.isShowProgress(false);
                clientHelper.sendPost(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //提交房源信息
    private void submitHouseInfo(String status) {
        JSONObject obj = new JSONObject();

        try {
            if (house.estateid != null) {
                // 选择的楼盘
                obj.put("estateid", house.estateid);
                obj.put("opestatedistrict", house.houseName);
            } else {
                obj.put("estateid", "00000000000000000000000000000000");
                obj.put("opestatelon", house.opestatelon);
                obj.put("opestatelat", house.opestatelat);
                obj.put("opestatedistrict", house.opestatedistrict);
                obj.put("opestatename", house.houseName);
            }
            //租售方式
            obj.put("trade", trade);
            //面积
            String square = mEdtMianji.getText().toString();
            obj.put("square", square);
            //楼层
            String floor = mEdtCeng.getText().toString().trim();
            obj.put("floor", floor);
            //楼层总数
            String floorall = mEdtCengCount.getText().toString().trim();
            obj.put("floorall", floorall);
            //房号
            String roomno = mEdtFanghao.getText().toString().trim();
            obj.put("roomno", roomno);
            //租金
            String rentprice = mEdtZuJin.getText().toString().trim();
            //售价
//            String price = mEdtShoujia.getText().toString().trim();
            //押金 首付
//            String ekerentpaymodedeposit = mEdtYaJin.getText().toString().trim();
//            String ekerentpaymodecash = mEdtShouFu.getText().toString().trim();
            if (trade.contains("出租") || trade.contains("分租")) {
                obj.put("rentprice", Integer.valueOf(rentprice));
                obj.put("rentunitname", "元/月");
//                obj.put("ekerentpaymodedeposit", ekerentpaymodedeposit);
//                obj.put("ekerentpaymodecash", ekerentpaymodecash);
            } else if (trade.contains("出售")) {
//                obj.put("price", Integer.valueOf(price));
                obj.put("unitname", "元/月");
            } else {
//                obj.put("price", Integer.valueOf(price));
                obj.put("unitname", "元/月");
                obj.put("rentprice", Integer.valueOf(rentprice));
                obj.put("rentunitname", "元/月");
            }
            // 分租
//            if (trade.equals("分租")) {
//                String subletroom = mTxtChuzujian.getText().toString();
//                // 分租条件
//                String subletcondition = mTxtFenzutiaojian.getText()
//                        .toString();
//                String subletnumber = mEdtHezu.getText()
//                        .toString();
//                if (StringUtils.isNotEmpty(subletroom)) {
//                    obj.put("subletroom", subletroom);
//                }
//                if (StringUtils.isNotEmpty(subletcondition)) {
//                    obj.put("subletcondition", subletcondition);
//                }
//                if (StringUtils.isNotEmpty(subletnumber)) {
//                    obj.put("subletnumber", subletnumber);
//                }
//            }
            //税费说明
//            String propertytax = mTxtPropertytax.getText().toString().trim();
//            obj.put("propertytax", propertytax);
            //看房方式
            String propertylook = mTxtPropertylook.getText().toString().trim();
            obj.put("propertylook", propertylook);

            //户型
            String countf = mEdtShi.getText().toString();
            String countt = mEdtTing.getText().toString();
            String countw = mEdtWei.getText().toString();
            String county = mEdtYangTai.getText().toString();
            obj.put("countf", countf);
            obj.put("countt", countt);
            obj.put("countw", countw);
            obj.put("county", county);
            //朝向
            String propertydirection = mTxtPropertydecoration.getText().toString().trim();
            obj.put("propertydirection", propertydirection);
            //装修程度
            String propertydecoration = mTxtPropertydecoration.getText().toString().trim();
            obj.put("propertydecoration", propertydecoration);
            //配套设施
            String propertyfurniture = mTxtPropertyfurniture.getText().toString().trim();
            obj.put("propertyfurniture", propertyfurniture);
            //日期

            //房源特点
            String ekefeature = mTxtTeDianAdd.getText().toString();
            obj.put("ekefeature", ekefeature);
            String remark = mEdtContent.getText().toString();
            obj.put("status", status);
            obj.put("remark", remark);
            JSONArray listjson1 = new JSONArray();
            for (int i = 0; i < mFangYuanTuList.size() - 1; i++) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("filePath", Base64.encodeToString(
                            BitmapUtils.getBitmapByte(mFangYuanTuList.get(i)),
                            Base64.DEFAULT));
                    listjson1.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            obj.put("list", listjson1);

            obj.put("flagnoneagencyrent", flagnoneagencyrent);

            String jsonString = obj.toString();
            jsonString = jsonString.replace("\\/", "/");
            String urlString;
            if (status.equals("待审核")) {
                urlString = ServerUrl.METHOD_insertPropertyByWT;
            } else {
                urlString = ServerUrl.METHOD_insertPropertyDj;

            }
            ClientHelper clientHelper = new ClientHelper(mContext,
                    urlString, jsonString, mHandler);
            clientHelper.setShowProgressMessage("正在提交数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //房屋租售变化
    private void ZuShouChage() {

        switch (houseType){
            case 0:
                fenzu_linear.setVisibility(View.GONE);
                shuojia_Linear.setVisibility(View.GONE);
                break;
            case 1:
                mLayoutChuZu.setVisibility(View.VISIBLE);
                fenzu_linear.setVisibility(View.VISIBLE);
                shuojia_Linear.setVisibility(View.GONE);
                break;
            case 2:
                mLayoutChuZu.setVisibility(View.GONE);
                fenzu_linear.setVisibility(View.GONE);
                shuojia_Linear.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.selectLoupan)) {
            house = (House) notification.object;
            if (!TextUtils.isEmpty(house.houseName)) {
                mTxtHouseName.setText(house.houseName);
                estateid = house.estateid;
                //[自助登记]根据楼盘获取栋座
                queryListBuilding(estateid);
            }
        }
        return false;
    }
    //[自助登记]根据楼盘获取栋座
    private void queryListBuilding(String estateid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("estateid",estateid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_queryListBuilding, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在提交数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //查询
    private void queryListReference(String reference) {
        showLoading();
        moreApiHelper.queryListReference(reference);
    }

    //endregion
    //region  选择朝向   index    0 税费说明 1 看房方式  2 朝向  3 装修程度 4 配套设置
    private void showdialog(final int index, final List houseReferences) {
        View view = inflateContentView(R.layout.layout_house_building);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.build_recycleview);
        if (index == 0 || index == 1) {
            recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_5)));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            recyclerView.addItemDecoration(new GridSpaceItemDecoration(3, getResources().getDimensionPixelSize(R.dimen.dimen_5), true));
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false));
        }

        final AlertDialog alertDialog = DialogHelper.getViewDialog(mContext, view);
        recyclerView.setAdapter(new BaseRecyclerViewAdapter(mContext, (ArrayList) houseReferences) {
            @Override
            public void setUpData(RecyclerView.ViewHolder holder, final int position, int viewType, Object data) {
                RecycleviewViewHolder recycleviewViewHolder = (RecycleviewViewHolder) holder;
//                final HouseReference houseReference = (HouseReference) data;
                TextView mTxtHouseName = recycleviewViewHolder.getView(R.id.tv_maidian);
                if (houseReferences != null) {
//                    String name = !StringCheckHelper.isEmpty(houseReference.itemvalue) ? houseReference.itemvalue : "";
                    mTxtHouseName.setText(houseReferences.get(position)+"");
                }
                recycleviewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index == 0) {
                            mTxtPropertytax.setText(houseReferences.get(position)+"");
                            mTxtShuiFeiMandatory.setVisibility(View.INVISIBLE);
                        } else if (index == 1) {
                            mTxtPropertylook.setText(houseReferences.get(position)+"");
                            mTxtKanFangMandatory.setVisibility(View.INVISIBLE);
                        } else if (index == 2) {
                            mTxtPropertydirection.setText(houseReferences.get(position)+"");
                            mTxtChaoXiangMandatory.setVisibility(View.INVISIBLE);
                        } else if (index == 3) {
                            mTxtPropertydecoration.setText(houseReferences.get(position)+"");
                            mTxtZhuangxiuMandatory.setVisibility(View.INVISIBLE);
                        } else if (index == 4) {
                            mTxtPropertyfurniture.setText(houseReferences.get(position)+"");
                        }


                        alertDialog.cancel();

                    }
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecycleviewViewHolder(inflateContentView(R.layout.dlg_maidian_item));
            }
        });
        alertDialog.show();
    }

    //endregion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        ToastUtil.showToast(resultCode+"");
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ZHUZHAI_FANGYUANTU) {
                String img_url = data.getStringExtra("img_url");
                updateFangYuanTu(img_url);
            } else if (requestCode == LOPAN_NAME) {
                String name = data.getStringExtra("name");
                mTxtHouseName.setText(name);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateFangYuanTu(String imgUrl) {
        mFangYuanTuList.add(mFangYuanTuList.size() - 1, imgUrl);
        mFangYuanAdapter.notifyDataSetChanged();
    }

    @Override
    public void callback(BaseRestApi baseRestApi) {
        hideLoading();
        if (baseRestApi.getTag().equals(PROPERTY_TAX)) {
            //税费说明
            List jsonArray = JSONUtils.getStringList(baseRestApi.responseData, "data", null);
            if (jsonArray.size() !=0){
                showdialog(0, jsonArray);
            }
//            if (jsonArray != null) {
//                PropertyTax = JSONUtils.getObjectList(jsonArray, HouseReference.class);
//                if (PropertyTax != null && PropertyTax.size() > 0) {
//                    showdialog(0, PropertyTax);
//                }
//            }
        } else if (baseRestApi.getTag().equals(PROPERTY_LOOK)) {
            //看房方式

            List jsonArray = JSONUtils.getStringList(baseRestApi.responseData, "data", null);
            if (jsonArray.size() !=0){
                showdialog(1, jsonArray);
            }

//            if (jsonArray != null) {
//                PropertyLook = JSONUtils.getObjectList(jsonArray, HouseReference.class);
//                if (PropertyLook != null && PropertyLook.size() > 0) {
//
//                }
//            }
        } else if (baseRestApi.getTag().equals(PROPERTY_DIRECTION)) {
            //朝向
            List jsonArray = JSONUtils.getStringList(baseRestApi.responseData, "data", null);
            if (jsonArray.size() !=0){
                showdialog(2, jsonArray);
            }
//            if (jsonArray != null) {
//                PropertyDirection = JSONUtils.getObjectList(jsonArray, HouseReference.class);
//                if (PropertyDirection != null && PropertyDirection.size() > 0) {
//                    showdialog(2, PropertyDirection);
//                }
//            }
        } else if (baseRestApi.getTag().equals(PROPERTY_DECORATION)) {
            //装修程度
            List jsonArray = JSONUtils.getStringList(baseRestApi.responseData, "data", null);
            if (jsonArray.size() !=0){
                showdialog(3, jsonArray);
            }
//            if (jsonArray != null) {
//                PropertyDecoration = JSONUtils.getObjectList(jsonArray, HouseReference.class);
//                if (PropertyDecoration != null && PropertyDecoration.size() > 0) {
//                    showdialog(3, PropertyDecoration);
//                }
//            }
        } else if (baseRestApi.getTag().equals(PROPERTY_FURNITURE)) {
            //配套设施
            List jsonArray = JSONUtils.getStringList(baseRestApi.responseData, "data", null);
            if (jsonArray.size() !=0){
                showdialog(4, jsonArray);
            }
//            if (jsonArray != null) {
//                PropertyFurniture = JSONUtils.getObjectList(jsonArray, HouseReference.class);
//                if (PropertyFurniture != null && PropertyFurniture.size() > 0) {
//                    showdialog(4, PropertyFurniture);
//                }
//            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            OperatingHouseActivity.this.finish();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
