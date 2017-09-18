package com.eke.cust.tabmine.safty_activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.TransformUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FanyongActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private EditText mEditText_et_real_name;
	private EditText mEditText_et_shenfenzheng;
	private TextView mEditText_et_fangshi;
	private EditText mEditText_et_shoukuan_zhanghao;
	private ImageView mImageView_iv_shenfenzheng_zheng;
	private TextView mTextView_tv_zheng;
	private TextView mTextView_tv_clear_zheng;
	private ImageView mImageView_iv_shenfenzheng_fan;
	private TextView mTextView_tv_fan;
	private TextView mTextView_tv_clear_fan;
	private Button mButton_btn_submit;
	
	private List<String> bankList = new ArrayList<String>();
	private ImageLoader imageLoader;
	private FanYongNodeInfo mFanYongNodeInfo = new FanYongNodeInfo();
	
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
							if (request_url.equals(ServerUrl.METHOD_getSafeCount)) {
								JSONObject data_object = jsonObject.optJSONObject("data");
								if (data_object != null) {
									try {
										mFanYongNodeInfo = TransformUtil.getEntityFromJson(
												data_object,
												FanYongNodeInfo.class);
										
										
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									bankList.clear();
									JSONArray jsonArray = data_object.optJSONArray("listbank");
									if (jsonArray != null) {
										for (int i = 0; i < jsonArray.length(); i++) {
											JSONObject object = jsonArray.getJSONObject(i);
											bankList.add(object.optString("bankname"));
										}
									}
									
									if (mFanYongNodeInfo != null) {
										mEditText_et_real_name.setText(mFanYongNodeInfo.getName());
										mEditText_et_shenfenzheng.setText(mFanYongNodeInfo.getIdcard());
										mEditText_et_fangshi.setText(mFanYongNodeInfo.getBankname());
										mEditText_et_shoukuan_zhanghao.setText(mFanYongNodeInfo.getBankaccount());
										
										String img_front = mFanYongNodeInfo.getEkeidcardfront();
										
										if (img_front != null && !img_front.equals("")) {
											mImageView_iv_shenfenzheng_zheng.setImageBitmap(BitmapUtils.stringtoBitmap(img_front));
										}
										
										String img_back = mFanYongNodeInfo.getEkeidcardback();
										if (img_back != null && !img_back.equals("")) {
											mImageView_iv_shenfenzheng_fan.setImageBitmap(BitmapUtils.stringtoBitmap(img_back));
										}
									}
								}
							}
							else if (request_url.equals(ServerUrl.METHOD_updateBankCount)) {
								Toast.makeText(getApplicationContext(), "提交成功!", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tab_mine_shenfen_verify);
		
		initActivity();
		
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(FanyongActivity.this,
				ServerUrl.METHOD_getSafeCount, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}
	
	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout)findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mEditText_et_real_name = (EditText)findViewById(R.id.et_real_name);
		mEditText_et_shenfenzheng = (EditText)findViewById(R.id.et_shenfenzheng);
		mEditText_et_fangshi = (TextView)findViewById(R.id.tv_fangshi);
		mEditText_et_shoukuan_zhanghao = (EditText)findViewById(R.id.et_shoukuan_zhanghao);
		mImageView_iv_shenfenzheng_zheng = (ImageView)findViewById(R.id.iv_shenfenzheng_zheng);
		mTextView_tv_zheng = (TextView)findViewById(R.id.tv_zheng);
		mTextView_tv_clear_zheng = (TextView)findViewById(R.id.tv_clear_zheng);
		mImageView_iv_shenfenzheng_fan = (ImageView)findViewById(R.id.iv_shenfenzheng_fan);
		mTextView_tv_fan = (TextView)findViewById(R.id.tv_fan);
		mTextView_tv_clear_fan = (TextView)findViewById(R.id.tv_clear_fan);
		mButton_btn_submit = (Button)findViewById(R.id.btn_submit);
		
		mEditText_et_fangshi.setOnClickListener(this);
		mButton_btn_submit.setOnClickListener(this);
		
		mImageView_iv_shenfenzheng_zheng.setOnClickListener(this);
		mImageView_iv_shenfenzheng_fan.setOnClickListener(this);
		
		mTextView_tv_clear_zheng.setOnClickListener(this);
		mTextView_tv_clear_fan.setOnClickListener(this);
		
		imageLoader = ImageLoader.getInstance();
	}

	private List<Map<String, Object>> getDataClicked() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        for (int i = 0; i < bankList.size(); i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("bank", bankList.get(i));
            list.add(map);
		}
        
        return list;
	}
	
	private void showBankSelectDlg() {
		
		final Dialog dlg = new Dialog(FanyongActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(FanyongActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_house_source_clicked, null);
		
		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		
		SimpleAdapter adapter = new SimpleAdapter(FanyongActivity.this, getDataClicked(), R.layout.layout_bank_select_list_item,
                new String[]{"bank"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mEditText_et_fangshi.setText(bankList.get(position));
				dlg.dismiss();
			}
		});
		
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
		FanyongActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
//		WindowManager.LayoutParams lp =window.getAttributes();
//		lp.width = dm.widthPixels*2/3;
//		window.setAttributes(lp);
		
		dlg.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String img_url = null;
			if (null != data) {
				img_url = data.getStringExtra("img_url");
			}
			
			if (img_url == null) {
				return;
			}
			
			switch (requestCode) {
			case 0:{//zheng
				imageLoader.displayImage("file://"+img_url, mImageView_iv_shenfenzheng_zheng, AppContext.mDisplayImageOptions_no_round_corner);
				mFanYongNodeInfo.setEkeidcardfront(Base64.encodeToString(getBitmapByte(img_url),Base64.DEFAULT));
				break;
			}
			case 1:{//fan
				imageLoader.displayImage("file://"+img_url, mImageView_iv_shenfenzheng_fan, AppContext.mDisplayImageOptions_no_round_corner);
				mFanYongNodeInfo.setEkeidcardback(Base64.encodeToString(getBitmapByte(img_url),Base64.DEFAULT));
				break;
			}
		}
		}	
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public byte[] getBitmapByte(String imgPath){   
		Bitmap bmp = BitmapUtils.decodeFileAsBitmap(imgPath, 1280*768);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 65) {
			baos.reset();
			options -= 2;
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		
//		byte[] data = baos.toByteArray();
		return baos.toByteArray();   
	}   


		public Bitmap getBitmapFromByte(byte[] temp){   
		   if(temp != null){   
		       Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);   
		       return bitmap;   
		   }else{   
		       return null;   
		   }   
		}  
		
	public byte[] image2byte(String path){
	    byte[] data = null;
	    FileInputStream input = null;
	    try {
	      input = new FileInputStream(new File(path));
	      ByteArrayOutputStream output = new ByteArrayOutputStream();
	      byte[] buf = new byte[1024];
	      int numBytesRead = 0;
	      while ((numBytesRead = input.read(buf)) != -1) {
	      output.write(buf, 0, numBytesRead);
	      }
	      data = output.toByteArray();
	      output.close();
	      input.close();
	    }
	    catch (FileNotFoundException ex1) {
	      ex1.printStackTrace();
	    }
	    catch (IOException ex1) {
	      ex1.printStackTrace();
	    }
	    return data;
	  }
	  //byte数组到图片
	  public void byte2image(byte[] data,String path){
	    if(data.length<3||path.equals("")) return;
	    try{
	    FileOutputStream imageOutput = new FileOutputStream(new File(path));
	    imageOutput.write(data, 0, data.length);
	    imageOutput.close();
	    System.out.println("Make Picture success,Please find image in " + path);
	    } catch(Exception ex) {
	      System.out.println("Exception: " + ex);
	      ex.printStackTrace();
	    }
	  }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_fangshi:{
			showBankSelectDlg();
		}
			
			break;
			
		case R.id.btn_submit:{
			String name = mEditText_et_real_name.getText().toString().trim();
			if (name.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入收款人姓名!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String id = mEditText_et_shenfenzheng.getText().toString().trim();
			if (id.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入身份证号码!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String bank = mEditText_et_fangshi.getText().toString().trim();
			if (bank.equals("") || bank.equals("选择方式")) {
				Toast.makeText(getApplicationContext(), "请选择方式!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String zhanghao = mEditText_et_shoukuan_zhanghao.getText().toString().trim();
			if (zhanghao.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入收款账号!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (mFanYongNodeInfo.getEkeidcardfront() == null || mFanYongNodeInfo.getEkeidcardfront().equals("")) {
				Toast.makeText(getApplicationContext(), "请选择身份证正面照片!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (mFanYongNodeInfo.getEkeidcardback() == null || mFanYongNodeInfo.getEkeidcardback().equals("")) {
				Toast.makeText(getApplicationContext(), "请选择身份证反面照片!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", name);
				obj.put("idcard", id);
				obj.put("bankname", bank);
				obj.put("bankaccount", zhanghao);
				obj.put("ekeidcardfront", mFanYongNodeInfo.getEkeidcardfront());
				obj.put("ekeidcardback", mFanYongNodeInfo.getEkeidcardback());
				
				ClientHelper clientHelper = new ClientHelper(FanyongActivity.this,
						ServerUrl.METHOD_updateBankCount, obj.toString(), mHandler);
				clientHelper.setShowProgressMessage("正在提交...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			break;
			
		case R.id.iv_shenfenzheng_zheng:{
			Intent intent = new Intent(FanyongActivity.this, LocalImagePreviewActivity.class);
			intent.putExtra("from_where", Constants.FROM_SELECT_SHENFENZHENG_ZHENG);
			startActivityForResult(intent, 0);
		}
			break;
			
		case R.id.iv_shenfenzheng_fan:{
			Intent intent = new Intent(FanyongActivity.this, LocalImagePreviewActivity.class);
			intent.putExtra("from_where", Constants.FROM_SELECT_SHENFENZHENG_FAN);
			startActivityForResult(intent, 1);
		}
			break;
			
		case R.id.tv_clear_zheng: {
			mFanYongNodeInfo.setEkeidcardfront("");
			mImageView_iv_shenfenzheng_zheng.setImageDrawable(null);
		}
			break;
			
		case R.id.tv_clear_fan: {
			mFanYongNodeInfo.setEkeidcardback("");
			mImageView_iv_shenfenzheng_fan.setImageDrawable(null);
		}
			break;
			
		default:
			break;
		}
	}
}
