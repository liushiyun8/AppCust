package com.eke.cust.tabmore.house_register_activity.house_add;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.House;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.MaidianGridViewAdapter;
import com.eke.cust.tabhouse.MaidianNodeInfo;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.CalendarUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.utils.TransformUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.droidparts.annotation.inject.InjectResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import foundation.base.fragment.BaseFragment;
import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;

import static com.eke.cust.R.id.tv_kanfangfangshi_bitian_tip;
import static com.eke.cust.R.id.tv_louceng_bitian_tip;

public class FragmentAddZhuZhai extends BaseFragment implements OnClickListener {
    private Context mContext;
    private RelativeLayout mRelativeLayout_rl_loupanmingcheng;
    private TextView mTextView_tv_loupanmingcheng_bitian_tip;
    private EditText mEdtext_tv_loupanmingcheng;
    private RadioGroup mRadioGroup_rg_action;
    private LinearLayout mLinearLayout_ll_zhengzu;
    private TextView mTextView_tv_zujin_bitian_tip;
    private EditText mEditText_et_zujin;
    private TextView mTextView_tv_zhifufangshi_bitian_tip;
    private EditText mEditText_et_zhifufangshi_ya_num;
    private EditText mEditText_et_zhifufangshi_fu_num;
    private LinearLayout mLinearLayout_ll_chushou;
    private TextView mTextView_tv_chushou_bitian_tip;
    private EditText mEditText_et_chushou_shoujia;
    private RelativeLayout mRelativeLayout_rl_shuifeishuoming;
    private TextView mTextView_tv_shuifeishuoming_bitian_tip;
    private TextView mTextView_tv_shuifeishuoming;
    private LinearLayout mLinearLayout_ll_zhushou;
    private TextView mTextView_tv_zhushou_bitian_tip;
    private EditText mEditText_et_zhushou_zhujin;
    private TextView mTextView_tv_zhushou_shoujia_bitian_tip;
    private EditText mEditText_et_zhushou_shoujia;
    private LinearLayout mLinearLayout_ll_fangshi_fenzu;
    private TextView mTextView_tv_chuzujian;
    private TextView mTextView_tv_fenzutiaojian;
    private TextView mTextView_tv_mianji_bitian_tip;
    private EditText mEditText_et_mianji;
    private TextView mTextView_tv_louceng_bitian_tip;
    private EditText mEditText_et_louceng_di_num;
    private EditText mEditText_et_louceng_total_num;
    private TextView mTextView_tv_fanghao_bitian_tip;
    private EditText mEditText_et_fanghao;
    private RelativeLayout mRelativeLayout_rl_kanfangfangshi;
    private TextView mTextView_tv_kanfangfangshi_bitian_tip;
    private TextView mTextView_tv_kanfangfangshi;
    private TextView mTextView_tv_huxing_bitian_tip;
    private EditText mEditText_et_huxing_shi_num;
    private EditText mEditText_et_huxing_ting_num;
    private EditText mEditText_et_huxing_wei_num;
    private EditText mEditText_et_huxing_yangtai_num;
    private RelativeLayout mRelativeLayout_rl_chaoxiang;
    private RelativeLayout mRelativeLayout_rl_chuzujian;
    private RelativeLayout mRelativeLayout_rl_fenzutiaojian;
    private TextView mTextView_tv_chaoxiang_bitian_tip;
    private TextView mTextView_tv_chaoxiang;
    private RelativeLayout mRelativeLayout_rl_zhuangxiuchengdu;
    private TextView mTextView_tv_zhuangxiuchengdu_bitian_tip;
    private TextView mTextView_tv_zhuangxiuchengdu;
    private RelativeLayout mRelativeLayout_rl_peitaosheshi;
    private TextView mTextView_tv_peitaosheshi;
    private RelativeLayout mRelativeLayout_rl_jiaofangriqi;
    private TextView mTextView_tv_jiaofangriqi_bitian_tip;
    private TextView mTextView_tv_jiaofangriqi;
    private RelativeLayout mRelativeLayout_rl_fangyuantedian;
    private TextView mTextView_tv_fangyuantedian;
    private EditText mEditText_et_qita_content;
    private Button mButton_btn_lijifabu;
    private Button mButton_btn_daifabu;
    private GridView mGridView_fangyuantu;

    private LinearLayout mLinearLayout_ll_maidian;
    private TextView mTextView_tv_maidian_1;
    private TextView mTextView_tv_maidian_2;
    private TextView mTextView_tv_maidian_3;
    private TextView mTextView_tv_maidian_4;

    private static final int TEXT_SELECTED_COLOR = 0xff00ff;




    // 楼盘
    private House house;
    private String trade = "出租";
    //房间
    @InjectResource(value = R.array.chuzujian)
    private String[] chuzujian;
    //限制
    @InjectResource(value = R.array.fenzutiaojian)
    private String[] fenzutiaojian;
    //方位
    @InjectResource(value = R.array.chao_xiang)
    private String[] chaoxiang;
    @InjectResource(value = R.array.peitaosheshi)
    private String[] peitaosheshi;
    private boolean[] peitaosheshi_selected = {false, false, false, false,
            false, false, false, false, false, false, false, false};
    //装修程度
    @InjectResource(value = R.array.zhuangxiuchengdu)
    private String[] zhuangxiuchengdu;
    /*
     * private static final String[] maidianName = { "超大阳台", "带露台", "限租女生",
     * "超低价", "小区唯一", "可以办公", "双阳台", "交通便利", "带天台花园", "可以注册", "首次出租", "预留钥匙",
     * "视野开阔", "室内保养好", "带家电", "近超市", "近公园", "双地铁物业", "近地铁", "顶层复式", "小区中间",
     * "园中园", "超安静", "楼王位置", "拎包入住", "花园洋房", "高层", "业主出国", "低密度小区" , "可以注册",
     * "首次出租", "预留钥匙", "视野开阔", "室内保养好", "带家电", "近超市", "近公园", "双地铁物业", "近地铁",
     * "顶层复式", "小区中间", "园中园", "超安静", "楼王位置", "拎包入住", "花园洋房", "高层", "业主出国",
     * "低密度小区"};
     */
    private List<MaidianNodeInfo> mMaidianNodeInfos = new ArrayList<MaidianNodeInfo>();

    private List<String> mFangYuanTuList = new ArrayList<String>();
    private FangYuanAdapter mFangYuanAdapter;

    private int currentPage = 1;
    private int pageSize = 20;
    private int totalRecords = 0;
    private String orderColumn = null;
    private boolean orderASC = false;
    private int recordStart = 0;
    private int totalPages = 0;

    private int mFromYear_prior_init = 0;
    private int mFromMonth_prior_init = 0;
    private int mFromDay_prior_init = 0;
    private int mToYear_prior_init = 0;
    private int mToMonth_prior_init = 0;
    private int mToDay_prior_init = 0;

    private int mFromYear = 0;
    private int mFromMonth = 0;
    private int mFromDay = 0;
    private int mToYear = 0;
    private int mToMonth = 0;
    private int mToDay = 0;

    private String mStrFromDate = null;
    private String mStrToDate = null;
    private String mStrFromDateParam = null;
    private String mStrToDateParam = null;

