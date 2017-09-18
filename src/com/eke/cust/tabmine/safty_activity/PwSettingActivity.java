package com.eke.cust.tabmine.safty_activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;

public class PwSettingActivity extends BaseActivity implements OnClickListener{
	private EditText mEditText_et_your_orig_pwd;
	private EditText mEditText_et_your_number;
	private EditText mEditText_et_your_number_again;
	private Button mButton_btn_submit;
	
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
							if (request_url.equals(ServerUrl.METHOD_updatePassword)) {
								Toast.makeText(getApplicationContext(), "设置成功!", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tab_mine_safty_bankcard);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mEditText_et_your_orig_pwd = (EditText)findViewById(R.id.et_your_orig_pwd);
		mEditText_et_your_number = (EditText)findViewById(R.id.et_your_number);
		mEditText_et_your_number_again = (EditText)findViewById(R.id.et_your_number_again);
		mButton_btn_submit = (Button)findViewById(R.id.btn_submit);
		
		mButton_btn_submit.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit:{
			
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put("password", LoginDialog.genSecPwd(orig_pw));
//				obj.put("newpassword", LoginDialog.genSecPwd(new_pw));
//				
//				ClientHelper clientHelper = new ClientHelper(PwSettingActivity.this,
//						ServerUrl.METHOD_updatePassword, obj.toString(), mHandler);
//				clientHelper.setShowProgressMessage("正在设置...");
//				clientHelper.isShowProgress(true);
//				clientHelper.sendPost(true);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			String orig_pw = mEditText_et_your_orig_pwd.getText().toString().trim();
			if (orig_pw.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入原始密码!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String new_pw = mEditText_et_your_number.getText().toString().trim();
			if (new_pw.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入新密码!", Toast.LENGTH_SHORT).show();
				return;
			}
			else if (new_pw.length()<6 || new_pw.length() > 12) {
				Toast.makeText(getApplicationContext(), "请输入6-12位数字或字母做为新密码!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String new_pw_again = mEditText_et_your_number_again.getText().toString().trim();
			if (new_pw_again.equals("")) {
				Toast.makeText(getApplicationContext(), "请再次输入新密码!", Toast.LENGTH_SHORT).show();
				return;
			}
			else if (!new_pw_again.equals(new_pw)) {
				Toast.makeText(getApplicationContext(), "两次输入的密码不相同!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			JSONObject obj = new JSONObject();
			try {
//				obj.put("password", LoginDialog.genSecPwd(orig_pw));
//				obj.put("newpassword", LoginDialog.genSecPwd(new_pw));
				
				ClientHelper clientHelper = new ClientHelper(PwSettingActivity.this,
						ServerUrl.METHOD_updatePassword, obj.toString(), mHandler);
				clientHelper.setShowProgressMessage("正在设置...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);
			} catch (Exception e) {
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
