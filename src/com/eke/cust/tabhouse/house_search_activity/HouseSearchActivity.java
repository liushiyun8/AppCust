package com.eke.cust.tabhouse.house_search_activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class HouseSearchActivity extends BaseActivity implements OnClickListener{
	private Button mButton_btn_search;
	private EditText mEditText_et_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_house_search);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mButton_btn_search = (Button)findViewById(R.id.btn_search);
		mEditText_et_content = (EditText)findViewById(R.id.et_content);
				
		mButton_btn_search.setOnClickListener(this);
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
