package com.eke.cust.tabmine.profile_activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.TransformUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class DaiLiLouPanActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "DaiLiLouPanActivity";
	public static DaiLiLouPanActivity instance;
	private ImageView mImageView_iv_setting;
	private PullToRefreshListView mListView_lv_daililoupan;
	
	private DaiLiLouPanListAdapter mDaiLiLouPanListAdapter;
	private EstatePageNodeInfo mEstatePageNodeInfo;
	public List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
	public List<EstateNodeInfo> mAllEstates = new ArrayList<EstateNodeInfo>();
	public List<EstateNodeInfo> mSelectedEstates = new ArrayList<EstateNodeInfo>();
	
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
						if(BuildConfig.DEBUG) {
							Log.d(TAG, "result: " + resp);
						}
						if (result.equals(Constants.RESULT_SUCCESS)) {
							if (request_url.equals(ServerUrl.METHOD_initAgent)) {
								mListDistrict.clear();
								
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								JSONArray array_listDistrict = obj_data.optJSONArray("listDistrict");
								if (array_listDistrict != null) {
									for (int i = 0; i < array_listDistrict.length(); i++) {
										JSONObject object = array_listDistrict.getJSONObject(i);
										if (object != null) {
											try {
												DistrictNodeInfo districtNodeInfo = TransformUtil
																				.getEntityFromJson(
																					object,
																					DistrictNodeInfo.class);
												if (districtNodeInfo != null) {
													mListDistrict.add(districtNodeInfo);
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
								
								mAllEstates.clear();
								JSONObject object_allEstatePage = obj_data.optJSONObject("allEstatePage");
								if (object_allEstatePage != null) {
									try {
										mEstatePageNodeInfo = TransformUtil
												.getEntityFromJson(
														object_allEstatePage,
														EstatePageNodeInfo.class);
										JSONArray array_data_allEstatePage = object_allEstatePage.optJSONArray("data");
										if (array_data_allEstatePage != null) {
											for (int i = 0; i < array_data_allEstatePage.length(); i++) {
												JSONObject object = array_data_allEstatePage.getJSONObject(i);
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
									}
								}
								
								mSelectedEstates.clear();
								JSONArray array_listSelectEstate = obj_data.optJSONArray("listSelectEstate");
								if (array_listSelectEstate != null) {
									for (int i = 0; i < array_listSelectEstate.length(); i++) {
										JSONObject object = array_listSelectEstate.getJSONObject(i);
										if (object != null) {
											try {
												EstateNodeInfo node = TransformUtil
																				.getEntityFromJson(
																					object,
																					EstateNodeInfo.class);
												if (node != null) {
													mSelectedEstates.add(node);
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
								
								mDaiLiLouPanListAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_tab_mine_profile_daililoupan);
		instance = this;
		initActivity();
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("val", GlobalSPA.getInstance(getApplicationContext()).getStringValueForKey(GlobalSPA.KEY_CITYID));
//			obj.put("cityname", "深圳");
			
			ClientHelper clientHelper = new ClientHelper(DaiLiLouPanActivity.this,
					ServerUrl.METHOD_initAgent, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initActivity() {
		mImageView_iv_setting = (ImageView)findViewById(R.id.iv_sp_history);
		mListView_lv_daililoupan = (PullToRefreshListView)findViewById(R.id.lv_daililoupan);
		mListView_lv_daililoupan.setMode(Mode.PULL_FROM_END);
		mListView_lv_daililoupan.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mImageView_iv_setting.setOnClickListener(this);
		
		mDaiLiLouPanListAdapter = new DaiLiLouPanListAdapter(DaiLiLouPanActivity.this, mSelectedEstates);
		mListView_lv_daililoupan.setAdapter(mDaiLiLouPanListAdapter);
		
//		for (int i = 0; i < 10; i++) {
//			EstateNodeInfo node  =  new EstateNodeInfo();
//			node.setEstatename("test" + i);
//			mSelectedEstates.add(node);
//		}
	}


	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_sp_history:
		{
			Intent intent = new Intent(DaiLiLouPanActivity.this, DaiLiChongZhiActivity.class);
			startActivity(intent);
		}
			
			break;
			
		default:
			break;
		}
	}
}
