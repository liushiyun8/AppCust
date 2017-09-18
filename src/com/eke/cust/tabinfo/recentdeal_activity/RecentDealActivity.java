package com.eke.cust.tabinfo.recentdeal_activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class RecentDealActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private List<RecentDealNodeInfo> mNodeInfosList = new ArrayList<RecentDealNodeInfo>();
	private RecentDealListAdapter mAdapter;
	
	private int currentPage = 1;
	private int pageSize = 20;
	private int totalRecords = 0;
	private String orderColumn = null;
	private boolean orderASC = false;
	private int recordStart = 0;
	private int totalPages = 0;
	
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
							if (request_url.equals(ServerUrl.METHOD_getMyDeptContract)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								currentPage = obj_data.optInt("currentPage");
								pageSize = obj_data.optInt("pageSize");
								totalRecords = obj_data.optInt("totalRecords");
								orderColumn = obj_data.optString("orderColumn");
								orderASC = obj_data.optBoolean("orderASC");
								
								recordStart = obj_data.optInt("recordStart");
								totalPages = obj_data.optInt("totalPages");
								
								JSONArray jsonArray = obj_data.optJSONArray("data");
								if (jsonArray != null) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject obj = jsonArray.getJSONObject(i);
										
										RecentDealNodeInfo node = new RecentDealNodeInfo();
										node.setContractid(obj.optString("contractid", ""));
										node.setContractdate(obj.optString("contractdate", ""));
										node.setTrade(obj.optString("trade", ""));
										node.setPrice(obj_data.optInt("price")+"");
										node.setEstatename(obj.optString("estatename", ""));
										node.setPropertyno(obj.optString("propertyno", ""));
										node.setRoomno(obj.optString("roomno", ""));
										node.setBuildno(obj.optString("buildno", ""));
										node.setEmpname(obj.optString("empname", ""));
										node.setEkesource(obj.optString("ekesource", ""));
										node.setEkeservice(obj.optString("ekeservice", ""));
										
										mNodeInfosList.add(node);
									}
									
									mAdapter.notifyDataSetChanged();
								}
								
								mListView.postDelayed(new Runnable() {
						            @Override
						            public void run() {
						            	mListView.onRefreshComplete();
						            }
						        }, 300);
							}
						}
						else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg", "出错!");
							Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case Constants.TAG_FAIL:
					Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_info_recent_deal);
		
		initActivity();
		
		refreshDate();
	}
	
	private void initActivity() {
		mListView = (PullToRefreshListView)findViewById(R.id.listview_recentdeal);
		mListView.setMode(Mode.PULL_FROM_END);
		mListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mListView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
				
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (currentPage < totalPages) {
					currentPage ++;
					refreshDate();
				}
				else {
					mListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("没有更多数据");
					mListView.getLoadingLayoutProxy(false, true).setPullLabel("没有更多数据");
					mListView.getLoadingLayoutProxy(false, true).setReleaseLabel("没有更多数据");
					
					mListView.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	mListView.onRefreshComplete();
			            }
			        }, 500);
				}
			}
		});
		
		mNodeInfosList.clear();
		
		mAdapter = new RecentDealListAdapter(RecentDealActivity.this, mNodeInfosList);
		mListView.setAdapter(mAdapter);
	}
	
	private void refreshDate() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("currentPage", currentPage);
			obj.put("pageSize", pageSize);
			
			ClientHelper clientHelper = new ClientHelper(RecentDealActivity.this,
					ServerUrl.METHOD_getMyDeptContract, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
