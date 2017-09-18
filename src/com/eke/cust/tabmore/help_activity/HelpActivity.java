package com.eke.cust.tabmore.help_activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.eke.cust.BaseExpandaleListActivity;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.GlobalDaiKan;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.MyLog;

public class HelpActivity extends BaseExpandaleListActivity implements OnClickListener{
	private static final String TAG = "HelpActivity";
	public static final int MSG_UPDATE_GROUP = 10001;
	public static final int MSG_UPDATE_CHILD = 10002;
	private ExpandableListView mListView = null;
	private HelpListAdapter adapter;
	private GlobalDaiKan mGlobalDaiKan;
	
	private LinkedList<String> mTitleList = new LinkedList<String>();
	private List<LinkedList<HelpNodeInfo>> mSubTitleList = new ArrayList<LinkedList<HelpNodeInfo>>();
	
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);
			
			if (msg != null) {
				switch (msg.what) {
				case MSG_UPDATE_GROUP:
				{
					int group = msg.arg1;
					int action = msg.arg2;
					MyLog.d(TAG, "group = " + group);
					for (int i = 0; i < mSubTitleList.size(); i++) {
						LinkedList<HelpNodeInfo> list = mSubTitleList.get(i);
						for (int j = 0; j < list.size(); j++) {
							list.get(j).setExpanded(false);
						}
					}
					
					for (int j = 0; j < adapter.getGroupCount(); j++) {
						if (j != group) {
							mListView.collapseGroup(j);
						}
					}
					
					if (action == 0) {
						mListView.collapseGroup(group);
					}
					else {
						mListView.expandGroup(group);
					}
					
				}
					break;
					
				case MSG_UPDATE_CHILD:
				{
					int group = msg.arg1;
					int child = msg.arg2;
					MyLog.d(TAG, "group = " + group + ", child = " + child);
					for (int i = 0; i < mSubTitleList.size(); i++) {
						LinkedList<HelpNodeInfo> list = mSubTitleList.get(i);
						for (int j = 0; j < list.size(); j++) {
								list.get(j).setExpanded(false);
							
							if (i == group && j == child) {
								String action = (String)msg.obj;
								if (action.equals("0")) {
									list.get(j).setExpanded(false);
								}
								else {
									list.get(j).setExpanded(true);
								}
								
							}
						}
					}
					adapter.notifyDataSetChanged();
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
							if (request_url.equals(ServerUrl.METHOD_getAboutUsHelp)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								if (obj_data != null) {
									JSONArray array_data = obj_data.optJSONArray("data");
									if (array_data != null) {
										for (int i = 0; i < array_data.length(); i++) {
											JSONObject object = array_data.getJSONObject(i);
											
											if (object != null) {
												mTitleList.add(object.optString("subject"));
												LinkedList<HelpNodeInfo> subTitleContent = new LinkedList<HelpNodeInfo>();
												
												JSONArray array_data_subject = object.optJSONArray("data");
												if (array_data_subject != null) {
													for (int j = 0; j < array_data_subject.length(); j++) {
														JSONObject object_sub = array_data_subject.getJSONObject(j);
														if (object_sub != null) {
															HelpNodeInfo node = new HelpNodeInfo();
															node.setTitle(object_sub.optString("subject"));
															node.setContent(object_sub.optString("value"));
															subTitleContent.add(node);
														}
														
													}
												}
												
												mSubTitleList.add(subTitleContent);
											}
										}
									}
									
									adapter = new HelpListAdapter(HelpActivity.this, mHandler, mTitleList, mSubTitleList);
									mListView.setAdapter(adapter);
								}
								
//								JSONArray array = obj_data.names();
//								if (array != null) {
//									for (int i = 0; i < array.length(); i++) {
//										String key = array.getString(i);
//										mTitleList.add(key);
//										
//										LinkedList<HelpNodeInfo> subTitleContent = new LinkedList<HelpNodeInfo>();
//										JSONObject obj = obj_data.optJSONObject(key);
//										if (obj != null) {
//											JSONArray array_sub = obj.names();
//											if (array_sub != null) {
//												for (int j = 0; j < array_sub.length(); j++) {
//													HelpNodeInfo node = new HelpNodeInfo();
//													
//													String key_sub = array_sub.getString(j);
//													String obj_sub = obj.optString(key_sub);
//													if (obj_sub != null) {
//														node.setTitle(key_sub);
//														node.setContent(obj_sub);
//														subTitleContent.add(node);
//													}
//												}
//											}
//										}
//										mSubTitleList.add(subTitleContent);
//									}
//									
//									
//								}
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
		setContentView(R.layout.activity_tab_more_help);
		
		mGlobalDaiKan = new GlobalDaiKan(this, getApplicationContext());
        mGlobalDaiKan.createGenFangFloatView();
        
		initActivity();
		
	}
	
	private void initActivity() {
		mListView = getExpandableListView();
		mListView.setCacheColorHint(0);// 拖动时避免出现黑色
		mListView.setDivider(null);// 去掉每项下面的黑线(分割线)
		mListView.setGroupIndicator(null);// 去除组默认箭头图标
		
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(HelpActivity.this,
				ServerUrl.METHOD_getAboutUsHelp, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}

	@Override
    protected void onResume() {
        super.onResume();
        if (mGlobalDaiKan != null) {
        	mGlobalDaiKan.updateViewStatus();
		}
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
    	if (mGlobalDaiKan != null) {
        	mGlobalDaiKan.removeFloatView();
		}
    	
        super.onDestroy();
    }

    public void back(View view) {
        finish();
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
