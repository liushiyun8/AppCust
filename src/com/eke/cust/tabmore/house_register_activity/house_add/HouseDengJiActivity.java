package com.eke.cust.tabmore.house_register_activity.house_add;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.global.nodeinfo.DistrictNodeInfo;
import com.eke.cust.model.House;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.sipandengji_activity.MapSearchFragment;
import com.eke.cust.tabhouse.sipandengji_activity.SiPanInfo;
import com.eke.cust.tabmine.profile_activity.EstateNodeInfo;
import com.eke.cust.utils.AbDateUtil;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.TransformUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;



public class HouseDengJiActivity extends BaseActivity implements OnClickListener, NotificationListener {
	private Button mButton_btn_submit;


	private List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
	private List<EstateNodeInfo> mEstatesList = new ArrayList<EstateNodeInfo>();
	private List<SiPanInfo> mSiPanInfos = new ArrayList<SiPanInfo>();


	public static final int TAB_NAME_SEARCH = 0;

	FragmentManager fragmentManager = getSupportFragmentManager();
	private CommonListAdapter<SiPanInfo> sipanHistoryAdapter;
	private MapSearchFragment mapSearchFragment;
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
													HouseDengJiActivity.this,
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
	protected View onCreateContentView() {
		return inflateContentView(R.layout.activity_house_dengji);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initActivity();

	}

	private void initActivity() {
		mapSearchFragment= new MapSearchFragment();

		fragmentManager.beginTransaction()
				.add(R.id.sipan_content, mapSearchFragment)
				.commit();

		mButton_btn_submit = (Button) findViewById(R.id.btn_submit);

		mButton_btn_submit.setOnClickListener(this);

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




	private void getTePanLiShiData() {
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(this,
				ServerUrl.METHOD_getSPHistory, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);

	}

	private void showTePanLiShiDlg() {

		final Dialog dlg = new Dialog(HouseDengJiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(HouseDengJiActivity.this);
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
				if (!TextUtils.isEmpty(siPanInfo.regdate)
						&& !siPanInfo.regdate.equals("null")) {
					timeTextView.setText(AbDateUtil.getStringByFormat(
							Long.parseLong(siPanInfo.regdate),
							"yyyy-MM-dd HH:mm:ss"));
				}

				if (!TextUtils.isEmpty(siPanInfo.estatename)
						&& !siPanInfo.estatename.equals("null")) {
					contentTextView.setText(siPanInfo.estatename);

				}
				if (!TextUtils.isEmpty(siPanInfo.propertyno)
						&& !siPanInfo.propertyno.equals("null")) {
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
		window.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels
				- DensityUtil.dip2px(HouseDengJiActivity.this, 40);
		lp.width = dm.widthPixels;
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


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_sp_history: {
			showTePanLiShiDlg();
		}
			break;



		case R.id.btn_submit: {

			String address = "";
			House house=new House();
			if (mapSearchFragment.isVisible()) {
				EditText mEdtInput = (EditText) mapSearchFragment.getView()
						.findViewById(R.id.et_content_loupan_name);
				TextView mTxtAddress = (TextView) mapSearchFragment.getView()
						.findViewById(R.id.tv_current_address);
				address = String.format(
						mEdtInput.getText().toString() + "(%s)", mTxtAddress
								.getText().toString().trim());
				house.estateid=null;
				house.opestatelat=mapSearchFragment.latitude;
				house.opestatelon=mapSearchFragment.longitude;
				house.houseName=mEdtInput.getText().toString();
				house.opestatedistrict=mTxtAddress.getText().toString();
				MyLog.d(TAG, "address=" + address);
			} else {
//				EstateNodeInfo estateNodeInfo = nameSearchFragment.mCurrentSelect;
//				if (estateNodeInfo != null) {
//					house.estateid=estateNodeInfo.getEstateid();
//
//					if(!TextUtils.isEmpty(estateNodeInfo.getEstatename())){
//						address = String.format(estateNodeInfo.getDistrictname()
//										+ estateNodeInfo.getAreaname() + "(%s)",
//								estateNodeInfo.getEstatename());
//					}else{
//						address =estateNodeInfo.getDistrictname()+ estateNodeInfo.getAreaname() ;
//					}
//
//					MyLog.d(TAG, "楼盘==" + address);
//
//				}

			}
			house.houseName=address;

			NotificationCenter.defaultCenter.postNotification(
					NotificationKey.selectLoupan, house);
			finish();
		}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}



}
