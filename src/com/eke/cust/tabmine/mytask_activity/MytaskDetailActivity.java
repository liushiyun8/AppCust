package com.eke.cust.tabmine.mytask_activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.TransformUtil;

public class MytaskDetailActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "MytaskActivity";
	private ListView mListView_listview_mytask;
	private List<MyTaskDetailNodeInfo> mNodesList = new ArrayList<MyTaskDetailNodeInfo>();
	private String type;
	
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
							if (request_url.equals(ServerUrl.METHOD_queryTypeList)) {
								mNodesList.clear();
								JSONArray array_data = jsonObject.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data.getJSONObject(i);
										if (object != null) {
											MyTaskDetailNodeInfo node;
											try {
												node = TransformUtil.getEntityFromJson(
																					object,
																					MyTaskDetailNodeInfo.class);
												
												if (node != null) {
													mNodesList.add(node);
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
								
								SimpleAdapter adapter = new SimpleAdapter(MytaskDetailActivity.this, getData(), R.layout.layout_tab_mine_mytask_detail_list_item,
						                new String[]{"date", "desc", "wancheng", "shengyu"},
						                new int[]{R.id.tv_date, R.id.tv_desc, R.id.tv_wancheng, R.id.tv_shengyu});
								
								mListView_listview_mytask.setAdapter(adapter);
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
		setContentView(R.layout.activity_tab_mine_mytask_detail);
		
		type = getIntent().getStringExtra("type");
		if (type == null) {
			finish();
			return;
		}
		
		initActivity();
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("type", type);
			ClientHelper clientHelper = new ClientHelper(MytaskDetailActivity.this,
					ServerUrl.METHOD_queryTypeList, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initActivity() {
		mListView_listview_mytask = (ListView)findViewById(R.id.listview_items);
	}
	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mNodesList.size(); i++) {
        	MyTaskDetailNodeInfo node = mNodesList.get(i);
        	int wancheng = 0;
        	if (node.getSuccesstasknum() != null && !node.getSuccesstasknum().equals("") && !node.getSuccesstasknum().equals("null")) {
				wancheng = Integer.valueOf(node.getSuccesstasknum());
			}
        	int zongshu = 0;
        	if (node.getTasknum() != null && !node.getTasknum().equals("") && !node.getTasknum().equals("null")) {
        		zongshu = Integer.valueOf(node.getTasknum());
			}
        	
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", DateUtil.getDateToString(node.getTime()));
            map.put("desc", node.getDetail());
            map.put("wancheng", wancheng+"");
            map.put("shengyu", (zongshu-wancheng) +"");
            list.add(map);
		}
        
        return list;
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
