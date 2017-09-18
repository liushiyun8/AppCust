package com.eke.cust.tabhouse.sipandengji_activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.nodeinfo.DistrictNodeInfo;
import com.eke.cust.global.nodeinfo.PageNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabmine.profile_activity.DaiLiLouPanSelectEstateListAdapter;
import com.eke.cust.tabmine.profile_activity.EstateNodeInfo;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.utils.TransformUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import foundation.base.fragment.BaseFragment;

public class NameSearchFragment extends BaseFragment {
    private ImageView mSearchBtn;
    private EditText mEditText_et_content_find;

    private PullToRefreshListView mListView_listview_loupan;
    private PageNodeInfo mEstatePageNodeInfo = new PageNodeInfo();
    private TextView mTextView_quxian, mTextView_pianqu, mTextView_loupan;
    private String mCityName = "深圳";
    private List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
    private List<EstateNodeInfo> mAllEstates = new ArrayList<EstateNodeInfo>();
    private List<EstateNodeInfo> mDailiEstates = new ArrayList<EstateNodeInfo>();
    private List<EstateNodeInfo> mSelectedEstates = new ArrayList<EstateNodeInfo>();
    private List<Boolean> mSelectedmarked = new ArrayList<Boolean>();
    private DaiLiLouPanSelectEstateListAdapter mDaiLiLouPanEstateListAdapter;
    public  EstateNodeInfo  mCurrentSelect;

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
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "result: " + resp);
                            }
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_initAgent)) {
                                    mListDistrict.clear();

                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");

                                    JSONArray array_listDistrict = obj_data
                                            .optJSONArray("listDistrict");
                                    if (array_listDistrict != null) {
                                        for (int i = 0; i < array_listDistrict
                                                .length(); i++) {
                                            JSONObject object = array_listDistrict
                                                    .optJSONObject(i);
                                            if (object != null) {
                                                try {
                                                    DistrictNodeInfo districtNodeInfo = TransformUtil
                                                            .getEntityFromJson(
                                                                    object,
                                                                    DistrictNodeInfo.class);
                                                    if (districtNodeInfo != null) {
                                                        mListDistrict
                                                                .add(districtNodeInfo);
                                                    }

                                                } catch (InstantiationException e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                } catch (IllegalAccessException e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    mAllEstates.clear();
                                    JSONObject object_allEstatePage = obj_data
                                            .optJSONObject("allEstatePage");
                                    if (object_allEstatePage != null) {
                                        try {
                                            mEstatePageNodeInfo = TransformUtil
                                                    .getEntityFromJson(
                                                            object_allEstatePage,
                                                            PageNodeInfo.class);

                                            JSONArray array_data_allEstatePage = object_allEstatePage
                                                    .optJSONArray("data");
                                            if (array_data_allEstatePage != null) {
                                                for (int i = 0; i < array_data_allEstatePage
                                                        .length(); i++) {
                                                    JSONObject object = array_data_allEstatePage
                                                            .getJSONObject(i);
                                                    if (object != null) {
                                                        EstateNodeInfo node = TransformUtil
                                                                .getEntityFromJson(
                                                                        object,
                                                                        EstateNodeInfo.class);
                                                        if (node != null) {
                                                            mAllEstates.add(node);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (InstantiationException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }

                                    mSelectedEstates.clear();
                                    mSelectedmarked.clear();
                                    JSONArray array_listSelectEstate = obj_data
                                            .optJSONArray("listSelectEstate");
                                    if (array_listSelectEstate != null) {
                                        for (int i = 0; i < array_listSelectEstate
                                                .length(); i++) {
                                            JSONObject object = array_listSelectEstate
                                                    .getJSONObject(i);
                                            if (object != null) {
                                                try {
                                                    EstateNodeInfo node = TransformUtil
                                                            .getEntityFromJson(
                                                                    object,
                                                                    EstateNodeInfo.class);
                                                    if (node != null) {
                                                        for (int j = 0; j < mAllEstates
                                                                .size(); j++) {
                                                            if (mAllEstates
                                                                    .get(j)
                                                                    .getEstateid()
                                                                    .equals(node
                                                                            .getEstateid())) {
                                                                mAllEstates
                                                                        .get(j)
                                                                        .setSelected(
                                                                                true);
                                                                break;
                                                            }
                                                        }
                                                        mDailiEstates.add(node);
                                                        mSelectedEstates.add(node);
                                                        mSelectedmarked.add(true);
                                                    }

                                                } catch (InstantiationException e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                } catch (IllegalAccessException e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    mDaiLiLouPanEstateListAdapter
                                            .notifyDataSetChanged();
                                    MyLog.d(TAG, "数据"
                                            + mListView_listview_loupan
                                            .getAdapter().getCount());
                                    mListView_listview_loupan.postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    mListView_listview_loupan
                                                            .onRefreshComplete();
                                                }
                                            }, 300);
                                } else if (request_url
                                        .equals(ServerUrl.METHOD_queryListPage)) {
                                    JSONObject obj_data = jsonObject
                                            .getJSONObject("data");

                                    try {
                                        mEstatePageNodeInfo = TransformUtil
                                                .getEntityFromJson(obj_data,
                                                        PageNodeInfo.class);

                                        JSONArray array_data_allEstatePage = obj_data
                                                .optJSONArray("data");
                                        if (array_data_allEstatePage != null) {
                                            for (int i = 0; i < array_data_allEstatePage
                                                    .length(); i++) {
                                                JSONObject object = array_data_allEstatePage
                                                        .getJSONObject(i);
                                                if (object != null) {
                                                    EstateNodeInfo node = TransformUtil
                                                            .getEntityFromJson(
                                                                    object,
                                                                    EstateNodeInfo.class);
                                                    if (node != null) {
                                                        for (int j = 0; j < mSelectedEstates
                                                                .size(); j++) {
                                                            if (node.getEstateid()
                                                                    .equals(mSelectedEstates
                                                                            .get(j)
                                                                            .getEstateid())) {
                                                                node.setSelected(true);
                                                                break;
                                                            }
                                                        }
                                                        mAllEstates.add(node);
                                                    }
                                                }
                                            }

                                            mDaiLiLouPanEstateListAdapter
                                                    .notifyDataSetChanged();
                                        }
                                    } catch (InstantiationException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    mListView_listview_loupan.postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    mListView_listview_loupan
                                                            .onRefreshComplete();
                                                }
                                            }, 300);

                                } else if (request_url
                                        .equals(ServerUrl.METHOD_insertEkeempestatemodi)) {
                                    Toast.makeText(getActivity(), "提交成功!",
                                            Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(getActivity(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "请求出错!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getActivity(), "请求出错!", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getActivity(), "请求出错!", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }

            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_search, null);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        refreshData(null);

    }

    private void initViews(View view) {
        mCityName = AppContext.getInstance().getAppPref().cityId();
        mListView_listview_loupan = (PullToRefreshListView) view
                .findViewById(R.id.listview_loupan);

        mTextView_quxian = (TextView) view.findViewById(R.id.chongzhi_quxian);
        mTextView_pianqu = (TextView) view.findViewById(R.id.chongzhi_pianqu);
        mTextView_loupan = (TextView) view.findViewById(R.id.chongzhi_loupan);
        mDaiLiLouPanEstateListAdapter = new DaiLiLouPanSelectEstateListAdapter(
                getContext(), mAllEstates);
        mListView_listview_loupan.setAdapter(mDaiLiLouPanEstateListAdapter);

        mListView_listview_loupan.setMode(Mode.PULL_FROM_END);
        mListView_listview_loupan.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在加载");
        mListView_listview_loupan.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        mListView_listview_loupan.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("释放开始加载");

        mListView_listview_loupan
                .setOnRefreshListener(new OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // TODO Auto-generated method stub
                        if (mEstatePageNodeInfo.getCurrentPage() < mEstatePageNodeInfo
                                .getTotalPages()) {
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setRefreshingLabel("正在加载");
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setPullLabel("上拉加载更多");
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setReleaseLabel("释放开始加载");

                            int page = mEstatePageNodeInfo.getCurrentPage();
                            mEstatePageNodeInfo.setCurrentPage(page + 1);
                            refreshData("");
                        } else {
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setRefreshingLabel("没有更多数据");
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setPullLabel("没有更多数据");
                            mListView_listview_loupan.getLoadingLayoutProxy(
                                    false, true).setReleaseLabel("没有更多数据");

                            mListView_listview_loupan.postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            mListView_listview_loupan
                                                    .onRefreshComplete();
                                        }
                                    }, 500);
                        }
                    }

                });
        mListView_listview_loupan
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        // LinkedList<Integer>indexed = new
                        // LinkedList<Integer>();
                        // if (mAllEstates.get(position-1).isSelected()) {
                        // for (int i = 0; i < mSelectedEstates.size(); i++) {
                        // if
                        // (mSelectedEstates.get(i).getEstateid().equals(mAllEstates.get(position-1).getEstateid()))
                        // {
                        // indexed.add(i);
                        // mSelectedmarked.remove(i);
                        // break;
                        // }
                        // }
                        // for(int i=indexed.get(indexed.size()-1);i>=0;i--) {
                        // mSelectedmarked.remove(indexed.get(i).intValue());
                        // }
                        // }
                        // else {
                        // EstateNodeInfo node = new EstateNodeInfo();
                        // node.setEstateid(mAllEstates.get(position -
                        // 1).getEstateid());
                        // node.setEstatename(mAllEstates.get(position -
                        // 1).getEstatename());
                        // mSelectedEstates.add(node);
                        // mSelectedmarked.add(true);
                        // }
                        mTextView_quxian.setText(mAllEstates.get(position - 1)
                                .getDistrictname());
                        mTextView_pianqu.setText(mAllEstates.get(position - 1)
                                .getAreaname());
                        mEditText_et_content_find.setText(mAllEstates.get(
                                position - 1).getEstatename());
                        mAllEstates.get(position - 1).setSelected(
                                !mAllEstates.get(position - 1).isSelected());
                        mCurrentSelect=mAllEstates.get(position - 1);

                        mDaiLiLouPanEstateListAdapter.notifyDataSetChanged();

                    }
                });
        mTextView_quxian = (TextView) view.findViewById(R.id.chongzhi_quxian);
        mTextView_pianqu = (TextView) view.findViewById(R.id.chongzhi_pianqu);
        mTextView_loupan = (TextView) view.findViewById(R.id.chongzhi_loupan);
        mSearchBtn = (ImageView) view.findViewById(R.id.chongzhi_search);
        mSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mAllEstates.clear();
                mEstatePageNodeInfo.setCurrentPage(1);

                refreshData(mEditText_et_content_find.getText().toString());
            }
        });
        mEditText_et_content_find = (EditText) view
                .findViewById(R.id.et_content_find);

    }



    private void refreshData(String filter) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cityid", mCityName);
            obj.put("currentPage", mEstatePageNodeInfo.getCurrentPage());
            obj.put("pageSize", mEstatePageNodeInfo.getPageSize());
            if(!StringCheckHelper.isEmpty(filter)){
                obj.put("estatename", filter);
            }
            ClientHelper clientHelper = new ClientHelper(getContext(),
                    ServerUrl.METHOD_queryListPage, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