    private TextView mTv_no_house_filter_item;
    private EditText et_quxian;
    private EditText et_pianqu;
    private EditText et_search;
    private PullToRefreshListView mListView;
    private List<HouseNameNodeInfo> mHouseNameNodeInfos = new ArrayList<HouseNameNodeInfo>();
    private HouseNameListAdapter mHouseNameListAdapter;
    private String estateid;
    private String estatename;
    private String estatequxian;
    private String estatepianqu;
    private boolean isClickSet;
    private ScrollView scrollView;
//region 接口
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case CalendarUtil.MSG_UPDATE_DATE_FROM: {
                        Bundle bundle = msg.getData();
                        mFromYear = bundle.getInt("year");
                        mFromMonth = bundle.getInt("month");
                        mFromDay = bundle.getInt("day");

                        mStrFromDate = mFromYear + "-"
                                + String.format("%02d", mFromMonth) + "-"
                                + String.format("%02d", mFromDay);
                        mStrFromDateParam = String.format("%02d", mFromMonth)
                                + String.format("%02d", mFromDay) + mFromYear;
                        mTextView_tv_jiaofangriqi.setText(mStrFromDate);
                        mTextView_tv_jiaofangriqi_bitian_tip
                                .setVisibility(View.INVISIBLE);
                        mTextView_tv_jiaofangriqi.setTextColor(TEXT_SELECTED_COLOR);
                    }
                    break;

