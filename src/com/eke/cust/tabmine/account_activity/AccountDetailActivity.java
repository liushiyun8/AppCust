package com.eke.cust.tabmine.account_activity;

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
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AccountDetailActivity extends BaseActivity {
	private int currentPage = 1;
	private int pageSize = 20;
	private int totalRecords = 0;
	private int totalPages = 0;
	private PullToRefreshListView mListView;
	private CommonListAdapter<AccountInfo> adapter;
	private List<AccountInfo> mAccountInfosList = new ArrayList<AccountInfo>();

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
									.equals(ServerUrl.METHOD_getMyFollow)) {
								JSONObject obj_data = jsonObject
										.getJSONObject("data");
								currentPage = obj_data.optInt("currentPage");
								pageSize = obj_data.optInt("pageSize");

								JSONArray jsonArray = obj_data
										.optJSONArray("data");
								if (jsonArray != null) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject obj = jsonArray
												.getJSONObject(i);

										// adapter.addList(node);

									}

									adapter.notifyDataSetChanged();
								}

								mListView.postDelayed(new Runnable() {
									@Override
									public void run() {
										mListView.onRefreshComplete();
									}
								}, 300);

							}
						} else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg",
									"出错!");
							Toast.makeText(getApplicationContext(), errorMsg,
									Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "请求出错!",
								Toast.LENGTH_SHORT).show();
					}

					break;

				case Constants.TAG_FAIL:
					Toast.makeText(getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
		initActivity();
		refreshDate();
	}

	private void initActivity() {
		mListView = (PullToRefreshListView) findViewById(R.id.listview_track);
		mListView.setMode(Mode.PULL_FROM_END);
		mListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mListView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");

		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (currentPage < totalPages) {
					currentPage++;
					refreshDate();
				} else {
					mListView.getLoadingLayoutProxy(false, true)
							.setRefreshingLabel("没有更多数据");
					mListView.getLoadingLayoutProxy(false, true).setPullLabel(
							"没有更多数据");
					mListView.getLoadingLayoutProxy(false, true)
							.setReleaseLabel("没有更多数据");

					mListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							mListView.onRefreshComplete();
						}
					}, 500);
				}
			}

		});

		adapter = new CommonListAdapter<AccountInfo>(
				AccountDetailActivity.this, R.layout.item_account_detail,
				mAccountInfosList) {

			@Override
			public void convert(ViewHolder holder, AccountInfo t, int position) {
				// TODO Auto-generated method stub

			}

		};
		mListView.setAdapter(adapter);
	}

	private void refreshDate() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("currentPage", currentPage);
			obj.put("pageSize", pageSize);

			ClientHelper clientHelper = new ClientHelper(
					AccountDetailActivity.this, ServerUrl.METHOD_queryMXPage,
					obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
