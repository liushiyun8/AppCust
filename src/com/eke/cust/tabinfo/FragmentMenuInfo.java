package com.eke.cust.tabinfo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.chat.ConversationActivity;
import com.eke.cust.chat.ui.MineConversationListFragment;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.Adv;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.UserLocation;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.HouseDetailActivity;
import com.eke.cust.tabinfo.adapter.RentSellAdapter;
import com.eke.cust.tabinfo.news_activity.NewsDetailActivity;
import com.eke.cust.tabinfo.news_activity.NewsListActivity;
import com.eke.cust.tabmine.EntrustActivity;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.widget.GridViewForScrollView;
import com.eke.cust.widget.ListViewForScrollView;
import com.eke.cust.widget.convenientbanner.CBViewHolderCreator;
import com.eke.cust.widget.convenientbanner.ConvenientBanner;
import com.eke.cust.widget.convenientbanner.Holder;

import org.apache.commons.lang3.StringUtils;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foundation.base.fragment.BaseFragment;
import foundation.helper.GlideImageLoader;
import foundation.notification.NotificationCenter;

public class FragmentMenuInfo extends BaseFragment implements OnClickListener {
    private Context mContext;
    @InjectView(id = R.id.scrollview)
    public ScrollView mScrollView;
    @InjectView(id = R.id.viewpager)
    public ConvenientBanner convenientBanner;
    @InjectView(id = R.id.layout_message, click = true)
    private RelativeLayout mLayoutMessage;
    @InjectView(id = R.id.list_housepush)
    private GridViewForScrollView mGridViewForHouseTrack;
    @InjectView(id = R.id.txt_house_push, click = true)
    private TextView mTv_house_push;
    @InjectView(id = R.id.list_news)
    private ListViewForScrollView mListViewForNews;
    @InjectView(id = R.id.tv_news, click = true)
    private TextView mTv_news;
    @InjectView(id = R.id.iv_banner, click = true)
    private ImageView mIvBanner;
    @InjectView(id = R.id.iv_banner1, click = true)
    private ImageView mIvBanner1;
    @InjectView(id = R.id.tv_mymsg, click = true)
    private TextView mTv_mymsg;
    // 房屋信息
    private ArrayList<HouseSource> mHouseTrackNodeInfosList = new ArrayList<HouseSource>();
    private RentSellAdapter mHouseTrackListAdapter;
    private ArrayList<TabNewsNodeInfo> mNewsNodeInfosList = new ArrayList<TabNewsNodeInfo>();
    private TabNewsListAdapter mNewsListAdapter;

    //region  接口回调
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
                                        .equals(ServerUrl.METHOD_getIndexNews)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");


                                    JSONArray jsonArray_listNews = obj_data
                                            .optJSONArray("listNews");
                                    if (jsonArray_listNews != null) {
                                        for (int i = 0; i < jsonArray_listNews
                                                .length(); i++) {
                                            JSONObject obj = jsonArray_listNews
                                                    .getJSONObject(i);

                                            TabNewsNodeInfo node = new TabNewsNodeInfo();

                                            node.setNewsid(obj.optString("newsid",
                                                    ""));
                                            node.setTitle(obj
                                                    .optString("title", ""));
                                            mNewsNodeInfosList.add(node);
                                        }
                                        initNews(mNewsNodeInfosList);
                                    }

                                    // 租房
                                    mHouseTrackNodeInfosList.clear();
                                    // 售房

                                    JSONArray jsonArray = obj_data
                                            .optJSONArray("listrent");
                                    JSONArray listsell = obj_data
                                            .optJSONArray("listsell");

                                    if (listsell != null) {
                                        for (int i = 0; i < listsell.length(); i++) {
                                            JSONObject obj = listsell
                                                    .getJSONObject(i);

                                            HouseSource house = JSONUtils
                                                    .getObject(obj,
                                                            HouseSource.class);
                                            house.type = 1;

                                            mHouseTrackNodeInfosList.add(house);

                                        }

                                    }
                                    if (jsonArray != null) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject obj = jsonArray
                                                    .getJSONObject(i);

