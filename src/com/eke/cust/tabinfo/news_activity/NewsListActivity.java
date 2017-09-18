package com.eke.cust.tabinfo.news_activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseListActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabinfo.TabNewsNodeInfo;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NewsListActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener2<ListView>,OnItemClickListener{
	@InjectView(id= R.id.listview_main_news)
	private PullToRefreshListView refreshListView;
	
	private ArrayList<TabNewsNodeInfo> mNewsList = new ArrayList<TabNewsNodeInfo>();
	private CommonListAdapter<TabNewsNodeInfo> adapter;

	
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
							if (request_url.equals(ServerUrl.METHOD_getListNewsNotice)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								int totalRecords = JSONUtils.getInt(obj_data, "totalPages", 0);
								JSONArray jsonArray = obj_data.optJSONArray("data");

								if (jsonArray != null) {
									mNewsList=JSONUtils.getObjectList(jsonArray,TabNewsNodeInfo.class);
									if (_RefreshState != BaseListActivity.RefreshState.LS_LoadMore) {
										initListAdapter();
									} else {
										ArrayList<TabNewsNodeInfo> mItems = JSONUtils.getObjectList(jsonArray, TabNewsNodeInfo.class);
										adapter.addList(mItems);
									}


								}

								setRefreshLoadedState(totalRecords);
								onComplete();
							}
						}
						else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg", "出错!");
							Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(mContext, "请求出错!", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case Constants.TAG_FAIL:
					Toast.makeText(mContext, "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(mContext, "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}
	};


	@Override
	protected View onCreateContentView() {
		return inflateContentView(R.layout.fragment_news_list);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("新闻公告");
		registerLeftImageView(R.drawable.arrow_back);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setOnItemClickListener(this);

		refreshData();
	}



	//region 设置适配器
	private void initListAdapter() {
		adapter = new CommonListAdapter<TabNewsNodeInfo>(mContext, R.layout.layout_news_list_item_for_detail, mNewsList) {
			@Override
			public void convert(ViewHolder holder, TabNewsNodeInfo tabNewsNodeInfo, int position) {
				TextView mTxtdate = holder.findViewById(R.id.tv_date);
				TextView mTxtTitle = holder.findViewById(R.id.tv_title);
				ImageView new_image = holder.findViewById(R.id.new_image);
				if (!StringCheckHelper.isEmpty(tabNewsNodeInfo.newsicon)) {
					new_image.setImageBitmap(BitmapUtils.stringtoBitmap(tabNewsNodeInfo.newsicon));
				} else {
					new_image.setImageResource(R.drawable.icon_def_75);
				}
				if (!StringCheckHelper.isEmpty(tabNewsNodeInfo.showdate)) {
					mTxtdate.setText(tabNewsNodeInfo.showdate);
				} else {
					mTxtdate.setText("");
				}

				if (!StringCheckHelper.isEmpty(tabNewsNodeInfo.title)) {
					mTxtTitle.setText(tabNewsNodeInfo.title);
				} else {
					mTxtTitle.setText("");
				}

			}
		};
		refreshListView.setAdapter(adapter);
	}



	private void refreshData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("currentPage", kPage);
			obj.put("pageSize", kPageSize);
			
			ClientHelper clientHelper = new ClientHelper(mContext,
					ServerUrl.METHOD_getListNewsNotice, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		_RefreshState = BaseListActivity.RefreshState.LS_Refresh;
		kPage = 0;
		refreshData();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		_RefreshState = BaseListActivity.RefreshState.LS_LoadMore;
		kPage++;
		refreshData();

	}

	//region 设置list
	private void setRefreshLoadedState(int totalPages) {
		refreshListView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("正在加载");
		refreshListView.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载更多");
		refreshListView.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("释放开始加载");
		if (adapter.getCount() < totalPages) {
			refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
		} else {
			refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

		}

	}

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
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		TabNewsNodeInfo  tabNewsNodeInfo= (TabNewsNodeInfo) adapterView.getItemAtPosition(i);
		UIHelper.startActivity(mContext,NewsDetailActivity.class,tabNewsNodeInfo.newslink);

	}

	//endregion

}
