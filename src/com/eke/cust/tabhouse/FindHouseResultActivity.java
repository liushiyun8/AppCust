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
import com.eke.cust.base.BaseListActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Ekefeature;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.SearchHouse;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.imagepage.ImagePagerActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class FindHouseResultActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    @InjectView(id = R.id.listview)
    private PullToRefreshListView refreshListView;
    @InjectBundleExtra(key = "data")
    private String title;
    @InjectBundleExtra(key = "data1")
    private SearchHouse searchHouse;
    private CommonListAdapter<HouseSource> mListAdapter;
    private ArrayList<HouseSource> mItems = new ArrayList<HouseSource>();

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        _RefreshState = RefreshState.LS_Refresh;
        kPage = 0;
        getList();

    }

    //上拉加载更多
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        _RefreshState = RefreshState.LS_LoadMore;
        kPage++;
        getList();
    }


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
                                        .equals(ServerUrl.METHOD_getListPropertyPage)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");
                                    int totalPages = JSONUtils.getInt(obj_data, "totalPages", 0);
                                    setRefreshLoadedState(totalPages);
                                    JSONArray jsonArray_listrent_data = JSONUtils.getJSONArray(obj_data, "data", null);
                                    if (jsonArray_listrent_data != null) {
                                        mItems = JSONUtils.getObjectList(jsonArray_listrent_data, HouseSource.class);
                                        if (_RefreshState != RefreshState.LS_LoadMore) {
                                            initListAdapter();
                                        } else {
                                            ArrayList<HouseSource> mItems = JSONUtils.getObjectList(jsonArray_listrent_data, HouseSource.class);
                                            mListAdapter.addList(mItems);
                                        }

                                    }
                                    onComplete();
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
        setTitle(title);
        registerLeftImageView(R.drawable.arrow_back);

        setRefreshLoadedState(1);
        refreshListView.setOnRefreshListener(this);
        _RefreshState = RefreshState.LS_INIT;
        getList();
    }

    private void getList() {
        JSONObject obj = new JSONObject();
        try {

            obj.put("currentPage", kPage);
            obj.put("pageSize", kPageSize);
            obj.put("trade", searchHouse.trade);

            if (searchHouse.trade.equals("出租")) {
                obj.put("minrentprice", searchHouse.minrentprice);
                obj.put("maxrentprice", searchHouse.maxrentprice);
            } else {
                obj.put("minprice", searchHouse.minprice);
                obj.put("maxprice", searchHouse.maxprice);
            }
            obj.put("minsquare", searchHouse.minsquare);
            obj.put("maxsquare", searchHouse.maxsquare);
            if(!searchHouse.countf.equals("全部")){
                obj.put("countf", searchHouse.countf);
            }else{
                obj.put("countf", "");
            }

            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getListPropertyPage, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取房源数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void initListAdapter() {

        mListAdapter = new CommonListAdapter<HouseSource>(this, R.layout.layout_house_list_item, mItems) {
            @Override
            public void convert(ViewHolder viewHolder, final HouseSource houseSource, int position) {
                // 房屋详情
                TextView mTxtHoseDesc = viewHolder.findViewById(R.id.tv_house_desc);
                //图片
                ImageView mIvHoseImage = viewHolder.findViewById(R.id.iv_house);
                //房屋状态
                TextView tv_house_status = viewHolder.findViewById(R.id.tv_house_update_time);

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
                    if (!StringCheckHelper.isEmpty(houseSource.rentunitname)) {
                        tv_price.setText("" + houseSource.rentprice
                                + houseSource.rentunitname);
                    } else {
                        tv_price.setText("" + houseSource.rentprice);
                    }
                } else {
                    if (!StringCheckHelper.isEmpty(houseSource.unitname)) {
                        tv_price.setText(houseSource.price
                                + houseSource.unitname);
                    } else {
                        tv_price.setText("" + houseSource.price);
                    }
                }
                String estatename = !StringCheckHelper.isEmpty(houseSource.estatename) ? houseSource.estatename : "待定";

                tv_housing_estate.setText(String.format("[%s]", estatename));
                tv_number.setText(houseSource.propertyno);

                String countf = !StringCheckHelper.isEmpty(houseSource.countf) ? houseSource.countf : "0";
                String countt = !StringCheckHelper.isEmpty(houseSource.countt) ? houseSource.countt : "0";
                String countw = !StringCheckHelper.isEmpty(houseSource.countw) ? houseSource.countw : "0";
                String county = !StringCheckHelper.isEmpty(houseSource.county) ? houseSource.county : "0";
                String square = !StringCheckHelper.isEmpty(houseSource.square) ? houseSource.square : "0";

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
                        UIHelper.startActivity(context, ImagePagerActivity.class,houseSource);

                    }
                });


                Date now = new Date();
                if (houseSource.handoverdate == 0
                        || houseSource.handoverdate <= now.getTime()) {
                    tv_house_status.setText("随时入住");
                } else {
                    tv_house_status.setText(DateUtil.getDateToString3(houseSource
                            .handoverdate) + "前入住");
                }

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

}