                                            HouseSource house = JSONUtils
                                                    .getObject(obj,
                                                            HouseSource.class);
                                            house.type = 0;
                                            mHouseTrackNodeInfosList.add(house);

                                        }

                                    }
                                    // 交换位置
                                    mHouseTrackNodeInfosList = HouseHelper
                                            .buildList(mHouseTrackNodeInfosList);

                                    mHouseTrackListAdapter = new RentSellAdapter(
                                            mContext,
                                            R.layout.item_resent_sell_layout,
                                            mHouseTrackNodeInfosList);
                                    mGridViewForHouseTrack
                                            .setAdapter(mHouseTrackListAdapter);


                                    JSONArray listLink = obj_data
                                            .optJSONArray("listLink");
                                    ArrayList<Adv> list = JSONUtils.getObjectList(listLink, Adv.class);
                                    initAdv(list);

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

    //endregion

    protected View onCreateContentView() {
        View view = inflateContentView(R.layout.fragment_menu_info);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setTitle("待命名");
        mContext = getActivity();
        dataInit();

        // 点击焦点广告
        NotificationCenter.defaultCenter.addListener(NotificationKey.click_adv,
                this);
        NotificationCenter.defaultCenter.addListener(NotificationKey.long_click_adv,
                this);
        initViews();

    }

    private void initViews() {
        getChildFragmentManager().beginTransaction().replace(R.id.message_content, new MineConversationListFragment()).commit();
        mGridViewForHouseTrack.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        HouseSource house = (HouseSource) parent.getItemAtPosition(position);
                        UIHelper.startActivity(mContext, HouseDetailActivity.class, house);

                    }
                });


    }

    private void initNews(ArrayList<TabNewsNodeInfo> mNewsNodeInfosList) {
        if (mNewsNodeInfosList.size() < 3) {
            for (int i = mNewsNodeInfosList.size(); i < 3; i++) {
                mNewsNodeInfosList.add(null);
            }
        }
        mNewsListAdapter = new TabNewsListAdapter(mContext, mNewsNodeInfosList);
        mListViewForNews.setAdapter(mNewsListAdapter);
        mListViewForNews.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                UIHelper.startActivity(getActivity(), NewsListActivity.class);

            }
        });
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        NotificationCenter.defaultCenter.removeListener(
                NotificationKey.click_adv, this);
        NotificationCenter.defaultCenter.removeListener(
                NotificationKey.long_click_adv, this);
    }


    private void dataInit() {
        JSONObject obj = new JSONObject();
        UserLocation location = AppContext.getInstance().getUserLocation();
        try {
            String cityid = AppContext.getInstance().getAppPref().cityId();
            if (StringUtils.isEmpty(cityid)) {
                cityid = "深圳";
            }
//            cityid = "深圳";

            obj.put("cityid", cityid);
            if (location != null) {
                obj.put("lon", "" + location.mLongitude);
                obj.put("lat", "" + location.mLatitude);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ClientHelper clientHelper = new ClientHelper(mContext,
                ServerUrl.METHOD_getIndexNews, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
        // getAdv();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.iv_banner: {
                ToastUtils.show(mContext, "扩展位");
            }
            break;

            case R.id.iv_banner1: {
                UIHelper.startActivity(getActivity(), EntrustActivity.class);
            }
            break;

            case R.id.tv_mymsg: {
                if (AppContext.getInstance().isLogin()) {
                    UIHelper.startActivity(getActivity(), ConversationActivity.class);
                } else {
                    UIHelper.startToLogin(getActivity());
                }

            }
            break;

            case R.id.txt_house_push: {
//                Intent intent = new Intent(getActivity(), TrackActivity.class);
//                startActivity(intent);
            }
            break;

            case R.id.tv_news: {
                UIHelper.startActivity(mContext, NewsListActivity.class);

            }
            break;
            default:
                break;
        }
    }

    private void initAdv(final ArrayList<Adv> advs) {


        convenientBanner.setPages(

                new CBViewHolderCreator<LocalImageHolderView>() {

                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, advs)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.point_gray, R.drawable.point_white})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        convenientBanner.setOnItemClickListener(new com.eke.cust.widget.convenientbanner.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                UIHelper.startActivity(getActivity(), NewsDetailActivity.class, advs.get(position).linkurl);

            }
        });
        mScrollView.fullScroll(View.FOCUS_UP);


    }

    public class LocalImageHolderView implements Holder<Adv> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Adv data) {
            GlideImageLoader.getInstace().loadImg(getContext(), imageView, data.coverurl);
        }
    }


}