                    case CalendarUtil.MSG_UPDATE_DATE_OTHER: {
                        Bundle bundle = msg.getData();
                        String data = bundle.getString("data");
                        mTextView_tv_jiaofangriqi.setText(data);
                        mTextView_tv_jiaofangriqi_bitian_tip
                                .setVisibility(View.INVISIBLE);
                        mTextView_tv_jiaofangriqi.setTextColor(TEXT_SELECTED_COLOR);
                    }
                    break;

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
                                        .equals(ServerUrl.METHOD_insertPropertyByWT)) {
                                    dialog();
                                } else if (request_url
                                        .equals(ServerUrl.METHOD_insertPropertyDj)) {
                                    dialog();

                                }
                                if (request_url
                                        .equals(ServerUrl.METHOD_queryCityListPage)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    currentPage = obj_data.optInt("currentPage");
                                    pageSize = obj_data.optInt("pageSize");
                                    totalRecords = obj_data.optInt("totalRecords");
                                    orderColumn = obj_data.optString("orderColumn");
                                    orderASC = obj_data.optBoolean("orderASC");

                                    recordStart = obj_data.optInt("recordStart");
                                    totalPages = obj_data.optInt("totalPages");

                                    JSONArray jsonArray = obj_data
                                            .optJSONArray("data");
                                    if (jsonArray != null) {
                                        if (jsonArray.length() == 0) {
                                            mTv_no_house_filter_item
                                                    .setVisibility(View.VISIBLE);
                                            mListView.setVisibility(View.INVISIBLE);
                                            estateid = null;
                                            estatename = null;
                                        } else {
                                            mTv_no_house_filter_item
                                                    .setVisibility(View.GONE);
                                            mListView.setVisibility(View.VISIBLE);

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray
                                                        .getJSONObject(i);
                                                if (obj != null) {
                                                    HouseNameNodeInfo node;
                                                    try {
                                                        node = TransformUtil
                                                                .getEntityFromJson(
                                                                        obj,
                                                                        HouseNameNodeInfo.class);

                                                        if (node != null) {
                                                            if (estateid != null
                                                                    && estateid
                                                                    .equals(node
                                                                            .getEstateid())) {
                                                                node.setSelected(true);
                                                                isClickSet = true;

                                                                et_quxian
                                                                        .setText(node
                                                                                .getAreaname());
                                                                et_pianqu
                                                                        .setText(node
                                                                                .getDistrictname());
                                                                et_search
                                                                        .setText(node
                                                                                .getEstatename());
                                                            }
                                                            mHouseNameNodeInfos
                                                                    .add(node);
                                                        }

                                                    } catch (java.lang.InstantiationException e) {
                                                        // TODO Auto-generated catch
                                                        // block
                                                        e.printStackTrace();
                                                    } catch (IllegalAccessException e) {
                                                        // TODO Auto-generated catch
                                                        // block
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                        }

                                        if (mHouseNameListAdapter != null) {
                                            mHouseNameListAdapter
                                                    .notifyDataSetChanged();
                                        }

                                    }

                                    mListView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mListView.onRefreshComplete();
                                        }
                                    }, 300);
                                }
                                // else if
                                // (request_url.equals(ServerUrl.METHOD_checkEKEMaper))
                                // {
                                // String data = jsonObject.optString("data");
                                // if (data.equals("success")) {//有权限
                                // mEt_chzs.setText("");
                                // Intent intent = new Intent(BarcodeActivity.this,
                                // CHZSActivity.class);
                                // startActivity(intent);
                                // }
                                // }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getActivity().getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getActivity().getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };
    //endregion

    protected void dialog() {
        ToastUtils.show(mContext, "房源登记成功");
//        AlertDialog.Builder builder = new Builder(getActivity());
//        builder.setMessage("房源登记成功");
//        builder.setTitle("提示");
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                clearEditView();
//                scrollView.fullScroll(View.FOCUS_UP);
//            }
//        });
//        builder.create().show();
    }


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.layout_house_add_zhuzhai);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mMaidianNodeInfos.clear();
        /*
         * for (int i = 0; i < maidianName.length; i++) { MaidianNodeInfo node =
		 * new MaidianNodeInfo(); node.setDetail(maidianName[i]);
		 * mMaidianNodeInfos.add(node); }
		 */

        mFangYuanTuList.add("add");
        mFangYuanAdapter = new FangYuanAdapter(getActivity(), mFangYuanTuList);
        mGridView_fangyuantu.setAdapter(mFangYuanAdapter);
        mGridView_fangyuantu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position == mFangYuanTuList.size() - 1) {
                    Intent intent = new Intent(mContext,
                            LocalImagePreviewActivity.class);
                    intent.putExtra("from_where",
                            Constants.FROM_ADD_HOUSE_FNAGYUANTU);
                    startActivityForResult(intent,
                            HouseAddActivity.ADD_ZHUZHAI_FANGYUANTU);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setContextThis(Context context) {
        this.mContext = context;
    }

    public void updateFangYuanTu(String imgUrl) {
        mFangYuanTuList.add(mFangYuanTuList.size() - 1, imgUrl);
        mFangYuanAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(View view) {
        initActivity(view);
    }

    private void initActivity(View view) {
        NotificationCenter.defaultCenter.addListener(
                NotificationKey.selectLoupan, this);
        mRelativeLayout_rl_loupanmingcheng = (RelativeLayout) view
                .findViewById(R.id.rl_loupanmingcheng);

        mEdtext_tv_loupanmingcheng = (EditText) view
                .findViewById(R.id.tv_loupanmingcheng);
        mRadioGroup_rg_action = (RadioGroup) view.findViewById(R.id.rg_action);
        mLinearLayout_ll_zhengzu = (LinearLayout) view
                .findViewById(R.id.ll_zhengzu);
        mTextView_tv_zujin_bitian_tip = (TextView) view
                .findViewById(R.id.tv_zujin_bitian_tip);
        mEditText_et_zujin = (EditText) view.findViewById(R.id.et_zujin);
        //支付方式
        mTextView_tv_zhifufangshi_bitian_tip = (TextView) view
                .findViewById(R.id.tv_zhifufangshi_bitian_tip);
        mEditText_et_zhifufangshi_ya_num = (EditText) view
                .findViewById(R.id.et_zhifufangshi_ya_num);
        mEditText_et_zhifufangshi_fu_num = (EditText) view
                .findViewById(R.id.et_zhifufangshi_fu_num);
        mLinearLayout_ll_chushou = (LinearLayout) view
                .findViewById(R.id.ll_chushou);
        mTextView_tv_chushou_bitian_tip = (TextView) view
                .findViewById(R.id.tv_chushou_shoujia_bitian_tip);
        mEditText_et_chushou_shoujia = (EditText) view
                .findViewById(R.id.et_chushou_shoujia);
        mRelativeLayout_rl_shuifeishuoming = (RelativeLayout) view
                .findViewById(R.id.rl_shuifeishuoming);
        mTextView_tv_shuifeishuoming_bitian_tip = (TextView) view
                .findViewById(R.id.tv_shuifeishuoming_bitian_tip);
        mTextView_tv_shuifeishuoming = (TextView) view
                .findViewById(R.id.tv_shuifeishuoming);
        mLinearLayout_ll_zhushou = (LinearLayout) view
                .findViewById(R.id.ll_zhushou);
        mTextView_tv_zhushou_bitian_tip = (TextView) view
                .findViewById(R.id.tv_zhushou_bitian_tip);
        mEditText_et_zhushou_zhujin = (EditText) view
                .findViewById(R.id.et_zhushou_zhujin);
        mTextView_tv_zhushou_shoujia_bitian_tip = (TextView) view
                .findViewById(R.id.tv_zhushou_shoujia_bitian_tip);
        mEditText_et_zhushou_shoujia = (EditText) view
                .findViewById(R.id.et_zhushou_shoujia);
        mLinearLayout_ll_fangshi_fenzu = (LinearLayout) view
                .findViewById(R.id.ll_fangshi_fenzu);
        mTextView_tv_chuzujian = (TextView) view
                .findViewById(R.id.tv_chuzujian);
        mTextView_tv_fenzutiaojian = (TextView) view
                .findViewById(R.id.tv_fenzutiaojian);
        //面积
        mTextView_tv_mianji_bitian_tip = (TextView) view
                .findViewById(R.id.tv_mianji_bitian_tip);
        mEditText_et_mianji = (EditText) view.findViewById(R.id.et_mianji);
        //楼层
        mTextView_tv_louceng_bitian_tip = (TextView) view
                .findViewById(tv_louceng_bitian_tip);
        mEditText_et_louceng_di_num = (EditText) view
                .findViewById(R.id.et_louceng_di_num);
        mEditText_et_louceng_total_num = (EditText) view
                .findViewById(R.id.et_louceng_total_num);
        mTextView_tv_fanghao_bitian_tip = (TextView) view
                .findViewById(R.id.tv_fanghao_bitian_tip);
        mEditText_et_fanghao = (EditText) view.findViewById(R.id.et_fanghao);
        mRelativeLayout_rl_kanfangfangshi = (RelativeLayout) view
                .findViewById(R.id.rl_kanfangfangshi);
        mTextView_tv_kanfangfangshi_bitian_tip = (TextView) view
                .findViewById(tv_kanfangfangshi_bitian_tip);
        mTextView_tv_kanfangfangshi = (TextView) view
                .findViewById(R.id.tv_kanfangfangshi);
        mTextView_tv_huxing_bitian_tip = (TextView) view
                .findViewById(R.id.tv_huxing_bitian_tip);
        mEditText_et_huxing_shi_num = (EditText) view
                .findViewById(R.id.et_huxing_shi_num);
        mEditText_et_huxing_ting_num = (EditText) view
                .findViewById(R.id.et_huxing_ting_num);
        mEditText_et_huxing_wei_num = (EditText) view
                .findViewById(R.id.et_huxing_wei_num);
        mEditText_et_huxing_yangtai_num = (EditText) view
                .findViewById(R.id.et_huxing_yangtai_num);
        mRelativeLayout_rl_chaoxiang = (RelativeLayout) view
                .findViewById(R.id.rl_chaoxiang);
        mRelativeLayout_rl_chuzujian = (RelativeLayout) view
                .findViewById(R.id.rl_chuzujian);
        mRelativeLayout_rl_fenzutiaojian = (RelativeLayout) view
                .findViewById(R.id.rl_fenzutiaojian);
        mTextView_tv_chaoxiang_bitian_tip = (TextView) view
                .findViewById(R.id.tv_chaoxiang_bitian_tip);
        mTextView_tv_chaoxiang = (TextView) view
                .findViewById(R.id.tv_chaoxiang);
        mRelativeLayout_rl_zhuangxiuchengdu = (RelativeLayout) view
                .findViewById(R.id.rl_zhuangxiuchengdu);
        mTextView_tv_zhuangxiuchengdu_bitian_tip = (TextView) view
                .findViewById(R.id.tv_zhuangxiuchengdu_bitian_tip);
        mTextView_tv_zhuangxiuchengdu = (TextView) view
                .findViewById(R.id.tv_zhuangxiuchengdu);
        mRelativeLayout_rl_peitaosheshi = (RelativeLayout) view
                .findViewById(R.id.rl_peitaosheshi);
        mTextView_tv_peitaosheshi = (TextView) view
                .findViewById(R.id.tv_peitaosheshi);
        mRelativeLayout_rl_jiaofangriqi = (RelativeLayout) view
                .findViewById(R.id.rl_jiaofangriqi);
        mTextView_tv_jiaofangriqi_bitian_tip = (TextView) view
                .findViewById(R.id.tv_jiaofangriqi_bitian_tip);
        mTextView_tv_jiaofangriqi = (TextView) view
                .findViewById(R.id.tv_jiaofangriqi);
        mRelativeLayout_rl_fangyuantedian = (RelativeLayout) view
                .findViewById(R.id.rl_fangyuantedian);
        mTextView_tv_fangyuantedian = (TextView) view
                .findViewById(R.id.tv_fangyuantedian);
        mEditText_et_qita_content = (EditText) view
                .findViewById(R.id.et_qita_content);
        mButton_btn_lijifabu = (Button) view.findViewById(R.id.btn_lijifabu);
        mButton_btn_daifabu = (Button) view.findViewById(R.id.btn_daifabu);
        mGridView_fangyuantu = (GridView) view
                .findViewById(R.id.gridview_fangyuantu);

        mLinearLayout_ll_maidian = (LinearLayout) view
                .findViewById(R.id.ll_maidian);
        mTextView_tv_maidian_1 = (TextView) view
                .findViewById(R.id.tv_maidian_1);
        mTextView_tv_maidian_2 = (TextView) view
                .findViewById(R.id.tv_maidian_2);
        mTextView_tv_maidian_3 = (TextView) view
                .findViewById(R.id.tv_maidian_3);
        mTextView_tv_maidian_4 = (TextView) view
                .findViewById(R.id.tv_maidian_4);

        mEditText_et_zhifufangshi_ya_num
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        if (s.toString().equals("0")) {
                            mEditText_et_zhifufangshi_ya_num.setText("");
                        }
                    }
                });

        mEditText_et_zhifufangshi_fu_num
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        if (s.toString().equals("0")) {
                            mEditText_et_zhifufangshi_fu_num.setText("");
                        }
                    }
                });

        mRadioGroup_rg_action
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        clearMaiDian();
                        switch (checkedId) {
                            case R.id.rb_zhengzu: {
                                trade = "整租";
                                mLinearLayout_ll_zhengzu
                                        .setVisibility(View.VISIBLE);
                                mLinearLayout_ll_fangshi_fenzu
                                        .setVisibility(View.GONE);
                                mLinearLayout_ll_chushou.setVisibility(View.GONE);
                                mLinearLayout_ll_zhushou.setVisibility(View.GONE);
                            }
                            break;

                            case R.id.rb_fenzu: {
                                trade = "分租";
                                mLinearLayout_ll_zhengzu
                                        .setVisibility(View.VISIBLE);
                                mLinearLayout_ll_fangshi_fenzu
                                        .setVisibility(View.VISIBLE);
                                mLinearLayout_ll_chushou.setVisibility(View.GONE);
                                mLinearLayout_ll_zhushou.setVisibility(View.GONE);
                            }
                            break;

                            case R.id.rb_chushou: {
                                trade = "出售";
                                mLinearLayout_ll_zhengzu.setVisibility(View.GONE);
                                mLinearLayout_ll_fangshi_fenzu
                                        .setVisibility(View.GONE);
                                mLinearLayout_ll_chushou
                                        .setVisibility(View.VISIBLE);
                                mLinearLayout_ll_zhushou.setVisibility(View.GONE);
                            }
                            break;

                            case R.id.rb_zushou: {
                                trade = "租售";
                                mLinearLayout_ll_zhengzu.setVisibility(View.GONE);
                                mLinearLayout_ll_fangshi_fenzu
                                        .setVisibility(View.GONE);
                                mLinearLayout_ll_chushou.setVisibility(View.GONE);
                                mLinearLayout_ll_zhushou
                                        .setVisibility(View.VISIBLE);
                            }
                            break;

                            default:
                                break;
                        }
                    }
                });

        mRelativeLayout_rl_loupanmingcheng.setOnClickListener(this);
        mButton_btn_lijifabu.setOnClickListener(this);
        mButton_btn_daifabu.setOnClickListener(this);
        mRelativeLayout_rl_chaoxiang.setOnClickListener(this);
        mRelativeLayout_rl_chuzujian.setOnClickListener(this);
        mRelativeLayout_rl_fenzutiaojian.setOnClickListener(this);
        mRelativeLayout_rl_peitaosheshi.setOnClickListener(this);
        mRelativeLayout_rl_kanfangfangshi.setOnClickListener(this);
        mRelativeLayout_rl_zhuangxiuchengdu.setOnClickListener(this);
        mRelativeLayout_rl_fangyuantedian.setOnClickListener(this);
        mRelativeLayout_rl_shuifeishuoming.setOnClickListener(this);
        mRelativeLayout_rl_jiaofangriqi.setOnClickListener(this);
        setETWatcher();
        scrollView = (ScrollView) view
                .findViewById(R.id.add_zhuzhai_scrollview);
    }

    private void clearEditView() {
        mEditText_et_zujin.setText("");
        mEditText_et_zhifufangshi_ya_num.setText("");
        mEditText_et_zhifufangshi_fu_num.setText("");
        mEditText_et_chushou_shoujia.setText("");
        mEditText_et_zhushou_zhujin.setText("");
        mEditText_et_zhushou_shoujia.setText("");
        mEditText_et_mianji.setText("");
        mEditText_et_louceng_di_num.setText("");
        mEditText_et_louceng_total_num.setText("");
        mEditText_et_fanghao.setText("");
        mEditText_et_huxing_shi_num.setText("");
        mEditText_et_huxing_ting_num.setText("");
        mEditText_et_huxing_wei_num.setText("");
        mEditText_et_huxing_yangtai_num.setText("");
        mEditText_et_qita_content.setText("");
        mTextView_tv_kanfangfangshi.setText("请选择");
        mTextView_tv_kanfangfangshi.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_chaoxiang.setText("请选择");
        mTextView_tv_chaoxiang.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_zhuangxiuchengdu.setText("请选择");
        mTextView_tv_zhuangxiuchengdu.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_peitaosheshi.setText("请选择");
        mTextView_tv_peitaosheshi.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_jiaofangriqi.setText("请选择");
        mTextView_tv_jiaofangriqi.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_fangyuantedian.setText("点击添加");
        mTextView_tv_fangyuantedian.setVisibility(View.VISIBLE);
        mTextView_tv_fangyuantedian.setTextColor(getResources().getColor(
                android.R.color.black));
        clearMaiDian();

        mEdtext_tv_loupanmingcheng.setText("请输入楼盘名称");
        mEdtext_tv_loupanmingcheng.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_chuzujian.setText("请选择");
        mTextView_tv_chuzujian.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_fenzutiaojian.setText("请选择");
        mTextView_tv_fenzutiaojian.setTextColor(getResources().getColor(
                android.R.color.black));
        mTextView_tv_shuifeishuoming.setText("请选择");
        mTextView_tv_shuifeishuoming.setTextColor(getResources().getColor(
                android.R.color.black));

        mTextView_tv_shuifeishuoming_bitian_tip.setVisibility(View.VISIBLE);
        mTextView_tv_loupanmingcheng_bitian_tip.setVisibility(View.VISIBLE);
        mTextView_tv_kanfangfangshi_bitian_tip.setVisibility(View.VISIBLE);
        mTextView_tv_chaoxiang_bitian_tip.setVisibility(View.VISIBLE);
        mTextView_tv_zhuangxiuchengdu_bitian_tip.setVisibility(View.VISIBLE);
        mTextView_tv_jiaofangriqi_bitian_tip.setVisibility(View.VISIBLE);
    }

    private void clearMaiDian() {
        mTextView_tv_maidian_1.setVisibility(View.INVISIBLE);
        mTextView_tv_maidian_1.setVisibility(View.INVISIBLE);
        mTextView_tv_maidian_2.setVisibility(View.INVISIBLE);
        mTextView_tv_maidian_3.setVisibility(View.INVISIBLE);
        mTextView_tv_maidian_4.setVisibility(View.INVISIBLE);
        mFangYuanTuList.clear();
        mFangYuanTuList.add("add");
        mFangYuanAdapter.notifyDataSetChanged();
        mTextView_tv_fangyuantedian.setText("点击添加");
        mTextView_tv_fangyuantedian.setVisibility(View.VISIBLE);
    }

    private void setETWatcher() {
        mEditText_et_zujin.addTextChangedListener(new MyTextWatcher(
                mTextView_tv_zujin_bitian_tip));
        mEditText_et_chushou_shoujia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_chushou_bitian_tip.setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_chushou_bitian_tip.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtext_tv_loupanmingcheng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_chushou_bitian_tip.setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_chushou_bitian_tip.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText_et_zhushou_shoujia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_zhushou_shoujia_bitian_tip
                            .setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_zhushou_shoujia_bitian_tip
                            .setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText_et_zhushou_zhujin.addTextChangedListener(new MyTextWatcher(
                mTextView_tv_zhushou_bitian_tip));
        mEditText_et_mianji.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_mianji_bitian_tip.setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_mianji_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText_et_fanghao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_fanghao_bitian_tip.setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_fanghao_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText_et_zhifufangshi_ya_num
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (TextUtils.isEmpty(s)) {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else if (TextUtils
                                .isEmpty(mEditText_et_zhifufangshi_fu_num
                                        .getText())) {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
        mEditText_et_zhifufangshi_fu_num
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (TextUtils.isEmpty(s)) {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else if (TextUtils
                                .isEmpty(mEditText_et_zhifufangshi_ya_num
                                        .getText())) {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else {
                            mTextView_tv_zhifufangshi_bitian_tip
                                    .setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
        mEditText_et_louceng_di_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mTextView_tv_louceng_bitian_tip.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(mEditText_et_louceng_total_num
                        .getText())) {
                    mTextView_tv_louceng_bitian_tip.setVisibility(View.VISIBLE);
                } else {
                    mTextView_tv_louceng_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText_et_louceng_total_num
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (TextUtils.isEmpty(s)) {
                            mTextView_tv_louceng_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else if (TextUtils
                                .isEmpty(mEditText_et_louceng_di_num.getText())) {
                            mTextView_tv_louceng_bitian_tip
                                    .setVisibility(View.VISIBLE);
                        } else {
                            mTextView_tv_louceng_bitian_tip
                                    .setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
        mEditText_et_huxing_shi_num
                .addTextChangedListener(new myHuxinWatcher1());
        mEditText_et_huxing_ting_num
                .addTextChangedListener(new myHuxinWatcher2());
        mEditText_et_huxing_wei_num
                .addTextChangedListener(new myHuxinWatcher3());
        mEditText_et_huxing_yangtai_num
                .addTextChangedListener(new myHuxinWatcher4());
    }

    private boolean watcher1HasContent = false;
    private boolean watcher2HasContent = false;
    private boolean watcher3HasContent = false;
    private boolean watcher4HasContent = false;

    class myHuxinWatcher1 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (TextUtils.isEmpty(s)) {
                mTextView_tv_huxing_bitian_tip.setVisibility(View.VISIBLE);
                watcher1HasContent = false;
            } else {
                watcher1HasContent = true;
                if (watcher1HasContent && watcher2HasContent
                        && watcher3HasContent && watcher4HasContent) {
                    mTextView_tv_huxing_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class myHuxinWatcher2 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (TextUtils.isEmpty(s)) {
                mTextView_tv_huxing_bitian_tip.setVisibility(View.VISIBLE);
                watcher2HasContent = false;
            } else {
                watcher2HasContent = true;
                if (watcher1HasContent && watcher2HasContent
                        && watcher3HasContent && watcher4HasContent) {
                    mTextView_tv_huxing_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class myHuxinWatcher3 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (TextUtils.isEmpty(s)) {
                mTextView_tv_huxing_bitian_tip.setVisibility(View.VISIBLE);
                watcher3HasContent = false;
            } else {
                watcher3HasContent = true;
                if (watcher1HasContent && watcher2HasContent
                        && watcher3HasContent && watcher4HasContent) {
                    mTextView_tv_huxing_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class myHuxinWatcher4 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (TextUtils.isEmpty(s)) {
                mTextView_tv_huxing_bitian_tip.setVisibility(View.VISIBLE);
                watcher4HasContent = false;
            } else {
                watcher4HasContent = true;
                if (watcher1HasContent && watcher2HasContent
                        && watcher3HasContent && watcher4HasContent) {
                    mTextView_tv_huxing_bitian_tip
                            .setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void getFangyuanFeatures() {
        JSONObject obj = new JSONObject();
        try {
            String property = trade;
            if (trade.equals("整租") || trade.equals("分租")) {
                property = "出租";
            }
            obj.put("property", property);

            ClientHelper clientHelper = new ClientHelper(getActivity(),
                    ServerUrl.METHOD_getFeatureByProperty, obj.toString(),
                    new Handler() {
                        @Override
                        public void dispatchMessage(Message msg) {
                            super.dispatchMessage(msg);
                            if (msg != null) {
                                switch (msg.what) {
                                    case Constants.TAG_SUCCESS:
                                        Bundle bundle = msg.getData();
                                        String request_url = bundle
                                                .getString("request_url");
                                        String resp = bundle.getString("resp");
                                        try {
                                            JSONObject jsonObject = new JSONObject(
                                                    resp);
                                            String result = jsonObject.optString(
                                                    "result", "");
                                            if (result
                                                    .equals(Constants.RESULT_SUCCESS)) {
                                                JSONArray array_features = jsonObject
                                                        .optJSONArray("data");
                                                List<MaidianNodeInfo> infors = new ArrayList<MaidianNodeInfo>();
                                                if (array_features != null) {
                                                    for (int i = 0; i < array_features
                                                            .length(); i++) {
                                                        JSONObject object = array_features
                                                                .getJSONObject(i);
                                                        if (object != null) {
                                                            String featureid = object
                                                                    .optString("featureid");
                                                            String featurdetail = object
                                                                    .optString("detail");
                                                            MaidianNodeInfo info = new MaidianNodeInfo();
                                                            info.setDetail(featurdetail);
                                                            info.setFeatureid(featureid);
                                                            info.setSelected(false);
                                                            infors.add(info);
                                                        }
                                                    }
                                                    maidianGridViewAdapter
                                                            .updateBgColors(infors
                                                                    .size());
                                                    mMaidianNodeInfos.clear();
                                                    mMaidianNodeInfos
                                                            .addAll(infors);
                                                    maidianGridViewAdapter
                                                            .notifyDataSetChanged();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }

                                        break;
                                    case Constants.TAG_FAIL:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    });
            clientHelper.setShowProgressMessage("正在获取房源特点");
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean checkIsNoValue(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }

    private void refreshDate(String str) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("currentPage", currentPage);
            obj.put("pageSize", pageSize);
            obj.put("val", GlobalSPA.getInstance(getActivity())
                    .getStringValueForKey(GlobalSPA.KEY_CITYID));
            obj.put("estatename", str);

            ClientHelper clientHelper = new ClientHelper(getActivity(),
                    ServerUrl.METHOD_queryCityListPage, obj.toString(),
                    mHandler);
            clientHelper.setShowProgressMessage("正在获取...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String filterStr = "";

    /**
     * 楼盘
     */
    private void showLouPanMingChengDlg() {
        UIHelper.startActivity(getActivity(), HouseDengJiActivity.class);

        // final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        // LayoutInflater inflater = LayoutInflater.from(getActivity());
        // View viewContent =
        // inflater.inflate(R.layout.dlg_house_add_loupanmingcheng, null);
        //
        // mTv_no_house_filter_item =
        // (TextView)viewContent.findViewById(R.id.tv_no_house_filter_item);
        //
        // et_quxian = (EditText)viewContent.findViewById(R.id.et_quxian);
        // et_pianqu = (EditText)viewContent.findViewById(R.id.et_pianqu);
        //
        // et_search = (EditText)viewContent.findViewById(R.id.et_search);
        // et_search.addTextChangedListener(new TextWatcher() {
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int
        // count) {
        // // TODO Auto-generated method stub
        // MyLog.d("TAG", s + ", " + start + ", " + before + ", " + count);
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // // TODO Auto-generated method stub
        // if (isClickSet) {
        // isClickSet = false;
        // return;
        // }
        //
        // if(s.toString().trim().length() <= filterStr.length()) {
        // et_quxian.setText("");
        // et_pianqu.setText("");
        // }
        //
        // if (s.toString().trim().length() > filterStr.length() ||
        // s.toString().trim().equals("")) {
        // filterStr = s.toString().trim();
        //
        // currentPage = 1;
        // mHouseNameNodeInfos.clear();
        // refreshDate(et_search.getText().toString().trim());
        // }
        //
        // }
        // });
        //
        // mListView =
        // (PullToRefreshListView)viewContent.findViewById(R.id.listview);
        // mListView.setMode(Mode.PULL_FROM_END);
        // mListView.getLoadingLayoutProxy(false,
        // true).setRefreshingLabel("正在加载");
        // mListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
        // mListView.getLoadingLayoutProxy(false,
        // true).setReleaseLabel("释放开始加载");
        //
        // mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
        //
        // @Override
        // public void onPullDownToRefresh(
        // PullToRefreshBase<ListView> refreshView) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void onPullUpToRefresh(
        // PullToRefreshBase<ListView> refreshView) {
        // // TODO Auto-generated method stub
        // if (currentPage < totalPages) {
        // currentPage ++;
        // refreshDate(et_search.getText().toString().trim());
        // }
        // else {
        // mListView.getLoadingLayoutProxy(false,
        // true).setRefreshingLabel("没有更多数据");
        // mListView.getLoadingLayoutProxy(false, true).setPullLabel("没有更多数据");
        // mListView.getLoadingLayoutProxy(false,
        // true).setReleaseLabel("没有更多数据");
        //
        // mListView.postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // mListView.onRefreshComplete();
        // }
        // }, 500);
        // }
        // }
        //
        // });
        //
        // mListView.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // // TODO Auto-generated method stub
        // isClickSet = true;
        // if (mHouseNameNodeInfos.get(position-1).isSelected()) {
        // for (HouseNameNodeInfo node:mHouseNameNodeInfos) {
        // node.setSelected(false);
        // }
        // estateid = null;
        // estatename = null;
        //
        // et_quxian.setText("");
        // et_pianqu.setText("");
        // et_search.setText("");
        // }
        // else {
        // for (HouseNameNodeInfo node:mHouseNameNodeInfos) {
        // node.setSelected(false);
        // }
        // mHouseNameNodeInfos.get(position-1).setSelected(true);
        // estateid = mHouseNameNodeInfos.get(position-1).getEstateid();
        // estatename = mHouseNameNodeInfos.get(position-1).getEstatename();
        // estatequxian = mHouseNameNodeInfos.get(position-1).getDistrictname();
        // estatepianqu = mHouseNameNodeInfos.get(position-1).getAreaname();
        //
        // et_quxian.setText(mHouseNameNodeInfos.get(position-1).getDistrictname());
        // et_pianqu.setText(mHouseNameNodeInfos.get(position-1).getAreaname());
        // et_search.setText(mHouseNameNodeInfos.get(position-1).getEstatename());
        // }
        //
        // if (mHouseNameListAdapter != null) {
        // mHouseNameListAdapter.notifyDataSetChanged();
        // }
        // }
        // });
        //
        // mHouseNameNodeInfos.clear();
        //
        // mHouseNameListAdapter = new HouseNameListAdapter(getActivity(),
        // mHouseNameNodeInfos);
        // mListView.setAdapter(mHouseNameListAdapter);
        //
        // final Button btn_confirm =
        // (Button)viewContent.findViewById(R.id.btn_confirm);
        // btn_confirm.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // dlg.dismiss();
        // if (estatename == null) {
        // mEdtext_tv_loupanmingcheng.setText("请输入楼盘名称");
        // mEdtext_tv_loupanmingcheng.setTextColor(getResources().getColor(R.color.add_fangyuan_text_color));
        // mTextView_tv_loupanmingcheng_bitian_tip.setVisibility(View.VISIBLE);
        // }
        // else {
        // mEdtext_tv_loupanmingcheng.setText(estatename + " (" + estatequxian
        // + ", " + estatepianqu + ")");
        // mTextView_tv_loupanmingcheng_bitian_tip.setVisibility(View.INVISIBLE);
        // mEdtext_tv_loupanmingcheng.setTextColor(TEXT_SELECTED_COLOR);
        // }
        // }
        // });
        //
        // dlg.setContentView(viewContent);
        // dlg.setCanceledOnTouchOutside(true);
        //
        // DisplayMetrics dm = new DisplayMetrics();
        // getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //
        // Window window = dlg.getWindow();
        // window.setGravity(Gravity.CENTER);
        // WindowManager.LayoutParams lp =window.getAttributes();
        // lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        // lp.height = dm.heightPixels - DensityUtil.dip2px(getActivity(), 100);
        // window.setAttributes(lp);
        //
        // dlg.show();
        //
        // mHandler.postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        //
        // refreshDate("");
        // }
        // }, 200);
    }

    private void showChaoXiangDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                getActivity(), chaoxiang);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_chaoxiang.setText(chaoxiang[position]);
                mTextView_tv_chaoxiang_bitian_tip.setVisibility(View.INVISIBLE);
                mTextView_tv_chaoxiang.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void showChuZuJianDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                getActivity(), chuzujian);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_chuzujian.setText(chuzujian[position]);
                mTextView_tv_chuzujian.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void showFenZuTiaoJianDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                getActivity(), fenzutiaojian);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_fenzutiaojian.setText(fenzutiaojian[position]);
                mTextView_tv_fenzutiaojian.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void showZhuangXiuChengDuDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(
                getActivity(), zhuangxiuchengdu);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_zhuangxiuchengdu
                        .setText(zhuangxiuchengdu[position]);
                mTextView_tv_zhuangxiuchengdu_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_zhuangxiuchengdu.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void showPeiTaoSheShiDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_gridview, null);

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.gridview);

        final CustomSelectableGridViewAdapter adapter = new CustomSelectableGridViewAdapter(
                getActivity(), peitaosheshi, peitaosheshi_selected);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                peitaosheshi_selected[position] = !peitaosheshi_selected[position];
                adapter.notifyDataSetChanged();
            }
        });

        final Button btn_confirm = (Button) viewContent
                .findViewById(R.id.btn_confirm);
        btn_confirm.setVisibility(View.VISIBLE);
        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < peitaosheshi_selected.length; i++) {
                    if (peitaosheshi_selected[i]) {
                        stringBuilder.append(peitaosheshi[i]).append(" ");
                    }
                }

                if (stringBuilder.length() == 0) {
                    mTextView_tv_peitaosheshi.setText("请选择");
                    mTextView_tv_peitaosheshi.setTextColor(getResources()
                            .getColor(R.color.add_fangyuan_text_color));
                } else {
                    mTextView_tv_peitaosheshi.setText(stringBuilder.toString());
                    mTextView_tv_peitaosheshi.setTextColor(TEXT_SELECTED_COLOR);
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    private void showKanFangFangShiDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(
                R.layout.dlg_house_add_kanfangfangshi, null);

        final Button btn_liushi = (Button) viewContent
                .findViewById(R.id.btn_liushi);
        btn_liushi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_kanfangfangshi.setText("留匙");
                mTextView_tv_kanfangfangshi_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_kanfangfangshi.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        final Button btn_lianxiyezhu = (Button) viewContent
                .findViewById(R.id.btn_lianxiyezhu);
        btn_lianxiyezhu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_kanfangfangshi.setText("联系业主");
                mTextView_tv_kanfangfangshi_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_kanfangfangshi.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        final Button btn_qita = (Button) viewContent
                .findViewById(R.id.btn_qita);
        btn_qita.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_kanfangfangshi.setText("其他");
                mTextView_tv_kanfangfangshi_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_kanfangfangshi.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
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

    private MaidianGridViewAdapter maidianGridViewAdapter;

    private void showMaidianDlg() {

        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.dlg_tab_house_maidian,
                null);


        final Button btn_confirm = (Button) viewContent
                .findViewById(R.id.btn_confirm);


        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
                    mTextView_tv_fangyuantedian.setText("点击添加");
                    mLinearLayout_ll_maidian.setVisibility(View.GONE);
                    mTextView_tv_fangyuantedian.setVisibility(View.VISIBLE);
                } else {
                    mLinearLayout_ll_maidian.setVisibility(View.VISIBLE);
                    mTextView_tv_fangyuantedian.setVisibility(View.GONE);
                    int count = 0;
                    for (MaidianNodeInfo node : mMaidianNodeInfos) {
                        if (node.isSelected()) {
                            count++;
                            if (count == 1) {
                                mTextView_tv_maidian_1
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_1.setText(node.getDetail());
                                mTextView_tv_maidian_2
                                        .setVisibility(View.INVISIBLE);
                                mTextView_tv_maidian_3
                                        .setVisibility(View.INVISIBLE);
                                mTextView_tv_maidian_4
                                        .setVisibility(View.INVISIBLE);
                            } else if (count == 2) {
                                mTextView_tv_maidian_1
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_2
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_2.setText(node.getDetail());
                                mTextView_tv_maidian_3
                                        .setVisibility(View.INVISIBLE);
                                mTextView_tv_maidian_4
                                        .setVisibility(View.INVISIBLE);
                            } else if (count == 3) {
                                mTextView_tv_maidian_1
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_2
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_3
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_3.setText(node.getDetail());
                                mTextView_tv_maidian_4
                                        .setVisibility(View.INVISIBLE);
                            } else if (count == 4) {
                                mTextView_tv_maidian_1
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_2
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_3
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_4
                                        .setVisibility(View.VISIBLE);
                                mTextView_tv_maidian_4.setText(node.getDetail());
                            }
                        }
                    }
                }

            }
        });

        final GridView gridview = (GridView) viewContent
                .findViewById(R.id.grid_view_maidian);

        maidianGridViewAdapter = new MaidianGridViewAdapter(getActivity(),
                mMaidianNodeInfos);
        gridview.setAdapter(maidianGridViewAdapter);
        getFangyuanFeatures();
        gridview.setOnItemClickListener(new OnItemClickListener() {

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

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.height = dm.heightPixels - DensityUtil.dip2px(getActivity(), 100);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        dlg.show();
    }

    private void showShuiFeiShuoMingDlg() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(
                R.layout.dlg_house_add_shuifeishuoming, null);

        final Button btn_gefu = (Button) viewContent
                .findViewById(R.id.btn_gefu);
        btn_gefu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_shuifeishuoming.setText("各付");
                mTextView_tv_shuifeishuoming_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_shuifeishuoming.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        final Button btn_shide = (Button) viewContent
                .findViewById(R.id.btn_shide);
        btn_shide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_shuifeishuoming.setText("实得");
                mTextView_tv_shuifeishuoming_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_shuifeishuoming.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        final Button btn_baoshui = (Button) viewContent
                .findViewById(R.id.btn_baoshui);
        btn_baoshui.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_shuifeishuoming.setText("包税");
                mTextView_tv_shuifeishuoming_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_shuifeishuoming.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        final Button btn_buxiang = (Button) viewContent
                .findViewById(R.id.btn_buxiang);
        btn_buxiang.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
                mTextView_tv_shuifeishuoming.setText("不详");
                mTextView_tv_shuifeishuoming_bitian_tip
                        .setVisibility(View.INVISIBLE);
                mTextView_tv_shuifeishuoming.setTextColor(TEXT_SELECTED_COLOR);
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        dlg.show();
    }


    private void setDaiFaBu(String status) {

        if (house == null) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入楼盘名称!",
                    Toast.LENGTH_SHORT).show();
            mTextView_tv_loupanmingcheng_bitian_tip.setVisibility(View.VISIBLE);
            return;
        }



        String rentprice = null;
        if (trade.equals("整租") || trade.equals("分租")) {
            rentprice = mEditText_et_zujin.getText().toString().trim();
            if (checkIsNoValue(rentprice)) {
                Toast.makeText(getActivity().getApplicationContext(), "请输入租金!",
                        Toast.LENGTH_SHORT).show();
                mTextView_tv_zujin_bitian_tip.setVisibility(View.VISIBLE);
                return;
            }
        } else if (trade.equals("租售")) {
            rentprice = mEditText_et_zhushou_zhujin.getText().toString().trim();
            if (checkIsNoValue(rentprice)) {
                mTextView_tv_zujin_bitian_tip.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "请输入租金!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String price = null;
        String shoujiaHint = "";
        if (trade.equals("出售")) {
            price = mEditText_et_chushou_shoujia.getText().toString().trim();
            shoujiaHint = "请输入出售售价";
            if (checkIsNoValue(price)) {
             mTextView_tv_chushou_bitian_tip.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(),
                        shoujiaHint, Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (trade.equals("租售")) {
            price = mEditText_et_zhushou_shoujia.getText().toString().trim();
            shoujiaHint = "请输入租售售价";
            if (checkIsNoValue(price)) {
                mTextView_tv_chushou_bitian_tip.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity().getApplicationContext(),
                        shoujiaHint, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String ekerentpaymodedeposit = mEditText_et_zhifufangshi_ya_num
                .getText().toString().trim();
        String ekerentpaymodecash = mEditText_et_zhifufangshi_fu_num.getText()
                .toString().trim();
        if (trade.equals("整租") || trade.equals("分租")) {
            if (checkIsNoValue(ekerentpaymodedeposit)) {
                mTextView_tv_zhifufangshi_bitian_tip.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(),
                        "请设置押的数值!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkIsNoValue(ekerentpaymodecash)) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "请设置付的数值!", Toast.LENGTH_SHORT).show();
                mTextView_tv_zhifufangshi_bitian_tip.setVisibility(View.VISIBLE);

                return;
            }
        }
        String square = mEditText_et_mianji.getText().toString().trim();
        if (checkIsNoValue(square)) {
            mTextView_tv_mianji_bitian_tip.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "请输入面积!",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        String floor = mEditText_et_louceng_di_num.getText().toString().trim();
        if (checkIsNoValue(floor)) {
            mTextView_tv_louceng_bitian_tip.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "请输入所在层数!",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        if (Integer.parseInt(floor) <= 0) {
            Toast.makeText(getActivity().getApplicationContext(), "楼层数应大于0!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String floorall = mEditText_et_louceng_total_num.getText().toString()
                .trim();
        if (checkIsNoValue(floorall)) {

            Toast.makeText(getActivity().getApplicationContext(), "请输入总共层数!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String countf = mEditText_et_huxing_shi_num.getText().toString().trim();
        if (checkIsNoValue(countf)) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入共有几室!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String roomno = mEditText_et_fanghao.getText().toString().trim();
        if (checkIsNoValue(roomno)) {
            mTextView_tv_fanghao_bitian_tip.setVisibility(View.VISIBLE);

            Toast.makeText(getActivity().getApplicationContext(), "请输入房号!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String countt = mEditText_et_huxing_ting_num.getText().toString()
                .trim();
        if (checkIsNoValue(countt)) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入共有几厅!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String countw = mEditText_et_huxing_wei_num.getText().toString().trim();
        if (checkIsNoValue(countw)) {
            mTextView_tv_huxing_bitian_tip.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "请输入共有几卫!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String county = mEditText_et_huxing_yangtai_num.getText().toString()
                .trim();
        if (checkIsNoValue(county)) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入共有几个阳台!",
                    Toast.LENGTH_SHORT).show();
            return;
        }



        String propertylook = mTextView_tv_kanfangfangshi.getText().toString()
                .trim();
        if (propertylook.equals("请选择")) {
            mTextView_tv_kanfangfangshi_bitian_tip.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "请选择看房方式!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String propertydirection = mTextView_tv_chaoxiang.getText().toString()
                .trim();
        if (trade.equals("出售") || trade.equals("租售")) {
            if (propertydirection.equals("请选择")) {
                mTextView_tv_chaoxiang_bitian_tip.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "请选择朝向!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String zhuangxiuchengdu = mTextView_tv_zhuangxiuchengdu.getText()
                .toString().trim();
        if (zhuangxiuchengdu.equals("请选择")) {
            mTextView_tv_zhuangxiuchengdu_bitian_tip.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "请选择装修方式!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String propertyJiaofangDate = mTextView_tv_jiaofangriqi.getText()
                .toString().trim();
//        if (propertyJiaofangDate.equals("请选择")) {
//            Toast.makeText(getActivity().getApplicationContext(), "请选择交房日期!",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }



        String propertytax = "";
        if (trade.equals("出售")) {
            propertytax = mTextView_tv_shuifeishuoming.getText().toString()
                    .trim();
            if (propertytax.equals("请选择")) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "请设置税费说明!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String ekefeature = mTextView_tv_fangyuantedian.getText().toString()
                .trim();
        if (ekefeature.equals("点击添加")) {
            ekefeature = "";
        } else {
            StringBuilder sbBuilder = new StringBuilder();
            for (MaidianNodeInfo node : mMaidianNodeInfos) {
                if (node.isSelected()) {
                    sbBuilder.append(node.getFeatureid()).append("/");
                }
            }
            if (sbBuilder.toString().length() > 0) {
                sbBuilder.deleteCharAt(sbBuilder.length() - 1);
            }
            ekefeature = sbBuilder.toString();
        }
        String propertyfurniture = mTextView_tv_peitaosheshi.getText()
                .toString().trim();
        if (propertyfurniture.equals("请选择")) {
            propertyfurniture = "";
        } else {
            propertyfurniture = propertyfurniture.replace(' ', '/');
        }

//        "opestatelon":"",
//                "opestatelat":"",
//                "opestatename" :"ss",
//                "opestatedistrict":"布吉",
        JSONObject obj = new JSONObject();
        try {
            obj.put("trade", trade);
            obj.put("floor", floor);
            obj.put("floorall", floorall);

            if (trade.contains("租")) {
                obj.put("rentprice", Integer.valueOf(rentprice));
            } else {
                obj.put("rentprice", 0);
            }


            if (trade.contains("售")) {
                obj.put("price", Integer.valueOf(price));
                obj.put("unitname", "万");
            } else {
                obj.put("price", 0);
                obj.put("rentunitname", "元/月");

            }

            if (!StringCheckHelper.isEmpty(house.estateid)) {
                obj.put("estateid", house.estateid);
            } else {
                obj.put("estateid", "00000000000000000000000000000000");
                obj.put("opestatelon", house.opestatelon);
                obj.put("opestatelat", house.opestatelat);
                obj.put("opestatedistrict", house.opestatedistrict);
                obj.put("opestatename", house.opestatename);

            }
            obj.put("roomno", roomno);
            obj.put("square", square);
            obj.put("countf", countf);
            obj.put("countt", countt);
            obj.put("countw", countw);
            obj.put("county", county);
            obj.put("propertydirection", propertydirection);

            obj.put("propertyfurniture", propertyfurniture);
            obj.put("propertylook", propertylook);
            String handoverdate = propertyJiaofangDate.replace("随时入住",
                    "2000-01-01");
            handoverdate = handoverdate.replace("日期待定", "2050-01-01");
            obj.put("handoverdate", handoverdate);
            obj.put("ekefeature", ekefeature);
            obj.put("propertytax", propertytax);
            obj.put("status", status);
            obj.put("remark", mEditText_et_qita_content.getText().toString()
                    .trim());
            obj.put("ekerentpaymodedeposit", ekerentpaymodedeposit);
            obj.put("ekerentpaymodecash", ekerentpaymodecash);
            obj.put("propertydecoration", zhuangxiuchengdu);// 装修程度，被漏掉了

            JSONArray listjson1 = new JSONArray();
            // BASE64Encoder encode = new BASE64Encoder();
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
            String jsonString = obj.toString();
            jsonString = jsonString.replace("\\/", "/");
            String urlString;
            if (status.equals("待审核")) {
                urlString = ServerUrl.METHOD_insertPropertyByWT;
            } else {
                urlString = ServerUrl.METHOD_insertPropertyDj;

            }
            ClientHelper clientHelper = new ClientHelper(getActivity(),
                    urlString, jsonString, mHandler);
            clientHelper.setShowProgressMessage("正在提交数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.rl_loupanmingcheng: {
                showLouPanMingChengDlg();
            }
            break;

            case R.id.btn_lijifabu: {
                setDaiFaBu("待审核");

            }
            break;

            case R.id.btn_daifabu: {
                setDaiFaBu("暂存");
            }
            break;

            case R.id.rl_chaoxiang: {
                showChaoXiangDlg();
            }
            break;

            case R.id.rl_chuzujian: {
                showChuZuJianDlg();
            }
            break;

            case R.id.rl_fenzutiaojian: {
                showFenZuTiaoJianDlg();
            }
            break;

            case R.id.rl_peitaosheshi: {
                showPeiTaoSheShiDlg();
            }
            break;

            case R.id.rl_kanfangfangshi: {
                showKanFangFangShiDlg();
            }
            break;

            case R.id.rl_zhuangxiuchengdu: {
                showZhuangXiuChengDuDlg();
            }
            break;

            case R.id.rl_fangyuantedian: {
                showMaidianDlg();
            }
            break;

            case R.id.rl_shuifeishuoming: {
                showShuiFeiShuoMingDlg();
            }
            break;

            case R.id.rl_jiaofangriqi: {
                new CalendarUtil(getActivity(), mHandler, mStrFromDate, mStrToDate)
                        .showDateSelectDlg(0);
            }
            break;

            default:
                break;
        }
    }

    class MyTextWatcher implements TextWatcher {
        private TextView hintView;

        public MyTextWatcher(TextView hintView) {
            this.hintView = hintView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (TextUtils.isEmpty(s)) {
                hintView.setVisibility(View.VISIBLE);
            } else {
                hintView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public boolean onNotification(NotificationListener.Notification notification) {
        if (notification.key.equals(NotificationKey.selectLoupan)) {
            house = (House) notification.object;
            mEdtext_tv_loupanmingcheng.setText(house.houseName);
            return true;

        }
        return super.onNotification(notification);
    }
}
