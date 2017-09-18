package com.eke.cust.tabhouse.track_activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class HouseTrackActivity extends BaseActivity implements OnClickListener{
	private TextView mTextView_tv_house_source;
	private EditText mEditText_et_content;
	private Button mButton_btn_changyonggenjin;
	private Button mButton_btn_submit;
	private ListView mListView_listview_msg_genjin;
	
	private TextView mTextView_tv_title;
	private TextView mTextView_tv_pos;
	
	private TrackListAdapter mTrackListAdapter;
	private List<TrackNodeInfo> mTrackNodeInfos = new ArrayList<TrackNodeInfo>();
	
	private String mPropertyid = "0206031025320225BC71C2B754E67659";
	private String propertyno = "";
	private String estatename = "";
	private String roomno = "";
	private String buildno = "";
	
	private boolean mIsCanEdit = true;
	private boolean mIsNeedCheckCanInputAt = true;
	private boolean mIsSelectAtContent = false;
	
//	private String[] changyonggenjin = {"房源：状态不变", "业主：交易条件变化", "@房源不实，请后台核实", "@房源下线：特殊情况", 
//			"@房源下线：已租(售)", "@房源下线：业主原因", "@业主联系不上", "已经拿到钥匙", "业主急租(售)，大家抓紧推。", "@前条跟进：误操作", "@书面合同已签"};
	
	private String[] changyonggenjin = {"登记有误：", "房源下线?", "交易条件有变", "已经拿到钥匙", "业主急，抓紧推。", "房源状态不变", "我已签约"};
	private String[] dengjiyouwu_items = {"面积", "朝向", "价格", "业主电话", "业主称呼", "虚假房源", "业主联系不上", "楼栋/房号"};
	private String[] fangyuanxiaxian_items = {"他司成交", "业主终止"};
	private String[] jiaoyitiaojianyoubian_items = {"价格", "入住时间", "有附加条件"};
	private String content_prefix = "";
	
	private PopupWindow pw_del;
	private String del_id;
	public static View view_clicked;
	
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
							if (request_url.equals(ServerUrl.METHOD_addFollow)) {
								Toast.makeText(getApplicationContext(), "跟进提交成功!", Toast.LENGTH_SHORT).show();
								mIsSelectAtContent = false;
								mEditText_et_content.setText("");
								getPropertyFollow();
							}
							else if (request_url.equals(ServerUrl.METHOD_deleteFollow)) {
								Toast.makeText(getApplicationContext(), "删除成功!", Toast.LENGTH_SHORT).show();
								getPropertyFollow();
							}
							else if (request_url.equals(ServerUrl.METHOD_getPropertyFollow)) {
								
								JSONObject obj_property = jsonObject.optJSONObject("property");
								if (obj_property != null) {
									propertyno = obj_property.optString("propertyno", "");
									estatename = obj_property.optString("estatename", "");
									roomno = obj_property.optString("roomno", "");
									buildno = obj_property.optString("buildno", "");
									
									mTextView_tv_house_source.setText(propertyno + ":[" + estatename + "]" + buildno + roomno);
								}
								
								mTrackNodeInfos.clear();
								JSONArray array_data = jsonObject.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject obj = array_data.getJSONObject(i);
										
										TrackNodeInfo node = new TrackNodeInfo();
										node.setFollowdate(DateUtil.getDateToString(obj.optLong("followdate")).substring(5));
										node.setEmpname(obj.optString("empname"));
										node.setContent(obj.optString("content"));
										node.setFollowid(obj.optString("followid"));
										node.setEmpid(obj.optString("empid"));
										
										mTrackNodeInfos.add(node);
									}
								}
								
								mTrackListAdapter.notifyDataSetChanged();
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
	private CheckBox checkBox_zhuli,checkBox_houtai;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_house_track);
		
		initActivity();
		
		mPropertyid = getIntent().getStringExtra("propertyid");
		if (mPropertyid != null && !mPropertyid.equals("")) {
			getPropertyFollow();
		}
		else {
			Toast.makeText(getApplicationContext(), "出错!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		mTextView_tv_title.setText(getIntent().getStringExtra("propertyno"));
		mTextView_tv_pos.setText(getIntent().getStringExtra("estatename") + " " + getIntent().getStringExtra("room"));
		checkBox_zhuli = (CheckBox)findViewById(R.id.track_cb_one);
		checkBox_houtai = (CheckBox)findViewById(R.id.track_cb_two);
		checkBox_zhuli.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox_houtai.isChecked()) {
					checkBox_houtai.setChecked(false);
					checkBox_zhuli.setChecked(false);
				} else {
					checkBox_zhuli.setChecked(checkBox_zhuli.isChecked());
				}
			}
		});
		checkBox_houtai.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox_zhuli.isChecked()) {
					checkBox_zhuli.setChecked(false);
					checkBox_houtai.setChecked(false);
				} else {
					checkBox_houtai.setChecked(checkBox_houtai.isChecked());
				}
			}
		});
	}
	
	private void getPropertyFollow() {
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyid", mPropertyid);
			
			ClientHelper clientHelper = new ClientHelper(HouseTrackActivity.this,
					ServerUrl.METHOD_getPropertyFollow, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取跟进列表...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initActivity() {
		mTextView_tv_house_source = (TextView)findViewById(R.id.tv_house_source);
		mEditText_et_content = (EditText)findViewById(R.id.et_content);
		mButton_btn_changyonggenjin = (Button)findViewById(R.id.btn_changyonggenjin);
		mButton_btn_submit = (Button)findViewById(R.id.btn_submit);
		mListView_listview_msg_genjin = (ListView)findViewById(R.id.listview_msg_genjin);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mTextView_tv_pos = (TextView)findViewById(R.id.tv_pos);
		
		initTrackData();
		
		mEditText_et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String editable = mEditText_et_content.getText().toString();
				if (mIsNeedCheckCanInputAt) {
					String str = stringFilter(editable);
					if (!editable.equals(str)) {
						mEditText_et_content.setText(str);
					}
				}	
				
//				mEditText_et_content.setSelection(mEditText_et_content.length());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.toString().equals("")) {
					mIsSelectAtContent = false;
				}
			}
		});
		
		mButton_btn_changyonggenjin.setOnClickListener(this);
		mEditText_et_content.setOnClickListener(this);
		mButton_btn_submit.setOnClickListener(this);
	}

	private void initTrackData() {
		mTrackNodeInfos.clear();
		
		mTrackListAdapter = new TrackListAdapter(HouseTrackActivity.this, mTrackNodeInfos);
		mListView_listview_msg_genjin.setAdapter(mTrackListAdapter);
		mListView_listview_msg_genjin.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.findViewById(R.id.track_delete).setVisibility(View.GONE);
			}
		});
		mListView_listview_msg_genjin.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mTrackNodeInfos.get(position).getEmpid().equals(GlobalSPA.getInstance(getApplicationContext()).getStringValueForKey(GlobalSPA.KEY_EMPID))) {
					
					del_id = mTrackNodeInfos.get(position).getFollowid();
					//showDelPopupWindow(view_clicked);
					view.findViewById(R.id.track_delete).setVisibility(View.VISIBLE);
					view.findViewById(R.id.track_delete).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (del_id != null) {
								JSONObject obj = new JSONObject();
								try {
									obj.put("followid", del_id);

									ClientHelper clientHelper = new ClientHelper(HouseTrackActivity.this,
											ServerUrl.METHOD_deleteFollow, obj.toString(), mHandler);
									clientHelper.setShowProgressMessage("正在删除...");
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
				
				return true;
			}
		});
	}
	
	private void showDelPopupWindow(View parent) {
		View myView = getLayoutInflater().inflate(R.layout.layout_genjin_del, null);
		pw_del = new PopupWindow(myView, DensityUtil.dip2px(getApplicationContext(), 60), DensityUtil.dip2px(getApplicationContext(), 30), true);
		pw_del.setFocusable(false);
		pw_del.setBackgroundDrawable(new BitmapDrawable());//when need touch outside it dismiss, this is must
		pw_del.setOutsideTouchable(true);
		
		TextView tv_del = (TextView)myView.findViewById(R.id.tv_del);
		tv_del.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pw_del.dismiss();
				if (del_id != null) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("followid", del_id);
						
						ClientHelper clientHelper = new ClientHelper(HouseTrackActivity.this,
								ServerUrl.METHOD_deleteFollow, obj.toString(), mHandler);
						clientHelper.setShowProgressMessage("正在删除...");
						clientHelper.isShowProgress(true);
						clientHelper.sendPost(true);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		pw_del.showAsDropDown(parent, (int)downPointX, -DensityUtil.dip2px(getApplicationContext(), 10));
	}
	
	public static float downPointX = 0.f;
	
	public String stringFilter(String str)throws PatternSyntaxException{ 
		String substr = str;
		if (str != null && !str.equals("")) {
			if (str.substring(0, 1).equals("@")) {
				if (mIsSelectAtContent) {
					substr = str.substring(1);
				}
			}
		}
		 String regEx = "@";
		 Pattern p = Pattern.compile(regEx);
		 Matcher m = p.matcher(substr);
		 if (mIsSelectAtContent) {
			 return "@" + m.replaceAll("");
		 }
		 return m.replaceAll("");
	}

	private List<Map<String, Object>> getDataClicked() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < changyonggenjin.length; i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("action", changyonggenjin[i]);
            list.add(map);
		}
        
        return list;
	}
	
	private List<Map<String, Object>> getItemsSubClicked(int index) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] items = null;
		switch (index) {
		case 0:
			items = dengjiyouwu_items;
			break;
			
		case 1:
			items = fangyuanxiaxian_items;
			break;
			
		case 2:
			items = jiaoyitiaojianyoubian_items;
			break;

		default:
			return list;
		}
        
        for (int i = 0; i < items.length; i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("action", items[i]);
            list.add(map);
		}
        
        return list;
	}
	
	private int sub_menu_offset = 0;
	private void showChangyonggenjinDlg() {
		
		final Dialog dlg = new Dialog(HouseTrackActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(HouseTrackActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_genjin_clicked, null);
		
		final RelativeLayout rl_bg = (RelativeLayout)viewContent.findViewById(R.id.rl_bg);
		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		final ListView listview_action_sub = (ListView)viewContent.findViewById(R.id.listview_action_sub);
		
		SimpleAdapter adapter = new SimpleAdapter(HouseTrackActivity.this, getDataClicked(), R.layout.layout_tab_house_action_list_item,
                new String[]{"action"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				listview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				sub_menu_offset = ((TextView)listview.getChildAt(0).findViewById(R.id.tv_action)).getMeasuredHeight();
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
				{
					showSunItems(dlg, 0, listview_action_sub);
				}
					break;
				case 1:
				{
					showSunItems(dlg, 1, listview_action_sub);
				}
					break;
				case 2:
				{
					showSunItems(dlg, 2, listview_action_sub);
				}
					break;
				
				case 3:
				case 4:
				case 5:
				case 6:
				{
//					mIsNeedCheckCanInputAt = true;
					mEditText_et_content.setText(changyonggenjin[position]);
//					mEditText_et_content.setInputType(InputType.TYPE_CLASS_TEXT);
//					mIsCanEdit = false;
					mIsSelectAtContent = false;
					dlg.dismiss();
				}
					break;

				default:
					break;
				}
				
//				if (mIsNeedCheckCanInputAt) {
//					popupSKB();
//				}
			}
		});
		
		rl_bg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsCanEdit = false;
				dlg.dismiss();
				
				if (!mIsNeedCheckCanInputAt) {
					mIsCanEdit = true;
				}
				else {
					popupSKB();
				}
				
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(false);
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
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
//		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels;
		lp.height = dm.heightPixels;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		
		dlg.show();
	}
	
	private void showSunItems(final Dialog dlg, final int index, final ListView listview) {
		mIsNeedCheckCanInputAt = false;
		mIsSelectAtContent = true;
		listview.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listview.getLayoutParams();
		params.topMargin = sub_menu_offset * index;
		listview.setLayoutParams(params);
		
		SimpleAdapter adapter = new SimpleAdapter(HouseTrackActivity.this, getItemsSubClicked(index), R.layout.layout_tab_house_action_list_item_sub,
                new String[]{"action"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (index) {
				case 0:
				{//@登记有误：", "@房源下线?", "@交易条件有变
					mEditText_et_content.setText("@登记有误：" + dengjiyouwu_items[position]);
				}
					break;
					
				case 1:
				{
					mEditText_et_content.setText("@房源下线?" + fangyuanxiaxian_items[position]);
				}
					break;
					
				case 2:
				{
					mEditText_et_content.setText("@交易条件有变：" + jiaoyitiaojianyoubian_items[position]);
				}
					break;

				default:
					break;
				}
				mEditText_et_content.setSelection(mEditText_et_content.length());
				mIsNeedCheckCanInputAt = true;
				dlg.dismiss();
			}
		});
	}
	
	private void submitFollow() {
		String content = mEditText_et_content.getText().toString().trim();
		if (content.length() < 6) {
			Toast.makeText(getApplicationContext(), "跟进不能少于6个字!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("propertyid", mPropertyid);
			obj.put("content", content);
			
			ClientHelper clientHelper = new ClientHelper(HouseTrackActivity.this,
					ServerUrl.METHOD_addFollow, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在提交...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void popupSKB() {
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mEditText_et_content.requestFocus();
				InputMethodManager imm = (InputMethodManager)mEditText_et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEditText_et_content, 0);
			}
		}, 300);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MyLog.d("House", "onresume..");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.et_content:
//		case R.id.btn_changyonggenjin:
		{
			if (mEditText_et_content.getText().toString().trim().equals("")/* || mIsCanEdit*/) {
				showChangyonggenjinDlg();
			}
			
		}
			break;
			
		case R.id.btn_submit:
		{
			submitFollow();
		}
			break;
			
		default:
			break;
		}
	}
}
