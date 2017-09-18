package com.eke.cust.tabhouse.xianshifankui_activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.TransformUtil;

public class XianshifankuiActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "XianshifankuiActivity";
	public static final int MSG_CHECK_CHANGED = 1021;
	private TextView mTv_house;
	private EditText mEt_content;
	private ListView mListView_listview_mytask;
	private List<FanKuiNodeInfo> mNodesList = new ArrayList<FanKuiNodeInfo>();
	private FanKuiListAdapter mAdapter;
	private String propertyid;
	private String houseInfo;
	private int prefixCount = 0;
	
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);
			
			if (msg != null) {
				switch (msg.what) {
				case MSG_CHECK_CHANGED:
				{
					int index = msg.arg1;
					mNodesList.get(index).setSelected(!mNodesList.get(index).isSelected());
					updateSendTo();
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
							if (request_url.equals(ServerUrl.METHOD_getListSchedule)) {
								mNodesList.clear();
								JSONArray array_data = jsonObject.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data.getJSONObject(i);
										if (object != null) {
											FanKuiNodeInfo node;
											try {
												node = TransformUtil.getEntityFromJson(
																					object,
																					FanKuiNodeInfo.class);
												
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
								
								mAdapter = new FanKuiListAdapter(getApplicationContext(), mHandler, mNodesList);
								
								mListView_listview_mytask.setAdapter(mAdapter);
							}
							else if (request_url.equals(ServerUrl.METHOD_updateSchedule)) {
								Toast.makeText(getApplicationContext(), "提交成功!", Toast.LENGTH_SHORT).show();
								finish();
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
		setContentView(R.layout.activity_tab_house_fankui);
		
		propertyid = getIntent().getStringExtra("propertyid");
		houseInfo = getIntent().getStringExtra("house");
		if (propertyid == null) {
			finish();
			return;
		}
		
		initActivity();
		
		mTv_house.setText(houseInfo);
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyid", propertyid);
			ClientHelper clientHelper = new ClientHelper(XianshifankuiActivity.this,
					ServerUrl.METHOD_getListSchedule, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initActivity() {
		mTv_house = (TextView)findViewById(R.id.tv_house);
		mEt_content = (EditText)findViewById(R.id.et_content);
		mListView_listview_mytask = (ListView)findViewById(R.id.listview_items);
	}
	
	public void SendClicked(View view) {
		if (mNodesList.size() == 0) {
			return;
		}
		
		String content = mEt_content.getText().toString().trim();
		if (content.length() - prefixCount < 6) {
			Toast.makeText(getApplicationContext(), "反馈信息不少于6个字!", Toast.LENGTH_SHORT).show();
			return;
		}
		
//		String valide_content = content.substring(prefixCount);
		JSONArray array = new JSONArray();
		for (FanKuiNodeInfo node: mNodesList) {
			if (node.isSelected()) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("scheduleid", node.getScheduleid());
					obj.put("content", content);
					
					array.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ClientHelper clientHelper = new ClientHelper(XianshifankuiActivity.this,
				ServerUrl.METHOD_updateSchedule, array.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在提交反馈...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}
	
	private void updateSendTo() {
		StringBuilder stringBuilder = new StringBuilder();
		for (FanKuiNodeInfo node: mNodesList) {
			if (node.isSelected()) {
				if (stringBuilder.length() == 0) {
					stringBuilder.append("To ").append(node.getEmpname());
				}
				else {
					if (!stringBuilder.toString().contains(node.getEmpname())) {
						stringBuilder.append(node.getEmpname());
					}					
				}
				stringBuilder.append("、");
			}
		}
		if (stringBuilder.length() > 1) {
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
			stringBuilder.append(":");
		}
		
		mEt_content.setText(stringBuilder.toString());
		
		prefixCount = mEt_content.getText().toString().trim().length();
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
