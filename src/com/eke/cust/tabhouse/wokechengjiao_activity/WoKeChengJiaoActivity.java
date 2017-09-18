package com.eke.cust.tabhouse.wokechengjiao_activity;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.CodeUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.TransformUtil;

public class WoKeChengJiaoActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "WoKeChengJiaoActivity";
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private LinearLayout mLinearLayout_ll_info_border;
	private LinearLayout mLinearLayout_ll_info;
	private TextView mTextView_tv_housenumber;
	private TextView mTextView_tv_addr;
	private TextView mTextView_tv_deal_type;
	private TextView mTextView_tv_mianji;
	private TextView mTextView_tv_house_type;
	private TextView mTextView_tv_price;
	private TextView mTextView_tv_ruzhushijian;
	private TextView mTextView_tv_panyuan;
	private TextView mTextView_tv_genfang;
	private TextView mTextView_tv_beizhu;
	private EditText mEditText_et_house_id;
	private EditText mEditText_et_phone;
	private EditText mEditText_et_identifier;
	private TextView mTextView_tv_identifier;
	private Button mButton_btn_submit;

	private boolean mIsGotInfo = false;
	public static final int NO_UPDATE_CODE= 1;// 更Code

	private Handler mUIHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);

			if (msg != null) {
				switch (msg.what) {
				case NO_UPDATE_CODE:
					String code=(String) msg.obj;
					mTextView_tv_identifier.setText(code);
					break;

				}

			}
		}
	};
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
									.equals(ServerUrl.METHOD_getPropertyByNo)) {
								JSONObject obj_data = jsonObject
										.getJSONObject("data");

								try {
									WoKeChengJiaoNodeInfo mWoKeChengJiaoNodeInfo = TransformUtil
											.getEntityFromJson(obj_data,
													WoKeChengJiaoNodeInfo.class);
									if (mWoKeChengJiaoNodeInfo != null) {
										mTextView_tv_housenumber
												.setText(mWoKeChengJiaoNodeInfo
														.getPropertyno());
										mTextView_tv_addr
												.setText(mWoKeChengJiaoNodeInfo
														.getEstatename()
														+ " "
														+ mWoKeChengJiaoNodeInfo
																.getBuildno()
														+ "栋"
														+ mWoKeChengJiaoNodeInfo
																.getRoomno());
										mTextView_tv_deal_type
												.setText(mWoKeChengJiaoNodeInfo
														.getTrade());
										mTextView_tv_mianji
												.setText(mWoKeChengJiaoNodeInfo
														.getSquare() + "");
										mTextView_tv_house_type
												.setText(mWoKeChengJiaoNodeInfo
														.getCountf()
														+ "房"
														+ mWoKeChengJiaoNodeInfo
																.getCountt()
														+ "厅"
														+ mWoKeChengJiaoNodeInfo
																.getCountw()
														+ "卫");
										if (mWoKeChengJiaoNodeInfo.getTrade()
												.equals("出售")) {
											mTextView_tv_price
													.setText(mWoKeChengJiaoNodeInfo
															.getPrice()
															+ mWoKeChengJiaoNodeInfo
																	.getUnitname());
										} else {
											mTextView_tv_price
													.setText(mWoKeChengJiaoNodeInfo
															.getRentprice()
															+ mWoKeChengJiaoNodeInfo
																	.getRentunitname());
										}

										mTextView_tv_ruzhushijian
												.setText(DateUtil
														.getDateToString(mWoKeChengJiaoNodeInfo
																.getHandoverdate()));
										mTextView_tv_panyuan
												.setText(mWoKeChengJiaoNodeInfo
														.getEmpid1name());
										mTextView_tv_genfang
												.setText(mWoKeChengJiaoNodeInfo
														.getEmpid2name());
										mTextView_tv_beizhu
												.setText(mWoKeChengJiaoNodeInfo
														.getRemark());

										mLinearLayout_ll_info
												.setVisibility(View.VISIBLE);
										mIsGotInfo = true;
									}

								} catch (InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if (request_url
									.equals(ServerUrl.METHOD_addContract)) {
								Toast.makeText(getApplicationContext(), "成功!",
										Toast.LENGTH_SHORT).show();
								finish();
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
		setContentView(R.layout.activity_tab_house_wokechengjiao);

		initActivity();

	}

	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout) findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView) findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView) findViewById(R.id.tv_title);
		mLinearLayout_ll_info_border = (LinearLayout) findViewById(R.id.ll_info_border);
		mLinearLayout_ll_info = (LinearLayout) findViewById(R.id.ll_info);
		mTextView_tv_housenumber = (TextView) findViewById(R.id.tv_housenumber);
		mTextView_tv_addr = (TextView) findViewById(R.id.tv_addr);
		mTextView_tv_deal_type = (TextView) findViewById(R.id.tv_deal_type);
		mTextView_tv_mianji = (TextView) findViewById(R.id.tv_mianji);
		mTextView_tv_house_type = (TextView) findViewById(R.id.tv_house_type);
		mTextView_tv_price = (TextView) findViewById(R.id.tv_price);
		mTextView_tv_ruzhushijian = (TextView) findViewById(R.id.tv_ruzhushijian);
		mTextView_tv_panyuan = (TextView) findViewById(R.id.tv_panyuan);
		mTextView_tv_genfang = (TextView) findViewById(R.id.tv_genfang);
		mTextView_tv_beizhu = (TextView) findViewById(R.id.tv_beizhu);
		mEditText_et_house_id = (EditText) findViewById(R.id.et_house_id);
		mEditText_et_phone = (EditText) findViewById(R.id.et_phone);
		mButton_btn_submit = (Button) findViewById(R.id.btn_submit);
		mEditText_et_identifier = (EditText) findViewById(R.id.et_identifier);
		mTextView_tv_identifier = (TextView) findViewById(R.id.txt_identifier);
		mTextView_tv_identifier.setText((CodeUtils.getInstance().createCode()));
		mButton_btn_submit.setOnClickListener(this);
		mTextView_tv_identifier.setOnClickListener(this);

		mEditText_et_house_id.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				// Log.d(TAG, "len: " + str.length());
				if (str.length() == 8) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("propertyno", str);
						ClientHelper clientHelper = new ClientHelper(
								WoKeChengJiaoActivity.this,
								ServerUrl.METHOD_getPropertyByNo, obj
										.toString(), mHandler);
						clientHelper.setShowProgressMessage("正在获取数据...");
						clientHelper.isShowProgress(true);
						clientHelper.sendPost(true);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void toSubmit() {
		if (!mIsGotInfo) {
			Toast.makeText(getApplicationContext(), "请先获取房源数据!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String phone = mEditText_et_phone.getText().toString().trim();
		if (!RegularUtil.isMobileNO(phone)) {
			Toast.makeText(getApplicationContext(), "请输入有效的手机号!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String  codeString=mTextView_tv_identifier.getText().toString();
		String  inputcode=mEditText_et_identifier.getText().toString().trim();
		if(StringUtils.isEmpty(inputcode)){
			Toast.makeText(getApplicationContext(), "请输入验证码!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(codeString.equals(inputcode)){
			Toast.makeText(getApplicationContext(), "请输入正确的验证码!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		

		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyno", mEditText_et_house_id.getText().toString()
					.trim());
			obj.put("custtel", phone);
			ClientHelper clientHelper = new ClientHelper(
					WoKeChengJiaoActivity.this, ServerUrl.METHOD_addContract,
					obj.toString(), mHandler);
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
		case R.id.btn_submit: {
			toSubmit();
		}
		case R.id.txt_identifier: {
			Message  message=new Message();
			message.obj=CodeUtils.getInstance().createCode();
			message.what=NO_UPDATE_CODE;
			mUIHandler.sendMessage(message);
		}
			break;
		default:
			break;
		}
	}
}
