package com.eke.cust.tabmore.cehuizhushou_activity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.eke.cust.R;

public class ChzsNode implements OnClickListener{
	private Context mContext;
	private Activity activity;
	private Handler mHandler;
	private int buildingNmaeTypeIndex = 0;
	
	private ImageView mImageView_iv_danyuanshu_down;
	private EditText mEditText_et_danyuanshu_count;
	private ImageView mImageView_iv_danyuanshu_up;
	private ImageView mImageView_iv_loucengshu_down;
	private EditText mEditText_et_loucengshu_count;
	private ImageView mImageView_iv_loucengshu_up;
	private ImageView mImageView_iv_cenghushu_down;
	private EditText mEditText_et_cenghushu_count;
	private ImageView mImageView_iv_cenghushu_up;
	private RadioGroup mRadioGroup_rg_dongming;
	private RadioButton mRadioButton_rb_shuzi_dongming;
	private RadioButton mRadioButton_rb_wenzi_dongming;
	private EditText mEditText_et_wenzi_dongming;
	
	public ChzsNode(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}
	
	public View genView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.layout_more_chzs_complete_list_item, null);
		
		mImageView_iv_danyuanshu_down = (ImageView)view.findViewById(R.id.iv_danyuanshu_down);
		mEditText_et_danyuanshu_count = (EditText)view.findViewById(R.id.et_danyuanshu_count);
		mImageView_iv_danyuanshu_up = (ImageView)view.findViewById(R.id.iv_danyuanshu_up);
		mImageView_iv_loucengshu_down = (ImageView)view.findViewById(R.id.iv_loucengshu_down);
		mEditText_et_loucengshu_count = (EditText)view.findViewById(R.id.et_loucengshu_count);
		mImageView_iv_loucengshu_up = (ImageView)view.findViewById(R.id.iv_loucengshu_up);
		mImageView_iv_cenghushu_down = (ImageView)view.findViewById(R.id.iv_cenghushu_down);
		mEditText_et_cenghushu_count = (EditText)view.findViewById(R.id.et_cenghushu_count);
		mImageView_iv_cenghushu_up = (ImageView)view.findViewById(R.id.iv_cenghushu_up);
		mRadioGroup_rg_dongming = (RadioGroup)view.findViewById(R.id.rg_dongming);
		mRadioButton_rb_shuzi_dongming = (RadioButton)view.findViewById(R.id.rb_shuzi_dongming);
		mRadioButton_rb_wenzi_dongming = (RadioButton)view.findViewById(R.id.rb_wenzi_dongming);
		mEditText_et_wenzi_dongming = (EditText)view.findViewById(R.id.et_wenzi_dongming);
		
		mImageView_iv_danyuanshu_down.setOnClickListener(this);
		mImageView_iv_danyuanshu_up.setOnClickListener(this);
		mImageView_iv_loucengshu_down.setOnClickListener(this);
		mImageView_iv_loucengshu_up.setOnClickListener(this);
		mImageView_iv_cenghushu_down.setOnClickListener(this);
		mImageView_iv_cenghushu_up.setOnClickListener(this);
		
		mRadioGroup_rg_dongming.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.rb_shuzi_dongming) {
					mEditText_et_wenzi_dongming.setVisibility(View.INVISIBLE);
					buildingNmaeTypeIndex = 0;
				}
				else {
					mEditText_et_wenzi_dongming.setVisibility(View.VISIBLE);
					buildingNmaeTypeIndex = 1;
				}
			}
		});
		
		return view;
	}

	public int getZongHushuCount() {
		int danyuanshu = Integer.valueOf(mEditText_et_danyuanshu_count.getText().toString().trim());
		int loucengshu = Integer.valueOf(mEditText_et_loucengshu_count.getText().toString().trim());
		int cenghushu = Integer.valueOf(mEditText_et_cenghushu_count.getText().toString().trim());
		return danyuanshu * loucengshu * cenghushu;
	}
	
	public String getBuildingName() {
		if (buildingNmaeTypeIndex == 0) {
			return "";
		}
		
		return mEditText_et_wenzi_dongming.getText().toString().trim();
	}
	
	public String getFloorAll() {
		return mEditText_et_loucengshu_count.getText().toString().trim();
	}
	
	public String getCounth() {
		return mEditText_et_cenghushu_count.getText().toString().trim();
	}
	
	public String getCell() {
		return mEditText_et_danyuanshu_count.getText().toString().trim();
	}
	
	private void sendUpdateNumMsg() {
		if (this.mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = CHZSActivity.MSG_UPDATE_ZONG_HUSHU;
			msg.arg1 = getZongHushuCount();
			mHandler.sendMessage(msg);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_danyuanshu_down:
		{
			String danyuanshu = mEditText_et_danyuanshu_count.getText().toString().trim();
			int danyuancount = Integer.valueOf(danyuanshu);
			if (danyuancount == 1) {
				break;
			}
			
			mEditText_et_danyuanshu_count.setText((--danyuancount) + "");
			
			sendUpdateNumMsg();
		}
			break;
			
		case R.id.iv_danyuanshu_up:
		{
			String danyuanshu = mEditText_et_danyuanshu_count.getText().toString().trim();
			int danyuancount = Integer.valueOf(danyuanshu);
			
			mEditText_et_danyuanshu_count.setText((++danyuancount) + "");
			
			sendUpdateNumMsg();
		}
			break;
			
		case R.id.iv_loucengshu_down:
		{
			String loucengshu = mEditText_et_loucengshu_count.getText().toString().trim();
			int count = Integer.valueOf(loucengshu);
			if (count == 1) {
				break;
			}
			
			mEditText_et_loucengshu_count.setText((--count) + "");
			
			sendUpdateNumMsg();
		}
			break;
			
		case R.id.iv_loucengshu_up:
		{
			String loucengshu = mEditText_et_loucengshu_count.getText().toString().trim();
			int count = Integer.valueOf(loucengshu);
			
			mEditText_et_loucengshu_count.setText((++count) + "");
			
			sendUpdateNumMsg();
		}
			break;
			
		case R.id.iv_cenghushu_down:
		{
			String cenghushu = mEditText_et_cenghushu_count.getText().toString().trim();
			int count = Integer.valueOf(cenghushu);
			if (count == 1) {
				break;
			}
			
			mEditText_et_cenghushu_count.setText((--count) + "");
			
			sendUpdateNumMsg();
		}
			break;
			
		case R.id.iv_cenghushu_up:
		{
			String cenghushu = mEditText_et_cenghushu_count.getText().toString().trim();
			int count = Integer.valueOf(cenghushu);
			
			mEditText_et_cenghushu_count.setText((++count) + "");
			
			sendUpdateNumMsg();
		}
			break;

		default:
			break;
		}
	}

}
