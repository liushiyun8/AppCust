package com.eke.cust.tabmine.account_activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.SharePreferenceUtil;
import com.eke.cust.utils.TransformUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAccountActivity extends BaseActivity implements OnClickListener {
	private TextView mTextViewMoney;
	private TextView mTextViewCanWithdraw;
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
						if (request_url
								.equals(ServerUrl.METHOD_queryContractact)) {

						} else if (request_url
								.equals(ServerUrl.METHOD_contractactTX)) {

						}
						JSONObject jsonObject = new JSONObject(resp);
						String result = jsonObject.optString("result", "");
						if (result.equals(Constants.RESULT_SUCCESS)) {
							if (request_url
									.equals(ServerUrl.METHOD_queryContractact)) {
								JSONObject obj_data = jsonObject
										.optJSONObject("data");
								if (obj_data != null) {
									String feeid = TransformUtil.getString(
											obj_data, "feeid", "0");
									String moneycurrent = TransformUtil
											.getString(obj_data,
													"moneycurrent", "0");
									String moneycash = TransformUtil.getString(
											obj_data, "moneycash", "0");
									mTextViewMoney
											.setText(TextUtils
													.isEmpty(moneycurrent)
													&& !moneycurrent
															.equals("null") ? moneycurrent
													: "0");
									mTextViewCanWithdraw
											.setText(TextUtils
													.isEmpty(moneycash)
													&& !moneycash
															.equals("null") ? moneycash
													: "0");

								}
							} else if (request_url
									.equals(ServerUrl.METHOD_contractactTX)) {
								
								// FIXME 提现
								Toast.makeText(getApplicationContext(), "提现成功",
										Toast.LENGTH_SHORT).show();								

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
		setContentView(R.layout.activity_my_account);

		mTextViewMoney = (TextView) findViewById(R.id.txt_money);
		mTextViewCanWithdraw = (TextView) findViewById(R.id.txt_can_withdraw_money);
		// 按钮点击事件
		findViewById(R.id.tv_details).setOnClickListener(this);

		// 提现
		findViewById(R.id.layout_withdraw).setOnClickListener(this);
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(MyAccountActivity.this,
				ServerUrl.METHOD_queryContractact, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_details:
			UIHelper.startActivity(this, AccountDetailActivity.class);

			break;

		case R.id.layout_withdraw:
			showWithDrawDialog();

			break;

		default:
			break;
		}

	}

	private void showWithDrawDialog() {
		final Dialog dlg = new Dialog(this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(this);
		View viewContent = inflater.inflate(R.layout.dlg_withdraw, null);

		Button btn_submit = (Button) viewContent.findViewById(R.id.btn_submit);
		final EditText edt_money = (EditText) viewContent
				.findViewById(R.id.et_money);
		final EditText edt_password = (EditText) viewContent
				.findViewById(R.id.et_password);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkInput()) {
					WithDraw();
					dlg.dismiss();

				}

			}

			private boolean checkInput() {
				String userPasswordString = SharePreferenceUtil.getInstance(
						MyAccountActivity.this).getUserPwd();
				String moneyString = mTextViewCanWithdraw.getText().toString();
				String inputMoney = edt_money.getText().toString();
				if (TextUtils.isEmpty(inputMoney)) {
					Toast.makeText(getApplicationContext(), "请输入金额",
							Toast.LENGTH_SHORT).show();
					return false;

				}
				if (Integer.parseInt(moneyString) < Integer
						.parseInt(inputMoney)) {
					Toast.makeText(getApplicationContext(), "超过提现最大金额",
							Toast.LENGTH_SHORT).show();
					return false;

				}
				if (!userPasswordString.equals(edt_password.getText()
						.toString())) {
					Toast.makeText(getApplicationContext(), "原密码输入不正确",
							Toast.LENGTH_SHORT).show();
					return false;

				}
				return true;
			}
		});

		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels * 2 / 3;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}

	private void WithDraw() {

		JSONObject obj = new JSONObject();
		try {

			obj.put("psw",
					SharePreferenceUtil.getInstance(MyAccountActivity.this)
							.getUserPwd());

			ClientHelper clientHelper = new ClientHelper(
					MyAccountActivity.this, ServerUrl.METHOD_contractactTX,
					obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在提交提现申请...");
			clientHelper.isShowProgress(false);
			clientHelper.sendPost(false);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
