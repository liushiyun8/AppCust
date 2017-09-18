package com.eke.cust.tabhouse;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Ekefeature;
import com.eke.cust.model.HouseCover;
import com.eke.cust.model.HouseSource;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.imagepage.ImagePagerActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.widget.PagerSlidingTabStrip;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 房屋租售
 */
public class RentalHouseActivity extends BaseActivity implements PagerSlidingTabStrip.OnTabClickListener {
    @InjectBundleExtra(key = "data")
    private HouseCover houseCover;
    @InjectView(id = R.id.pagertab)
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    @InjectView(id = R.id.listview)
    private ListView refreshListView;
    private String titles[] = {"租房", "二手房"};

    private CommonListAdapter<HouseSource> mListAdapter;
    private ArrayList<HouseSource> listrent = new ArrayList<HouseSource>();
    private ArrayList<HouseSource> listsell = new ArrayList<HouseSource>();

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
                        hideLoading();

                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url
                                        .equals(ServerUrl.METHOD_getListProperty)) {
                                    JSONArray jsonArray_listrent_data = JSONUtils.getJSONArray(jsonObject, "listrent", null);
                                    JSONArray jsonArray_listsell_data = JSONUtils.getJSONArray(jsonObject, "listsell", null);
                                    if (jsonArray_listrent_data != null) {
                                        listrent = JSONUtils.getObjectList(jsonArray_listrent_data, HouseSource.class);
                                    }
                                    if (jsonArray_listsell_data != null) {
                                        listsell = JSONUtils.getObjectList(jsonArray_listsell_data, HouseSource.class);
                                    }
                                    initListAdapter(listrent);
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
                        hideLoading();
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        hideLoading();
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_rental_house);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(!StringCheckHelper.isEmpty(houseCover.estatename) ? houseCover.estatename : houseCover.name);
        registerLeftImageView(R.drawable.arrow_back);
        pagerSlidingTabStrip.setTitle(titles);
        pagerSlidingTabStrip.setTabClickListener(this);
        pagerSlidingTabStrip.onTabSelected(0);
        getHouseList();
    }


    //根据交易类型获取楼盘
    private void getHouseList() {
        showLoading();
        JSONObject obj = new JSONObject();
        try {
            String estateid = !StringCheckHelper.isEmpty(houseCover.estateid) ? houseCover.estateid : houseCover.id;
            obj.put("estateid", estateid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getListProperty, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取房源数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onTabClick(int position) {
        pagerSlidingTabStrip.onTabSelected(position);
        if (position == 0) {
            initListAdapter(listrent);
        } else {
            initListAdapter(listsell);
        }


    }


    private void initListAdapter(ArrayList<HouseSource> houseSources) {

        mListAdapter = new CommonListAdapter<HouseSource>(this, R.layout.layout_house_list_item, houseSources) {
            @Override
            public void convert(ViewHolder viewHolder, final HouseSource houseSource, int position) {
                // 房屋详情
                TextView mTxtHoseDesc = viewHolder.findViewById(R.id.tv_house_desc);
                //图片
                ImageView mIvHoseImage = viewHolder.findViewById(R.id.iv_house);
                //房屋状态
                TextView tv_house_update_time = viewHolder.findViewById(R.id.tv_house_update_time);

                //价格
                TextView tv_price = viewHolder.findViewById(R.id.tv_price);
                //楼盘名
                TextView tv_housing_estate = viewHolder.findViewById(R.id.tv_housing_estate);
                TextView tv_number = viewHolder.findViewById(R.id.tv_number);
                TextView tv_floor = viewHolder.findViewById(R.id.tv_floor);
                LinearLayout mLayoutFeature = viewHolder.findViewById(R.id.ll_maidian);
                String Floorall = houseSource.floorall;
                if (!StringCheckHelper.isEmpty(Floorall)) {
                    tv_floor.setText(houseSource.floor + "/" + Floorall + "层   ");

                }
                if (houseSource.trade.equals("出租")) {
                    String rentprice = !StringCheckHelper.isEmpty(houseSource.rentprice) ? houseSource.rentprice : "";
                    tv_price.setText(rentprice + "元/月");
                } else {
                    String price = !StringCheckHelper.isEmpty(houseSource.price) ? houseSource.price : "";
                    tv_price.setText(price + "万");

                }
                tv_housing_estate.setText(String.format("[%s]", houseSource.estatename));
                tv_number.setText(houseSource.propertyno);
                String countf = houseSource.countf;
                if (StringCheckHelper.isEmpty(countf)) {
                    countf = "0";
                }
                String countt = houseSource.countt;
                if (StringCheckHelper.isEmpty(countt)) {
                    countt = "0";
                }
                String countw = houseSource.countw;
                if (StringCheckHelper.isEmpty(countw)) {
                    countw = "0";
                }
                String county = houseSource.county;
                if (StringCheckHelper.isEmpty(county)) {
                    county = "0";
                }
                String square = houseSource.square;
                if (StringCheckHelper.isEmpty(square)) {
                    square = "0";
                }
                mTxtHoseDesc.setText(countf + "房"
                        + countt + "厅" + countw + "卫"
                        + county + "阳" + " " + square + "平");

                if (houseSource.ekeheadpic != null && !houseSource.ekeheadpic.equals("")) {
                    mIvHoseImage.setImageBitmap(BitmapUtils.stringtoBitmap(houseSource
                            .ekeheadpic));
                } else {
                    mIvHoseImage.setImageResource(R.drawable.house_source);
                }
                mIvHoseImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        UIHelper.startActivity(context, ImagePagerActivity.class, houseSource);

                    }
                });


//                Date now = new Date();
//                if (houseSource.handoverdate == 0
//                        || houseSource.handoverdate <= now.getTime()) {
//                    tv_house_status.setText("随时入住");
//                } else {
//                    tv_house_status.setText(DateUtil.getDateToString3(houseSource
//                            .handoverdate) + "前入住");
//
//                }
                tv_house_update_time.setText(DateUtil.getUpdateDate(houseSource.followtime));

                if (houseSource.listEkefeature != null
                        && houseSource.listEkefeature.size() > 0) {
                    for (int i = 0; i < houseSource.listEkefeature.size(); i++) {
                        Ekefeature ekeFeature = houseSource.listEkefeature.get(i);

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

                        textView.setText(ekeFeature.detail);
                        LinearLayout.LayoutParams liaParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        liaParams.setMargins(5, 0, 5, 0);// 设置了左右上下边距，
                        mLayoutFeature.addView(textView, liaParams);
                    }
                }
            }
        };
        refreshListView.setAdapter(mListAdapter);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HouseSource house = (HouseSource) parent
                        .getItemAtPosition(position);
                UIHelper.startActivity(mContext,
                        HouseDetailActivity.class, house);

            }
        });
    }

}
