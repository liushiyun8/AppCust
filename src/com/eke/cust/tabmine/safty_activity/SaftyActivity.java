package com.eke.cust.tabmine.safty_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class SaftyActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout mRelativeLayout_rl_fanyong;
	private RelativeLayout mRelativeLayout_rl_mimashezhi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_mine_safty);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mRelativeLayout_rl_fanyong = (RelativeLayout)findViewById(R.id.rl_fanyong);
		mRelativeLayout_rl_mimashezhi = (RelativeLayout)findViewById(R.id.rl_mimashezhi);
		
		mRelativeLayout_rl_fanyong.setOnClickListener(this);
		mRelativeLayout_rl_mimashezhi.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_fanyong:
		{
			Intent intent = new Intent(SaftyActivity.this, FanyongActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.rl_mimashezhi:
		{
			Intent intent = new Intent(SaftyActivity.this, PwSettingActivity.class);
			startActivity(intent);
		}
			break;
			
		default:
			break;
		}
	}
}
