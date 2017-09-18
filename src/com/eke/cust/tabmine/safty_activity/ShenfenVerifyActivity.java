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

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class ShenfenVerifyActivity extends BaseActivity implements OnClickListener{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_mine_shenfen_verify);
		
		initActivity();
		
	}
	
	private void initActivity() {
		
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
		case R.id.tv_quit:{
		}
			
			break;
		default:
			break;
		}
	}
}
