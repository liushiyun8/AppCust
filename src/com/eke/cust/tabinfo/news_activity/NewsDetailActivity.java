package com.eke.cust.tabinfo.news_activity;

import android.os.Bundle;

import com.eke.cust.R;
import com.eke.cust.base.WebViewActivity;

import org.droidparts.annotation.inject.InjectBundleExtra;



public class NewsDetailActivity extends WebViewActivity {
	@InjectBundleExtra(key = "data")
	private String  link;


	@Override
	protected String getUrl() {
		return link;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("详情");
		registerLeftImageView(R.drawable.arrow_back);

	}


}
