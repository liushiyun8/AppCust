package com.eke.cust.tabmine.safty_activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class BankcardActivity extends BaseActivity implements OnClickListener{
	private EditText mEditText_et_your_orig_pwd;
	private EditText mEditText_et_your_number;
	private EditText mEditText_et_your_number_again;
	private Button mButton_btn_submit;
	
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


	private void showLogoutDlg() {
		final Dialog dlg = new Dialog(this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(this);
		View viewContent = inflater.inflate(R.layout.dialog_logout, null);
		
		Button btn_left	= (Button)viewContent.findViewById(R.id.btn_left);
		Button btn_right = (Button)viewContent.findViewById(R.id.btn_right);
		
		btn_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dlg.dismiss();
			}
		});
		
		btn_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				dlg.dismiss();
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(false);
		
		DisplayMetrics  dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels*2/3;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit:{
		}
			
			break;
		default:
			break;
		}
	}
}
