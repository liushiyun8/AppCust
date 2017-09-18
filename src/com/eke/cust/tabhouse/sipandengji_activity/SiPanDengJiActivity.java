package com.eke.cust.tabhouse.sipandengji_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.global.nodeinfo.DistrictNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabmine.profile_activity.EstateNodeInfo;
import com.eke.cust.utils.AbDateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.RegularUtil;
import com.eke.cust.utils.TransformUtil;
import com.eke.cust.widget.PagerSlidingTabStrip;
import com.eke.cust.widget.PagerSlidingTabStrip.OnTabClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;

public class SiPanDengJiActivity extends BaseActivity implements
		OnClickListener, OnTabClickListener, NotificationListener {
	private ImageView mImageView_iv_history;
	private Button mButton_btn_submit;

	private EditText mEditText_et_yezhu_phone;
	private EditText mEditText_et_dong;
	private EditText mEditText_et_fanghao;
	private EditText mEditText_et_pingfangshu;
	private EditText mEditText_et_yuezujin;
	private EditText mEditText_et_shoufu_count;
	private EditText mEditText_et_yajin_count;
	private EditText mEditText_et_shoujia;
	private EditText mEditText_et_yuefu;
	private EditText mEditText_et_shoufu;
	private EditText mEditText_et_yajin;

	// 出租或者出售
	private RadioGroup mRadioGroup_rg_zushou;
	private RelativeLayout mLayoutChZuLayout;
	private LinearLayout mLayoutChShouLayout;

	private PagerSlidingTabStrip pagerSlidingTabStrip;

	private List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
	private List<EstateNodeInfo> mEstatesList = new ArrayList<EstateNodeInfo>();
	private List<SiPanInfo> mSiPanInfos = new ArrayList<SiPanInfo>();
	private String trade = "出租";

	private String titles[] = { "名字检索", "地图查找" };

	public static final int TAB_NAME_SEARCH = 0;
	public static final int TAB_MAP_SEARCH = 1;
	public static int TAB_INDEX = TAB_NAME_SEARCH;

	private ArrayList<Fragment> mFragments;
	FragmentManager fragmentManager = getSupportFragmentManager();
	private CommonListAdapter<SiPanInfo> sipanHistoryAdapter;
	private EstateNodeInfo selectEstateNodeInfo;
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
							if (request_url
									.equals(ServerUrl.METHOD_queryDistrictByCity)) {
								mListDistrict.clear();

								JSONArray array_data = jsonObject
										.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data
												.getJSONObject(i);
										if (object != null) {
											try {
												DistrictNodeInfo districtNodeInfo = TransformUtil
														.getEntityFromJson(
																object,
																DistrictNodeInfo.class);
												if (districtNodeInfo != null) {
													mListDistrict
															.add(districtNodeInfo);
												}

											} catch (InstantiationException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}

									if (mListDistrict.size() > 0) {

										// 获取楼盘信息
										JSONObject obj = new JSONObject();
										try {
											obj.put("districtname",
													mListDistrict.get(0)
															.getDistrictname());
											obj.put("pageSize", 100000);
											obj.put("estatename", "");

											ClientHelper clientHelper = new ClientHelper(
													SiPanDengJiActivity.this,
													ServerUrl.METHOD_queryListPage,
													obj.toString(), mHandler);
											clientHelper
													.setShowProgressMessage("正在获取楼盘数据, 请稍候...");
											clientHelper.isShowProgress(true);
											clientHelper.sendPost(true);

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							} else if (request_url
									.equals(ServerUrl.METHOD_queryListPage)) {
								mEstatesList.clear();

								JSONObject obj_data = jsonObject
										.optJSONObject("data");
								if (obj_data != null) {

									JSONArray array_data = obj_data
											.optJSONArray("data");
									if (array_data != null) {
										for (int i = 0; i < array_data.length(); i++) {
											JSONObject obj = array_data
													.getJSONObject(i);
											if (obj != null) {
												try {
													EstateNodeInfo node = TransformUtil
															.getEntityFromJson(
																	obj,
																	EstateNodeInfo.class);
													if (node != null) {
														mEstatesList.add(node);
													}
												} catch (InstantiationException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												} catch (IllegalAccessException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}

									}

								}
							} else if (request_url
									.equals(ServerUrl.METHOD_addSpdjContract)) {
								Toast.makeText(getApplicationContext(), "成功!",
										Toast.LENGTH_SHORT).show();
								finish();
							} else if (request_url
									.equals(ServerUrl.METHOD_getSPHistory)) {

								// 私盘历史

								JSONArray array_data = jsonObject
										.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data
												.getJSONObject(i);
										if (object != null) {
											SiPanInfo siPanInfo = new SiPanInfo();

											siPanInfo.buildno = TransformUtil
													.getString(object,
															"buildno", "");
											siPanInfo.propertyid = TransformUtil
													.getString(object,
															"propertyid", "");
											siPanInfo.regdate = TransformUtil
													.getString(object,
															"regdate", "");
											siPanInfo.estatename = TransformUtil
													.getString(object,
															"estatename", "");
											siPanInfo.propertyno = TransformUtil
													.getString(object,
															"propertyno", "");
											if (siPanInfo != null) {
												mSiPanInfos.add(siPanInfo);
											}
										}
									}

								}

								sipanHistoryAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_tab_house_sepandengji);

		initActivity();

	}

	private void initActivity() {
		NotificationCenter.defaultCenter.addListener(
				NotificationKey.selectLoupan, this);

		mImageView_iv_history = (ImageView) findViewById(R.id.iv_sp_history);
		pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagertab);
		pagerSlidingTabStrip.setTitle(titles);
		pagerSlidingTabStrip.setTabClickListener(this);
		pagerSlidingTabStrip.onTabSelected(0);

		mFragments = new ArrayList<Fragment>(2);
		mFragments.add(new NameSearchFragment());
		mFragments.add(new MapSearchFragment());
		fragmentManager.beginTransaction()
				.add(R.id.sipan_content, mFragments.get(TAB_NAME_SEARCH))
				.commit();
		fragmentManager.beginTransaction()
				.add(R.id.sipan_content, mFragments.get(TAB_MAP_SEARCH))
				.commit();
		changeTab(0);

		mButton_btn_submit = (Button) findViewById(R.id.btn_submit);
		mEditText_et_dong = (EditText) findViewById(R.id.et_dong);
		mEditText_et_fanghao = (EditText) findViewById(R.id.et_fanghao);
		mEditText_et_pingfangshu = (EditText) findViewById(R.id.et_size);
		mEditText_et_yezhu_phone = (EditText) findViewById(R.id.et_yuezuphone);
		mEditText_et_shoujia = (EditText) findViewById(R.id.et_shoujia);
		mEditText_et_yuefu = (EditText) findViewById(R.id.et_yuefu);
		mEditText_et_shoufu = (EditText) findViewById(R.id.et_shoufu);
		mEditText_et_yajin = (EditText) findViewById(R.id.et_yajin);

		mLayoutChZuLayout = (RelativeLayout) findViewById(R.id.ll_chuzu);
		mLayoutChShouLayout = (LinearLayout) findViewById(R.id.ll_chushow);
		mRadioGroup_rg_zushou = (RadioGroup) findViewById(R.id.rg_zushou);

		mImageView_iv_history.setOnClickListener(this);

		mButton_btn_submit.setOnClickListener(this);

		mRadioGroup_rg_zushou
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == R.id.rb_chuzu) {
							mLayoutChZuLayout.setVisibility(View.VISIBLE);
							mLayoutChShouLayout.setVisibility(View.INVISIBLE);
							trade = "出租";
						} else if (checkedId == R.id.rb_chushou) {
							mLayoutChShouLayout.setVisibility(View.VISIBLE);
							mLayoutChZuLayout.setVisibility(View.INVISIBLE);
							trade = "出售";
						}
					}
				});

	}

	private List<Map<String, Object>> getSuoZaiLouPanData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mEstatesList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", mEstatesList.get(i).getEstatename());
			list.add(map);
		}

		return list;
	}

	private void changeTab(int index) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		TAB_INDEX = index;
		switch (index) {
		case TAB_NAME_SEARCH:
			transaction.hide(mFragments.get(TAB_MAP_SEARCH)).show(
					mFragments.get(TAB_NAME_SEARCH));

			break;
		case TAB_MAP_SEARCH:

			transaction.hide(mFragments.get(TAB_NAME_SEARCH)).show(
					mFragments.get(TAB_MAP_SEARCH));

			break;

		}
		transaction.commit();
	}

	private void showSuoZaiLouPanDlg() {
		final Dialog dlg = new Dialog(SiPanDengJiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(SiPanDengJiActivity.this);
		View viewContent = inflater.inflate(
				R.layout.dlg_tab_house_tepan_kuaisu_chazhao, null);

		final ListView listview = (ListView) viewContent
				.findViewById(R.id.recycleview);

		SimpleAdapter adapter = new SimpleAdapter(SiPanDengJiActivity.this,
				getSuoZaiLouPanData(),
				R.layout.layout_tab_house_tepan_kuaisu_chazhao_list_item,
				new String[] { "item" }, new int[] { R.id.tv_item });

		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				dlg.dismiss();
			}
		});

		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

				}
				return false;
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels
				- DensityUtil.dip2px(SiPanDengJiActivity.this, 40);
		window.setAttributes(lp);

		dlg.show();
	}

	private void getTePanLiShiData() {
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(this,
				ServerUrl.METHOD_getSPHistory, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);

	}

	private void showTePanLiShiDlg() {

		final Dialog dlg = new Dialog(SiPanDengJiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(SiPanDengJiActivity.this);
		View viewContent = inflater.inflate(
				R.layout.dlg_tab_house_lishi_tepan_jilu, null);
		final ListView listview = (ListView) viewContent
				.findViewById(R.id.listview_history);
		sipanHistoryAdapter = new CommonListAdapter<SiPanInfo>(this,
				R.layout.layout_tepan_lishi_list_item, mSiPanInfos) {

			@Override
			public void convert(ViewHolder holder, SiPanInfo siPanInfo,
					int position) {
				TextView timeTextView = holder.findViewById(R.id.tv_time);
				TextView bianhaoTextView = holder.findViewById(R.id.tv_bianhao);
				TextView contentTextView = holder.findViewById(R.id.tv_content);
				if (!TextUtils.isEmpty(siPanInfo.regdate)&&!siPanInfo.regdate.equals("null")) {
					timeTextView.setText(AbDateUtil.getStringByFormat(
							Long.parseLong(siPanInfo.regdate),
							"yyyy-MM-dd HH:mm:ss"));
				}

				if (!TextUtils.isEmpty(siPanInfo.estatename)&&!siPanInfo.estatename.equals("null")) {
					contentTextView.setText(siPanInfo.estatename);

				}
				if (!TextUtils.isEmpty(siPanInfo.propertyno)&&!siPanInfo.propertyno.equals("null")) {
					bianhaoTextView.setText("编号:" + siPanInfo.propertyno);

				}
			}
		};

		listview.setAdapter(sipanHistoryAdapter);
		getTePanLiShiData();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				dlg.dismiss();
			}
		});

		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

				}
				return false;
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels
				- DensityUtil.dip2px(SiPanDengJiActivity.this, 40);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);

		dlg.show();
	}

	private List<Map<String, Object>> getDataClicked() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mListDistrict.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("action", mListDistrict.get(i).getDistrictname());
			list.add(map);
		}

		return list;
	}

	private void showDistrictSelectDlg() {
		final Dialog dlg = new Dialog(SiPanDengJiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(SiPanDengJiActivity.this);
		View viewContent = inflater.inflate(
				R.layout.dlg_tab_house_source_clicked, null);

		final ListView listview = (ListView) viewContent
				.findViewById(R.id.listview_action);

		SimpleAdapter adapter = new SimpleAdapter(SiPanDengJiActivity.this,
				getDataClicked(), R.layout.layout_dailichongzhi_list_item,
				new String[] { "action" }, new int[] { R.id.tv_action });

		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				dlg.dismiss();

				JSONObject obj = new JSONObject();
				try {
					obj.put("districtname", mListDistrict.get(position)
							.getDistrictname());
					obj.put("pageSize", 100000);
					obj.put("estatename", "");

					ClientHelper clientHelper = new ClientHelper(
							SiPanDengJiActivity.this,
							ServerUrl.METHOD_queryListPage, obj.toString(),
							mHandler);
					clientHelper.setShowProgressMessage("正在获取楼盘数据, 请稍候...");
					clientHelper.isShowProgress(true);
					clientHelper.sendPost(true);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

				}
				return false;
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		// WindowManager.LayoutParams lp =window.getAttributes();
		// lp.width = dm.widthPixels*2/3;
		// window.setAttributes(lp);

		dlg.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_sp_history: {
			showTePanLiShiDlg();
		}
			break;

//		case R.id.rl_quyu_filter: {
//			showDistrictSelectDlg();
//		}
//			break;
//
//		case R.id.rl_suozailoupan_filter: {
//			showSuoZaiLouPanDlg();
//		}
//			break;

		case R.id.btn_submit: {

			String estateid = "";

			if (selectEstateNodeInfo == null) {
				Toast.makeText(getApplicationContext(), "请选择有效的楼盘!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String dong = mEditText_et_dong.getText().toString().trim();
			if (TextUtils.isEmpty(dong)) {
				Toast.makeText(getApplicationContext(), "请输入栋/座!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String fanghao = mEditText_et_fanghao.getText().toString().trim();
			if (TextUtils.isEmpty(fanghao)) {
				Toast.makeText(getApplicationContext(), "请输入房号!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String pingfangshu = mEditText_et_pingfangshu.getText().toString()
					.trim();
			if (TextUtils.isEmpty(pingfangshu)) {
				Toast.makeText(getApplicationContext(), "请填入平方数!",
						Toast.LENGTH_SHORT).show();
				return;
			}

			String phone = mEditText_et_yezhu_phone.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(getApplicationContext(), "请填入业主手机号!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!RegularUtil.isMobileNO(phone)) {
				Toast.makeText(getApplicationContext(), "请填入正确的手机号!",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (trade.equals("出租")) {
				String yuefu = mEditText_et_yuefu.getText().toString();
				if (TextUtils.isEmpty(yuefu)) {
					Toast.makeText(getApplicationContext(), "请填入月付金额!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String shoufu = mEditText_et_shoufu.getText().toString();
				if (TextUtils.isEmpty(shoufu)) {
					Toast.makeText(getApplicationContext(), "请填入首付金额!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String yajin = mEditText_et_yajin.getText().toString();
				if (TextUtils.isEmpty(yajin)) {
					Toast.makeText(getApplicationContext(), "请填入押金金额!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				String shoujia = mEditText_et_shoujia.getText().toString();
				if (TextUtils.isEmpty(shoujia)) {
					Toast.makeText(getApplicationContext(), "请填入售价!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			JSONObject obj = new JSONObject();
			try {
				String remark;

				// obj.put("districtname", district);
				// obj.put("estateid", estateid);
				obj.put("estateid", "00000000000000000000000000000000");
				// if(TAB_INDEX==TAB_NAME_SEARCH){
				// remark=selectEstateNodeInfo.getEstatename()+","+dong;
				// }else{
				//
				// }
				// obj.put("remark", estateid);
				obj.put("estatename", selectEstateNodeInfo.getEstatename());
				obj.put("tel", mEditText_et_yezhu_phone.getText().toString()
						.trim());
				obj.put("trade", trade);
				obj.put("buildroomno", mEditText_et_fanghao.getText()
						.toString().trim());
				obj.put("square", pingfangshu);
				if (trade.equals("出租")) {
					obj.put("ekerentpaymodecash", mEditText_et_yajin.getText()
							.toString().trim()
							+ "");
					obj.put("ekerentpaymodedeposit", mEditText_et_shoufu
							.getText().toString().trim()
							+ "");
					obj.put("rentprice", mEditText_et_yuefu.getText()
							.toString().trim()
							+ "");
				} else {
					obj.put("price", mEditText_et_yuezujin.getText().toString()
							.trim());
				}

				ClientHelper clientHelper = new ClientHelper(
						SiPanDengJiActivity.this,
						ServerUrl.METHOD_addSpdjContract, obj.toString(),
						mHandler);
				clientHelper.setShowProgressMessage("正在提交数据...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void onTabClick(int position) {
		// TODO Auto-generated method stub
		pagerSlidingTabStrip.onTabSelected(position);
		changeTab(position);

	}

	@Override
	public boolean onNotification(Notification notification) {
		if (notification.key.equals(NotificationKey.selectLoupan)) {
			selectEstateNodeInfo = (EstateNodeInfo) notification.object;
			return true;
		}
		return false;
	}

}
