package com.eke.cust.tabmine.mytask_activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.TransformUtil;

public class MytaskActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "MytaskActivity";
	private ListView mListView_listview_mytask;
	private MyTaskAdapter mAdapter;
	private String[] items = {"写跟进", "房源图", "登记房源"};
	private String[] numbers = {"0", "0", "0"};
	private MyTaskNodeInfo mMyTaskNodeInfo;
	
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
							if (request_url.equals(ServerUrl.METHOD_initMissionExp)) {
								JSONObject obj_data = jsonObject.optJSONObject("data");
								if (obj_data != null) {
									try {
										mMyTaskNodeInfo =  TransformUtil
															.getEntityFromJson(
																	obj_data,
																	MyTaskNodeInfo.class);
										
										if (mMyTaskNodeInfo != null) {
											numbers[0] = mMyTaskNodeInfo.getMfollow();
											numbers[1] = (Integer.valueOf(mMyTaskNodeInfo.getMpropertypic()) + Integer.valueOf(mMyTaskNodeInfo.getMestatepic())) + "";
											numbers[2] = mMyTaskNodeInfo.getMproperty();
											
											mAdapter.notifyDataSetChanged();
										}
										
										
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
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
		setContentView(R.layout.activity_tab_mine_mytask);
		
		initActivity();
		
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(MytaskActivity.this,
				ServerUrl.METHOD_initMissionExp, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}
	
	private void initActivity() {
		mListView_listview_mytask = (ListView)findViewById(R.id.listview_mytask);
		mAdapter = new MyTaskAdapter(MytaskActivity.this, numbers, items);
		mListView_listview_mytask.setAdapter(mAdapter);
		mListView_listview_mytask.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
				{
					startDetailActivity("mfollow");
				}
					break;
					
				case 1:
				{
					startDetailActivity("mpropertypic");
				}
					break;
					
				case 2:
				{
					startDetailActivity("mproperty");
				}
					break;

				default:
					break;
				}
			}
		});
	}

	private void startDetailActivity(String type) {
		Intent intent = new Intent(MytaskActivity.this, MytaskDetailActivity.class);
		intent.putExtra("type", type);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		default:
			break;
		}
	}
}
