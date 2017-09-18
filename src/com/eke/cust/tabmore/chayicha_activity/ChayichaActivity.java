package com.eke.cust.tabmore.chayicha_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.TransformUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChayichaActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private TextView mTextView_tv_tip;
//	private RadioGroup mRadioGroup_rg_chaxun_fangshi;
//	private RadioButton mRadioButton_rb_shouji_chaxun;
//	private RadioButton mRadioButton_rb_housenumber_chaxun;
	private EditText mEditText_et_content;
	private Button mBtn_confirm;
	
	private int chayichaType = 0;
	private List<BlackNodeInfo> mBlackNodes = new ArrayList<BlackNodeInfo>();
	private List<GuideNodeInfo> mGuideNodes = new ArrayList<GuideNodeInfo>();
	private HouseChaNodeInfo mHouseChaNodeInfo;
	
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
							if (request_url.equals(ServerUrl.METHOD_queryHTByTel)) {
								{
									mBlackNodes.clear();
									JSONArray array_blacklist = jsonObject.optJSONArray("listback");
									if (array_blacklist != null) {
										for (int i = 0; i < array_blacklist.length(); i++) {
											JSONObject object = array_blacklist.optJSONObject(i);
											if (object != null) {
												BlackNodeInfo node;
												try {
													node = TransformUtil
															.getEntityFromJson(
																object,
																BlackNodeInfo.class);
													if (node != null) {
														mBlackNodes.add(node);
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
								}
								
								{
									mGuideNodes.clear();
									JSONArray array_listguide = jsonObject.optJSONArray("listguide");
									if (array_listguide != null) {
										for (int i = 0; i < array_listguide.length(); i++) {
											JSONObject object = array_listguide.optJSONObject(i);
											if (object != null) {
												GuideNodeInfo node;
												try {
													node = TransformUtil
															.getEntityFromJson(
																object,
																GuideNodeInfo.class);
													if (node != null) {
														mGuideNodes.add(node);
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
								}
								
								showBlackDlg();
							}
							else if (request_url.equals(ServerUrl.METHOD_queryHTByNo)) {
								JSONObject object_data = jsonObject.optJSONObject("data");
								if (object_data != null) {
									try {
										mHouseChaNodeInfo  = TransformUtil
												.getEntityFromJson(
														object_data,
														HouseChaNodeInfo.class);
										
										if (mHouseChaNodeInfo != null) {
											showHouseNodeInfoDlg();
										}
										else {
											Toast.makeText(getApplicationContext(), "没有数据！", Toast.LENGTH_SHORT).show();
										}
										
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Toast.makeText(getApplicationContext(), "没有数据！", Toast.LENGTH_SHORT).show();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Toast.makeText(getApplicationContext(), "没有数据！", Toast.LENGTH_SHORT).show();
									}
								}
								else {
									Toast.makeText(getApplicationContext(), "没有数据！", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tab_more_chayicha);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout)findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mTextView_tv_tip = (TextView)findViewById(R.id.tv_tip);
//		mRadioGroup_rg_chaxun_fangshi = (RadioGroup)findViewById(R.id.rg_chaxun_fangshi);
//		mRadioButton_rb_shouji_chaxun = (RadioButton)findViewById(R.id.rb_shouji_chaxun);
//		mRadioButton_rb_housenumber_chaxun = (RadioButton)findViewById(R.id.rb_housenumber_chaxun);
		mEditText_et_content = (EditText)findViewById(R.id.et_content);
		mBtn_confirm = (Button)findViewById(R.id.btn_confirm);
		
		mEditText_et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = s.toString();
				if (str.length() == 8) {
					chayichaType = 1;
					mBtn_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_chayicha_confirm));
//					mBtn_confirm.setTextColor(0xffffffff);
				}
				else if(str.length() == 11) {
//					if (LoginDialog.isMobileNO(str)) {
//						chayichaType = 0;
//						mBtn_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_chayicha_confirm));
////						mBtn_confirm.setTextColor(0xffffffff);
//					}
//					else {
//						mTextView_tv_tip.setVisibility(View.VISIBLE);
//						mBtn_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_chayicha_confirm_disabled));
//					}
//				}
//				else {
//					chayichaType = -1;
//					mBtn_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_chayicha_confirm_disabled));
////					mBtn_confirm.setTextColor(0xff868686);
//					mTextView_tv_tip.setVisibility(View.INVISIBLE);
				}
			}
		});

//		mRadioGroup_rg_chaxun_fangshi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if (checkedId == R.id.rb_shouji_chaxun) {
//					mEditText_et_content.setText("");
//					mEditText_et_content.setHint("根据手机号查");
//					chayichaType = 0;
//				}
//				else if (checkedId == R.id.rb_housenumber_chaxun) {
//					mEditText_et_content.setText("");
//					mEditText_et_content.setHint("根据房源号查");
//					chayichaType = 1;
//				}
//			}
//		});
		
		mBtn_confirm.setOnClickListener(this);
	}
	
	private List<Map<String, Object>> getGuideData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();     
        
        for (int i = 0; i < mGuideNodes.size(); i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
        	GuideNodeInfo node = mGuideNodes.get(i);
            map.put("date", DateUtil.getDateToString1(node.getEndtime()));
            map.put("desc", node.getDescription() + "  " + node.getEmpname() + node.getEmpno());
            list.add(map);
		}
        return list;
	}

	private void showBlackDlg() {
		final Dialog dlg = new Dialog(ChayichaActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(ChayichaActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_more_chayicha_black, null);
		
		final ListView listview_black = (ListView)viewContent.findViewById(R.id.listview_black);
		final ListView listview_guide = (ListView)viewContent.findViewById(R.id.listview_guide);
		
		SimpleAdapter adapter = new SimpleAdapter(ChayichaActivity.this, getGuideData(), R.layout.layout_more_chayicha_guide_list_item,
                new String[]{"date", "desc"},
                new int[]{R.id.tv_date, R.id.tv_desc});		
		listview_guide.setAdapter(adapter);
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		ChayichaActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(ChayichaActivity.this, 60);
		lp.height = dm.heightPixels - DensityUtil.dip2px(ChayichaActivity.this, 100);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
	
	private void showHouseNodeInfoDlg() {
		final Dialog dlg = new Dialog(ChayichaActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(ChayichaActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_more_chayicha_house, null);
		
		TextView mTextView_tv_housenumber;
		TextView mTextView_tv_addr;
		TextView mTextView_tv_deal_type;
		TextView mTextView_tv_mianji;
		TextView mTextView_tv_house_type;
		TextView mTextView_tv_price;
		TextView mTextView_tv_chaoxiang;
		TextView mTextView_tv_sheshi;
		TextView mTextView_tv_beizhu;
		TextView mTextView_tv_tv_qianyueriqi;
		
		mTextView_tv_housenumber = (TextView)viewContent.findViewById(R.id.tv_housenumber);
		mTextView_tv_addr = (TextView)viewContent.findViewById(R.id.tv_addr);
		mTextView_tv_deal_type = (TextView)viewContent.findViewById(R.id.tv_deal_type);
		mTextView_tv_mianji = (TextView)viewContent.findViewById(R.id.tv_mianji);
		mTextView_tv_house_type = (TextView)viewContent.findViewById(R.id.tv_house_type);
		mTextView_tv_price = (TextView)viewContent.findViewById(R.id.tv_price);
		mTextView_tv_chaoxiang = (TextView)viewContent.findViewById(R.id.tv_chaoxiang);
		mTextView_tv_sheshi = (TextView)viewContent.findViewById(R.id.tv_sheshi);
		mTextView_tv_beizhu = (TextView)viewContent.findViewById(R.id.tv_beizhu);
		mTextView_tv_tv_qianyueriqi = (TextView)viewContent.findViewById(R.id.tv_qianyueriqi);
		
		if(mHouseChaNodeInfo != null) {
			mTextView_tv_housenumber.setText(mHouseChaNodeInfo.getPropertyno());
			mTextView_tv_addr.setText(mHouseChaNodeInfo.getEstatename() + " " + mHouseChaNodeInfo.getBuildno() + "栋" + mHouseChaNodeInfo.getRoomno());
			mTextView_tv_deal_type.setText(mHouseChaNodeInfo.getTrade());
			mTextView_tv_mianji.setText(mHouseChaNodeInfo.getSquare() + "");
			mTextView_tv_house_type.setText(mHouseChaNodeInfo.getCountf()+ "房" + mHouseChaNodeInfo.getCountt()+ "厅"+mHouseChaNodeInfo.getCountw()+ "卫");
			if (mHouseChaNodeInfo.getTrade().equals("出售")) {
				mTextView_tv_price.setText(mHouseChaNodeInfo.getPrice() + mHouseChaNodeInfo.getUnitname());
			}
			else {
				mTextView_tv_price.setText(mHouseChaNodeInfo.getRentprice() + mHouseChaNodeInfo.getRentunitname());
			}
			
			mTextView_tv_chaoxiang.setText(mHouseChaNodeInfo.getPropertydirection());
			mTextView_tv_sheshi.setText(mHouseChaNodeInfo.getPropertyfurniture());
			mTextView_tv_beizhu.setText(mHouseChaNodeInfo.getRemark());
			mTextView_tv_tv_qianyueriqi.setText(DateUtil.getDateToString(mHouseChaNodeInfo.getHandoverdate()));
		}
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(ChayichaActivity.this, 100);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_confirm:
		{
			if (chayichaType == -1) {
				return;
			}
			String str = mEditText_et_content.getText().toString().trim();
			if (str.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入有效信息!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			JSONObject obj = new JSONObject();
			try {
				String url = "";
				if (chayichaType == 0) {
					obj.put("blacktel", str);
					url = ServerUrl.METHOD_queryHTByTel;
				}
				else{
					obj.put("propertyno", str);
					url = ServerUrl.METHOD_queryHTByNo;
				}
				
				ClientHelper clientHelper = new ClientHelper(ChayichaActivity.this,
						url, obj.toString(), mHandler);
				clientHelper.setShowProgressMessage("正在进行查询...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			break;
			
		default:
			break;
		}
	}
}
