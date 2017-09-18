package com.eke.cust.tabhouse.key_manage_activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.nodeinfo.PageNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.TransformUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class KeyManageActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private TextView mTextView_tv_pos;
	private LinearLayout mLinearLayout_ll_bottom;
	private TextView mTextView_tv_shenqingjieshi;
	private TextView mTextView_tv_quxiaojieshi;
	private TextView mTextView_tv_beizhu;
	private TextView mTextView_tv_tousu;
	private TextView mTextView_tv_yaoshiyiqu;
	private TextView mTextView_tv_tongyi;
	private TextView mTextView_tv_cuihuan;
	private PullToRefreshListView mPullToRefreshListView_lv_key_track;
	
	private String propertyid;
	private String manageid;
	private String propertyno;
	private String estatename;
	private String room;
	private PageNodeInfo mPageNodeInfo = new PageNodeInfo();
	private List<KeyTrackNodeInfo> mKeyTrackNodeInfos = new ArrayList<KeyTrackNodeInfo>();
	
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
							if (request_url.equals(ServerUrl.METHOD_queryListByProperty)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								try {
									mPageNodeInfo =  TransformUtil.getEntityFromJson(
																obj_data,
																PageNodeInfo.class);
									
								} catch (InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
								
								
								JSONArray array_data = obj_data.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data.getJSONObject(i);
										if (object != null) {
											try {
												KeyTrackNodeInfo node = TransformUtil.getEntityFromJson(
														object,
														KeyTrackNodeInfo.class);
												if (node != null) {
													mKeyTrackNodeInfos.add(node);
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
								SimpleAdapter adapter = new SimpleAdapter(KeyManageActivity.this, getData(), R.layout.layout_tab_house_key_track_list_item,
						                new String[]{"date", "comment"},
						                new int[]{R.id.tv_date, R.id.tv_coment});
								
								mPullToRefreshListView_lv_key_track.setAdapter(adapter);
								
								mPullToRefreshListView_lv_key_track.postDelayed(new Runnable() {
						            @Override
						            public void run() {
						            	mPullToRefreshListView_lv_key_track.onRefreshComplete();
						            }
						        }, 300);
							}
							else if (request_url.equals(ServerUrl.METHOD_insertEkekeymanage)) {
								Toast.makeText(getApplicationContext(), "成功!", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tab_house_key_manage);
		propertyid = getIntent().getStringExtra("propertyid");
		manageid = getIntent().getStringExtra("manageid");
		propertyno = getIntent().getStringExtra("propertyno");
		estatename = getIntent().getStringExtra("estatename");
		room = getIntent().getStringExtra("room");
		
		initActivity();
		refreshData();
	}
	
	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout)findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mTextView_tv_pos = (TextView)findViewById(R.id.tv_pos);
		mLinearLayout_ll_bottom = (LinearLayout)findViewById(R.id.ll_bottom);
		mTextView_tv_shenqingjieshi = (TextView)findViewById(R.id.tv_shenqingjieshi);
		mTextView_tv_quxiaojieshi = (TextView)findViewById(R.id.tv_quxiaojieshi);
		mTextView_tv_beizhu = (TextView)findViewById(R.id.tv_beizhu);
		mTextView_tv_tousu = (TextView)findViewById(R.id.tv_tousu);
		mTextView_tv_yaoshiyiqu = (TextView)findViewById(R.id.tv_yaoshiyiqu);
		mTextView_tv_tongyi = (TextView)findViewById(R.id.tv_tongyi);
		mTextView_tv_cuihuan = (TextView)findViewById(R.id.tv_cuihuan);
		mPullToRefreshListView_lv_key_track = (PullToRefreshListView)findViewById(R.id.lv_key_track);
		
		mPullToRefreshListView_lv_key_track.setMode(Mode.PULL_FROM_END);
		mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
				
		mPullToRefreshListView_lv_key_track.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (mPageNodeInfo.getCurrentPage() < mPageNodeInfo.getTotalPages()) {
					int page = mPageNodeInfo.getCurrentPage();
					mPageNodeInfo.setCurrentPage(page+1);
					refreshData();
				}
				else {
					mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setRefreshingLabel("没有更多数据");
					mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setPullLabel("没有更多数据");
					mPullToRefreshListView_lv_key_track.getLoadingLayoutProxy(false, true).setReleaseLabel("没有更多数据");
					
					mPullToRefreshListView_lv_key_track.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	mPullToRefreshListView_lv_key_track.onRefreshComplete();
			            }
			        }, 500);
				}
			}
			
		});
				
		mKeyTrackNodeInfos.clear();
		
		mTextView_tv_shenqingjieshi.setOnClickListener(this);
		mTextView_tv_quxiaojieshi.setOnClickListener(this);
		mTextView_tv_beizhu.setOnClickListener(this);
		mTextView_tv_tousu.setOnClickListener(this);
		mTextView_tv_yaoshiyiqu.setOnClickListener(this);
		mTextView_tv_tongyi.setOnClickListener(this);
		mTextView_tv_cuihuan.setOnClickListener(this);
		
		mTextView_tv_title.setText("钥匙管理：" + propertyno);
		mTextView_tv_pos.setText(estatename + " " + room);
	}

	private void refreshData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyid", propertyid);
			obj.put("currentPage", mPageNodeInfo.getCurrentPage());
			obj.put("pageSize", mPageNodeInfo.getPageSize());
			
			ClientHelper clientHelper = new ClientHelper(KeyManageActivity.this,
					ServerUrl.METHOD_queryListByProperty, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();     
        
        for (int i = 0; i < mKeyTrackNodeInfos.size(); i++) {
        	KeyTrackNodeInfo node = mKeyTrackNodeInfos.get(i);
        	 Map<String, Object> map = new HashMap<String, Object>();
             map.put("date", DateUtil.getDateToString1(node.getTime()) + " " + node.getEmpname() + " " + node.getKeymanageid());
             map.put("comment", node.getDescription());
             list.add(map);
		}
        
        return list;
	}

	private void sendAction(String description) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyid", propertyid);
			obj.put("manageid", manageid);
			obj.put("description", description);
			
			ClientHelper clientHelper = new ClientHelper(KeyManageActivity.this,
					ServerUrl.METHOD_insertEkekeymanage, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在提交...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_shenqingjieshi:
		{
			sendAction("申请借匙");
		}
			break;
			
		case R.id.tv_quxiaojieshi:
		{
			sendAction("取消借匙");
		}
			break;
			
		case R.id.tv_beizhu:
		{
			sendAction("备注");
		}
			break;
			
		case R.id.tv_tousu:
		{
			sendAction("投诉");
		}
			break;
			
		case R.id.tv_yaoshiyiqu:
		{
			sendAction("钥匙已取");
		}
			break;
			
		case R.id.tv_tongyi:
		{
			sendAction("同意");
		}
			break;
			
		case R.id.tv_cuihuan:
		{
			sendAction("催还");
		}
			break;
		default:
			break;
		}
	}
}
