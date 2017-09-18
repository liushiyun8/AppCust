package com.eke.cust.tabhouse.backend_info_activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.TransformUtil;

public class BackendInfoActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "BackendInfoActivity";
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private TextView mTextView_tv_housenumber;
	private TextView mTextView_tv_addr;
	private TextView mTextView_tv_deal_type;
	private TextView mTextView_tv_mianji;
	private TextView mTextView_tv_house_type;
	private TextView mTextView_tv_price;
	private TextView mTextView_tv_chaoxiang;
	private TextView mTextView_tv_sheshi;
	private TextView mTextView_tv_ruzhushijian;
	private TextView mTextView_tv_kanfangfangshi;
	private TextView mTextView_tv_panyuan;
	private TextView mTextView_tv_genfang;
	private TextView mTextView_tv_beizhu;
	private TextView mTextView_tv_housenumber_bottom;
	private TextView mTextView_tv_yezhu;
	private TextView mTextView_tv_zhidinglianxi;
	private TextView mTextView_tv_tepan;
	private TextView mTextView_tv_kehu;
	
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
							if (request_url.equals(ServerUrl.METHOD_getPropertyById)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								try {
									BackEndNodeInfo mBackEndNodeInfo = TransformUtil
														.getEntityFromJson(
																obj_data,
																BackEndNodeInfo.class);
									if (mBackEndNodeInfo != null) {
										mTextView_tv_housenumber.setText(mBackEndNodeInfo.getPropertyno());
										mTextView_tv_addr.setText(mBackEndNodeInfo.getEstatename() + " " + mBackEndNodeInfo.getBuildno() + "栋" + mBackEndNodeInfo.getRoomno());
										mTextView_tv_deal_type.setText(mBackEndNodeInfo.getTrade());
										mTextView_tv_mianji.setText(mBackEndNodeInfo.getSquare() + "");
										mTextView_tv_house_type.setText(mBackEndNodeInfo.getCountf()+ "房" + mBackEndNodeInfo.getCountt()+ "厅"+mBackEndNodeInfo.getCountw()+ "卫");
										if (mBackEndNodeInfo.getTrade().equals("出售")) {
											mTextView_tv_price.setText(mBackEndNodeInfo.getPrice() + mBackEndNodeInfo.getUnitname());
										}
										else {
											mTextView_tv_price.setText(mBackEndNodeInfo.getRentprice() + mBackEndNodeInfo.getRentunitname());
										}
										
										mTextView_tv_chaoxiang.setText(mBackEndNodeInfo.getPropertydirection());
										mTextView_tv_sheshi.setText(mBackEndNodeInfo.getPropertyfurniture());
										mTextView_tv_ruzhushijian.setText(DateUtil.getDateToString(mBackEndNodeInfo.getHandoverdate()));
										mTextView_tv_kanfangfangshi.setText(mBackEndNodeInfo.getPropertylook());
										mTextView_tv_panyuan.setText(mBackEndNodeInfo.getEmpsourcename());
										
										StringBuilder genfang = new StringBuilder();
										if (mBackEndNodeInfo.getEmpfollowname() != null && !mBackEndNodeInfo.getEmpfollowname().equals("null")) {
											genfang.append(mBackEndNodeInfo.getEmpfollowname());
											
											if (mBackEndNodeInfo.getEmpfollowtel() != null && !mBackEndNodeInfo.getEmpfollowtel().equals("null")) {
												genfang.append(", ");
												genfang.append(mBackEndNodeInfo.getEmpfollowtel());
											}
										}
										else {
											if (mBackEndNodeInfo.getEmpfollowtel() != null && !mBackEndNodeInfo.getEmpfollowtel().equals("null")) {
												genfang.append(mBackEndNodeInfo.getEmpfollowtel());
											}
										}
										mTextView_tv_genfang.setText(formatText(genfang.toString()));
										mTextView_tv_beizhu.setText(mBackEndNodeInfo.getRemark());
										mTextView_tv_housenumber_bottom.setText(mBackEndNodeInfo.getBuildno() + "栋" + mBackEndNodeInfo.getRoomno());
										
										StringBuilder owner = new StringBuilder();
										if (mBackEndNodeInfo.getOwnername() != null && !mBackEndNodeInfo.getOwnername().equals("null")) {
											owner.append(mBackEndNodeInfo.getOwnername());
											
											if (mBackEndNodeInfo.getOwnertel() != null && !mBackEndNodeInfo.getOwnertel().equals("null")) {
												owner.append(", ");
												owner.append(mBackEndNodeInfo.getOwnertel());
											}
										}
										else {
											if (mBackEndNodeInfo.getOwnertel() != null && !mBackEndNodeInfo.getOwnertel().equals("null")) {
												owner.append(mBackEndNodeInfo.getOwnertel());
											}
										}
										mTextView_tv_yezhu.setText(formatText(owner.toString()));
										mTextView_tv_zhidinglianxi.setText(mBackEndNodeInfo.getContactname());
										mTextView_tv_tepan.setText("");
										mTextView_tv_kehu.setText("");
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
		setContentView(R.layout.activity_tab_house_backend_info);
		
		initActivity();
		
		String propertyid = getIntent().getStringExtra("propertyid");
		if (propertyid != null && !propertyid.equals("")) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("propertyid", propertyid);
				ClientHelper clientHelper = new ClientHelper(BackendInfoActivity.this,
						ServerUrl.METHOD_getPropertyById, obj.toString(), mHandler);
				clientHelper.setShowProgressMessage("正在获取数据...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout)findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mTextView_tv_housenumber = (TextView)findViewById(R.id.tv_housenumber);
		mTextView_tv_addr = (TextView)findViewById(R.id.tv_addr);
		mTextView_tv_deal_type = (TextView)findViewById(R.id.tv_deal_type);
		mTextView_tv_mianji = (TextView)findViewById(R.id.tv_mianji);
		mTextView_tv_house_type = (TextView)findViewById(R.id.tv_house_type);
		mTextView_tv_price = (TextView)findViewById(R.id.tv_price);
		mTextView_tv_chaoxiang = (TextView)findViewById(R.id.tv_chaoxiang);
		mTextView_tv_sheshi = (TextView)findViewById(R.id.tv_sheshi);
		mTextView_tv_ruzhushijian = (TextView)findViewById(R.id.tv_ruzhushijian);
		mTextView_tv_kanfangfangshi = (TextView)findViewById(R.id.tv_kanfangfangshi);
		mTextView_tv_panyuan = (TextView)findViewById(R.id.tv_panyuan);
		mTextView_tv_genfang = (TextView)findViewById(R.id.tv_genfang);
		mTextView_tv_beizhu = (TextView)findViewById(R.id.tv_beizhu);
		mTextView_tv_housenumber_bottom = (TextView)findViewById(R.id.tv_housenumber_bottom);
		mTextView_tv_yezhu = (TextView)findViewById(R.id.tv_yezhu);
		mTextView_tv_zhidinglianxi = (TextView)findViewById(R.id.tv_zhidinglianxi);
		mTextView_tv_tepan = (TextView)findViewById(R.id.tv_tepan);
		mTextView_tv_kehu = (TextView)findViewById(R.id.tv_kehu);
	}

	private SpannableStringBuilder formatText(String str) {
		SpannableStringBuilder builder = new SpannableStringBuilder(str); 
		if (str.contains(",")) {
			int index = str.indexOf(",");
			
			
			ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE); 
			builder.setSpan(blueSpan, index+1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			return builder;
		}
		return builder;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_search:
			
			break;
		default:
			break;
		}
	}
}
