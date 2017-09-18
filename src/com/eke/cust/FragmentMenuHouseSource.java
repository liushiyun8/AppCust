package com.eke.cust;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import foundation.base.fragment.BaseFragment;
import com.eke.cust.global.GlobalErrorReport;
import com.eke.cust.global.nodeinfo.PageNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.HouseFilterEstateListAdapter;
import com.eke.cust.tabhouse.HouseSourceListAdapter;
import com.eke.cust.tabhouse.HouseSourceNodeInfo;
import com.eke.cust.tabhouse.MaidianGridViewAdapter;
import com.eke.cust.tabhouse.MaidianNodeInfo;
import com.eke.cust.tabhouse.backend_info_activity.BackendInfoActivity;
import com.eke.cust.tabhouse.key_manage_activity.KeyManageActivity;
import com.eke.cust.tabhouse.sipandengji_activity.SiPanDengJiActivity;
import com.eke.cust.tabhouse.track_activity.HouseTrackActivity;
import com.eke.cust.tabhouse.upload_activity.UploadActivity;
import com.eke.cust.tabhouse.wokechengjiao_activity.WoKeChengJiaoActivity;
import com.eke.cust.tabhouse.xianshifankui_activity.XianshifankuiActivity;
import com.eke.cust.tabmine.EstateNodeInfo;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.Mutex;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.TransformUtil;
import com.eke.cust.widget.ListViewForScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMenuHouseSource extends BaseFragment implements
		OnClickListener {
	private Context mContext;
	private static final String TAG = "FragmentMenuHouseSource";
	private static final int RENT_NODE_CLICKED = 0;
	private static final int SELL_NODE_CLICKED = 1;
	private ImageView mImageView_iv_setting;
	private TextView mTextView_tv_title;
	private ImageView mImageView_iv_rent_arrow;
	private TextView mTextView_tv_rent_num;
	private PullToRefreshListView mListView_listview_main_rent;
	private ImageView mImageView_iv_sell_arrow;
	private TextView mTextView_tv_sell_num;
	private PullToRefreshListView mListView_listview_main_sell;
	private LinearLayout mLl_main;
	private LinearLayout mLl_rent;
	private LinearLayout mLl_sell;

	private ScrollView mSv_filter;
	private ImageView mImageView_iv_rent_arrow_filter;
	private TextView mTextView_tv_rent_num_filter;
	private ListViewForScrollView mListView_listview_main_rent_filter;
	private ImageView mImageView_iv_sell_arrow_filter;
	private TextView mTextView_tv_sell_num_filter;
	private ListViewForScrollView mListView_listview_main_sell_filter;

	private boolean mIsRentListOpened = true;
	private boolean mIsSellListOpened = true;
	private boolean mIsRentListOpenedFilter = true;
	private boolean mIsSellListOpenedFilter = true;

	private PageNodeInfo mPageNodeInfo_rent = new PageNodeInfo();
	private PageNodeInfo mPageNodeInfo_sell = new PageNodeInfo();

	private List<HouseSourceNodeInfo> mRentNodesList = new ArrayList<HouseSourceNodeInfo>();
	private List<HouseSourceNodeInfo> mRentNodesListFilter = new ArrayList<HouseSourceNodeInfo>();
	private List<HouseSourceNodeInfo> mSellNodesList = new ArrayList<HouseSourceNodeInfo>();
	private List<HouseSourceNodeInfo> mSellNodesListFilter = new ArrayList<HouseSourceNodeInfo>();
	private HouseSourceListAdapter mRentListAdapter;
	private HouseSourceListAdapter mSellListAdapter;
	private HouseSourceListAdapter mRentListAdapterFilter;// for collect
	private HouseSourceListAdapter mSellListAdapterFilter;// for collect
	private String mCurrentPropertyId = null;

	private int houseCollectFilterFlag = -1;

	private Mutex mMutex;

	private List<MaidianNodeInfo> mMaidianNodeInfos = new ArrayList<MaidianNodeInfo>();
	private List<MaidianNodeInfo> mMaidianSelectedNodeInfos = new ArrayList<MaidianNodeInfo>();
	private String maidian_survey = null;

	private boolean mIsShowCollectFlag;

	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);

			if (msg != null) {
				long date_now = (new Date()).getTime();

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
									.equals(ServerUrl.METHOD_getListProperty)) {
								// rent
								// mRentNodesList.clear();
								// mRentNodesListFilter.clear();
								JSONObject obj_listrent = jsonObject
										.optJSONObject("listrent");
								if (obj_listrent != null) {
									int totalRecord_rent = obj_listrent
											.optInt("totalRecords");
									mTextView_tv_rent_num.setText("出租"
											+ totalRecord_rent + "套");

									try {
										mPageNodeInfo_rent = TransformUtil
												.getEntityFromJson(
														obj_listrent,
														PageNodeInfo.class);
									} catch (java.lang.InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									// "ekesurvey": 实勘
									// "propertyno": 房源编号
									// "countf": 房
									// "countt": 厅
									// "countw": 卫
									// "county": 阳
									// "square": 平米
									// "floor": 楼层
									// "propertytype": "多层",
									// "handoverdate": 入住时间
									// "empid1":
									// "ekefeature": 房源特点

									JSONArray jsonArray_listrent_data = obj_listrent
											.optJSONArray("data");
									if (jsonArray_listrent_data != null) {
										for (int i = 0; i < jsonArray_listrent_data
												.length(); i++) {
											JSONObject obj = jsonArray_listrent_data
													.getJSONObject(i);

											MyLog.d(TAG,
													"rent "
															+ jsonArray_listrent_data
																	.length()
															+ ", "
															+ i
															+ ", "
															+ obj.optString(
																	"ekesurvey",
																	""));

											HouseSourceNodeInfo node;
											try {
												node = TransformUtil
														.getEntityFromJson(
																obj,
																HouseSourceNodeInfo.class);

												if (node != null) {

													JSONArray array_listEkefeature = obj
															.optJSONArray("listEkefeature");
													if (array_listEkefeature != null) {

														List<MaidianNodeInfo> maidianList = node
																.getEkeMaidian();
														for (int j = 0; j < array_listEkefeature
																.length(); j++) {
															JSONObject maidian = array_listEkefeature
																	.getJSONObject(j);
															if (maidian != null) {

																try {
																	MaidianNodeInfo maidianNode = TransformUtil
																			.getEntityFromJson(
																					maidian,
																					MaidianNodeInfo.class);
																	if (maidianNode != null) {
																		if (maidianList != null) {
																			maidianList
																					.add(maidianNode);
																		}
																	}
																} catch (java.lang.InstantiationException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																} catch (IllegalAccessException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																}
															}
														}
													}

													MyLog.d(TAG,
															node.getScheduledate()
																	+ " rent survery: "
																	+ node.getEkesurvey());

													if (node.getContent() != null
															&& !node.getContent()
																	.equals("null")
															&& !node.getContent()
																	.equals("")) {
														long scheduledate = node
																.getScheduledate();
														if (scheduledate
																- date_now > 0) {
															node.setScheduledate(scheduledate
																	- date_now);
														}
													}

													mMutex.lock();
													mRentNodesList.add(node);
													mRentNodesListFilter
															.add(node);
													mMutex.unlock();
												}

											} catch (java.lang.InstantiationException e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											} catch (IllegalAccessException e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											}

										}
									}
								}
								mRentListAdapter.notifyDataSetChanged();
								mRentListAdapterFilter.notifyDataSetChanged();

								// sell
								// mSellNodesList.clear();
								// mSellNodesListFilter.clear();
								JSONObject obj_listsell = jsonObject
										.optJSONObject("listsell");
								if (obj_listsell != null) {
									int totalRecord_sell = obj_listsell
											.optInt("totalRecords");
									mTextView_tv_sell_num.setText("出售"
											+ totalRecord_sell + "套");

									try {
										mPageNodeInfo_sell = TransformUtil
												.getEntityFromJson(
														obj_listsell,
														PageNodeInfo.class);
									} catch (java.lang.InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									JSONArray jsonArray_listsell_data = obj_listsell
											.optJSONArray("data");
									if (jsonArray_listsell_data != null) {
										for (int i = 0; i < jsonArray_listsell_data
												.length(); i++) {
											JSONObject obj = jsonArray_listsell_data
													.getJSONObject(i);

											Log.d(TAG,
													"sell "
															+ jsonArray_listsell_data
																	.length()
															+ ", "
															+ i
															+ ", "
															+ obj.optString(
																	"estatename",
																	""));

											HouseSourceNodeInfo node;
											try {
												node = TransformUtil
														.getEntityFromJson(
																obj,
																HouseSourceNodeInfo.class);

												if (node != null) {
													JSONArray array_listEkefeature = obj
															.optJSONArray("listEkefeature");
													if (array_listEkefeature != null) {
														List<MaidianNodeInfo> maidianList = node
																.getEkeMaidian();
														for (int j = 0; j < array_listEkefeature
																.length(); j++) {
															JSONObject maidian = array_listEkefeature
																	.getJSONObject(j);
															if (maidian != null) {
																try {
																	MaidianNodeInfo maidianNode = TransformUtil
																			.getEntityFromJson(
																					maidian,
																					MaidianNodeInfo.class);
																	if (maidianNode != null) {
																		if (maidianList != null) {
																			maidianList
																					.add(maidianNode);
																		}
																	}
																} catch (java.lang.InstantiationException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																} catch (IllegalAccessException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																}
															}
														}
													}

													MyLog.d(TAG,
															node.getEkeMaidian()
																	.size()
																	+ " sell survery: "
																	+ node.getEkesurvey());

													if (node.getContent() != null
															&& !node.getContent()
																	.equals("null")
															&& !node.getContent()
																	.equals("")) {
														long scheduledate = node
																.getScheduledate();
														if (scheduledate
																- date_now > 0) {
															node.setScheduledate(scheduledate
																	- date_now);
														}
													}

													mMutex.lock();
													mSellNodesList.add(node);
													mSellNodesListFilter
															.add(node);
													mMutex.unlock();
												}
											} catch (java.lang.InstantiationException e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											} catch (IllegalAccessException e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											}

										}
									}
								}
								mSellListAdapter.notifyDataSetChanged();
								mSellListAdapterFilter.notifyDataSetChanged();
							} else if (request_url
									.equals(ServerUrl.METHOD_getListPropertyPage)) {
								JSONObject obj_data = jsonObject
										.optJSONObject("data");
								if (obj_data != null) {
									String trade = obj_data.optString("trade");
									if (trade.equals("出租")) {
										try {
											mPageNodeInfo_rent = TransformUtil
													.getEntityFromJson(
															obj_data,
															PageNodeInfo.class);
										} catch (java.lang.InstantiationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										JSONArray jsonArray_data = obj_data
												.optJSONArray("data");
										if (jsonArray_data != null) {
											for (int i = 0; i < jsonArray_data
													.length(); i++) {
												JSONObject obj = jsonArray_data
														.getJSONObject(i);

												MyLog.d(TAG,
														trade
																+ " "
																+ jsonArray_data
																		.length()
																+ ", "
																+ i
																+ ", "
																+ obj.optString(
																		"ekesurvey",
																		""));

												HouseSourceNodeInfo node;
												try {
													node = TransformUtil
															.getEntityFromJson(
																	obj,
																	HouseSourceNodeInfo.class);
													if (node != null) {
														JSONArray array_listEkefeature = obj
																.optJSONArray("listEkefeature");
														if (array_listEkefeature != null) {
															List<MaidianNodeInfo> maidianList = node
																	.getEkeMaidian();
															for (int j = 0; j < array_listEkefeature
																	.length(); j++) {
																JSONObject maidian = array_listEkefeature
																		.getJSONObject(j);
																if (maidian != null) {
																	try {
																		MaidianNodeInfo maidianNode = TransformUtil
																				.getEntityFromJson(
																						maidian,
																						MaidianNodeInfo.class);
																		if (maidianNode != null) {
																			if (maidianList != null) {
																				maidianList
																						.add(maidianNode);
																			}
																		}
																	} catch (java.lang.InstantiationException e) {
																		// TODO
																		// Auto-generated
																		// catch
																		// block
																		e.printStackTrace();
																	} catch (IllegalAccessException e) {
																		// TODO
																		// Auto-generated
																		// catch
																		// block
																		e.printStackTrace();
																	}
																}
															}
														}

														if (node.getContent() != null
																&& !node.getContent()
																		.equals("null")
																&& !node.getContent()
																		.equals("")) {
															long scheduledate = node
																	.getScheduledate();
															if (scheduledate
																	- date_now > 0) {
																node.setScheduledate(scheduledate
																		- date_now);
															}
														}

														mMutex.lock();
														mRentNodesList
																.add(node);
														mRentNodesListFilter
																.add(node);
														mMutex.unlock();
													}

												} catch (java.lang.InstantiationException e1) {
													// TODO Auto-generated catch
													// block
													e1.printStackTrace();
												} catch (IllegalAccessException e1) {
													// TODO Auto-generated catch
													// block
													e1.printStackTrace();
												}

											}
										}

										mRentListAdapter.notifyDataSetChanged();
										mRentListAdapterFilter
												.notifyDataSetChanged();

										mListView_listview_main_rent
												.postDelayed(new Runnable() {
													@Override
													public void run() {
														mListView_listview_main_rent
																.onRefreshComplete();
													}
												}, 300);
									} else if (trade.equals("出售")) {
										try {
											mPageNodeInfo_sell = TransformUtil
													.getEntityFromJson(
															obj_data,
															PageNodeInfo.class);
										} catch (java.lang.InstantiationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										JSONArray jsonArray_data = obj_data
												.optJSONArray("data");
										if (jsonArray_data != null) {
											for (int i = 0; i < jsonArray_data
													.length(); i++) {
												JSONObject obj = jsonArray_data
														.getJSONObject(i);

												MyLog.d(TAG,
														trade
																+ " "
																+ jsonArray_data
																		.length()
																+ ", "
																+ i
																+ ", "
																+ obj.optString(
																		"ekesurvey",
																		""));

												HouseSourceNodeInfo node;
												try {
													node = TransformUtil
															.getEntityFromJson(
																	obj,
																	HouseSourceNodeInfo.class);
													if (node != null) {
														JSONArray array_listEkefeature = obj
																.optJSONArray("listEkefeature");
														if (array_listEkefeature != null) {
															List<MaidianNodeInfo> maidianList = node
																	.getEkeMaidian();
															for (int j = 0; j < array_listEkefeature
																	.length(); j++) {
																JSONObject maidian = array_listEkefeature
																		.getJSONObject(j);
																if (maidian != null) {
																	try {
																		MaidianNodeInfo maidianNode = TransformUtil
																				.getEntityFromJson(
																						maidian,
																						MaidianNodeInfo.class);
																		if (maidianNode != null) {
																			if (maidianList != null) {
																				maidianList
																						.add(maidianNode);
																			}
																		}
																	} catch (java.lang.InstantiationException e) {
																		// TODO
																		// Auto-generated
																		// catch
																		// block
																		e.printStackTrace();
																	} catch (IllegalAccessException e) {
																		// TODO
																		// Auto-generated
																		// catch
																		// block
																		e.printStackTrace();
																	}
																}
															}
														}

														if (node.getContent() != null
																&& !node.getContent()
																		.equals("null")
																&& !node.getContent()
																		.equals("")) {
															long scheduledate = node
																	.getScheduledate();
															if (scheduledate
																	- date_now > 0) {
																node.setScheduledate(scheduledate
																		- date_now);
															}
														}

														mMutex.lock();
														mSellNodesList
																.add(node);
														mSellNodesListFilter
																.add(node);
														mMutex.unlock();
													}

												} catch (java.lang.InstantiationException e1) {
													// TODO Auto-generated catch
													// block
													e1.printStackTrace();
												} catch (IllegalAccessException e1) {
													// TODO Auto-generated catch
													// block
													e1.printStackTrace();
												}

											}
										}

										mSellListAdapter.notifyDataSetChanged();
										mSellListAdapterFilter
												.notifyDataSetChanged();
										mListView_listview_main_sell
												.postDelayed(new Runnable() {
													@Override
													public void run() {
														mListView_listview_main_sell
																.onRefreshComplete();
													}
												}, 300);
									}

								}
							} else if (request_url
									.equals(ServerUrl.METHOD_getFeatureByPropertyID)) {
								JSONObject obj_data = jsonObject
										.getJSONObject("data");

								maidian_survey = obj_data.optString("survey");

								mMaidianNodeInfos.clear();
								mMaidianSelectedNodeInfos.clear();

								JSONArray jsonArray_optional = obj_data
										.optJSONArray("optional");
								if (jsonArray_optional != null) {
									for (int i = 0; i < jsonArray_optional
											.length(); i++) {
										JSONObject obj = jsonArray_optional
												.getJSONObject(i);

										MaidianNodeInfo node = new MaidianNodeInfo();
										node.setFeatureid(obj.optString(
												"featureid", ""));
										node.setDetail(obj.optString("detail",
												""));

										mMaidianNodeInfos.add(node);
									}

								}

								JSONArray jsonArray_select = obj_data
										.optJSONArray("select");
								if (jsonArray_select != null) {
									for (int i = 0; i < jsonArray_select
											.length(); i++) {
										JSONObject obj = jsonArray_select
												.getJSONObject(i);

										MaidianNodeInfo node = new MaidianNodeInfo();
										node.setFeatureid(obj.optString(
												"featureid", ""));
										node.setDetail(obj.optString("detail",
												""));

										mMaidianSelectedNodeInfos.add(node);
									}

								}

								for (int i = 0; i < mMaidianSelectedNodeInfos
										.size(); i++) {
									for (int j = 0; j < mMaidianNodeInfos
											.size(); j++) {
										if (mMaidianSelectedNodeInfos
												.get(i)
												.getFeatureid()
												.equals(mMaidianNodeInfos
														.get(j).getFeatureid())) {
											mMaidianNodeInfos.get(j)
													.setSelected(true);
											break;
										}
									}
								}

								showMaidianDlg();

							} else if (request_url
									.equals(ServerUrl.METHOD_applyGenfang)) {
								Toast.makeText(
										mContext.getApplicationContext(),
										"申请跟房成功!", Toast.LENGTH_SHORT).show();
							} else if (request_url
									.equals(ServerUrl.METHOD_getListPropertyNo)) {
								JSONObject obj = jsonObject
										.optJSONObject("data");
								if (obj != null) {

									HouseSourceNodeInfo node;
									try {
										node = TransformUtil.getEntityFromJson(
												obj, HouseSourceNodeInfo.class);
										if (node != null) {
											if (node.getPropertyid() == null
													|| node.getPropertyid()
															.equals("null")
													|| node.getPropertyid()
															.equals("")
													|| node.getTrade() == null
													|| node.getTrade().equals(
															"null")
													|| node.getTrade().equals(
															"")) {
												Toast.makeText(
														mContext.getApplicationContext(),
														"没有查询到数据!",
														Toast.LENGTH_SHORT)
														.show();
												return;
											}

											JSONArray array_listEkefeature = obj
													.optJSONArray("listEkefeature");
											if (array_listEkefeature != null) {
												List<MaidianNodeInfo> maidianList = node
														.getEkeMaidian();
												for (int j = 0; j < array_listEkefeature
														.length(); j++) {
													JSONObject maidian = array_listEkefeature
															.getJSONObject(j);
													if (maidian != null) {
														try {
															MaidianNodeInfo maidianNode = TransformUtil
																	.getEntityFromJson(
																			maidian,
																			MaidianNodeInfo.class);
															if (maidianNode != null) {
																if (maidianList != null) {
																	maidianList
																			.add(maidianNode);
																}
															}
														} catch (java.lang.InstantiationException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														} catch (IllegalAccessException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
													}
												}
											}

											if (node.getContent() != null
													&& !node.getContent()
															.equals("null")
													&& !node.getContent()
															.equals("")) {
												long scheduledate = node
														.getScheduledate();
												if (scheduledate - date_now > 0) {
													node.setScheduledate(scheduledate
															- date_now);
												}
											}

											mMutex.lock();
											if (node.getTrade().equals("出租")) {
												mRentNodesList.clear();
												mRentNodesListFilter.clear();
												mSellNodesList.clear();
												mSellNodesListFilter.clear();

												mRentNodesList.add(node);
												mRentNodesListFilter.add(node);

												mRentListAdapter
														.notifyDataSetChanged();
												mSellListAdapter
														.notifyDataSetChanged();
												mRentListAdapterFilter
														.notifyDataSetChanged();
												mSellListAdapterFilter
														.notifyDataSetChanged();

												mIsRentListOpened = true;
												mImageView_iv_rent_arrow
														.setImageResource(R.drawable.arrow_green);
												mLl_rent.setVisibility(View.VISIBLE);

												mLl_sell.setVisibility(View.GONE);

												mTextView_tv_rent_num
														.setText("出租1套");
												mTextView_tv_sell_num
														.setText("出售0套");
											} else {
												mRentNodesList.clear();
												mRentNodesListFilter.clear();
												mSellNodesList.clear();
												mSellNodesListFilter.clear();

												mSellNodesList.add(node);
												mSellNodesListFilter.add(node);

												mRentListAdapter
														.notifyDataSetChanged();
												mSellListAdapter
														.notifyDataSetChanged();
												mRentListAdapterFilter
														.notifyDataSetChanged();
												mSellListAdapterFilter
														.notifyDataSetChanged();

												mLl_rent.setVisibility(View.GONE);
												mIsSellListOpened = true;
												mImageView_iv_sell_arrow
														.setImageResource(R.drawable.arrow_green);
												mListView_listview_main_sell
														.setVisibility(View.VISIBLE);

												mTextView_tv_rent_num
														.setText("出租0套");
												mTextView_tv_sell_num
														.setText("出售1套");
											}
											mMutex.unlock();
										}

									} catch (java.lang.InstantiationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (IllegalAccessException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								}
							} else if (request_url
									.equals(ServerUrl.METHOD_updateEKEFeature)) {
								Toast.makeText(
										mContext.getApplicationContext(),
										"设置卖点成功!", Toast.LENGTH_SHORT).show();
								if (mIsShowCollectFlag) {
									getCollections();// 获取收藏
								} else {
									refreshData();
								}

							} else if (request_url
									.equals(ServerUrl.METHOD_queryPropertyCondition)) {
								mRentNodesListFilter.clear();
								mSellNodesListFilter.clear();

								JSONArray jsonArray_data = jsonObject
										.optJSONArray("data");
								if (jsonArray_data != null) {
									for (int i = 0; i < jsonArray_data.length(); i++) {
										JSONObject obj = jsonArray_data
												.getJSONObject(i);

										HouseSourceNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															obj,
															HouseSourceNodeInfo.class);
											if (node != null) {
												JSONArray array_listEkefeature = obj
														.optJSONArray("listEkefeature");
												if (array_listEkefeature != null) {
													List<MaidianNodeInfo> maidianList = node
															.getEkeMaidian();
													for (int j = 0; j < array_listEkefeature
															.length(); j++) {
														JSONObject maidian = array_listEkefeature
																.getJSONObject(j);
														if (maidian != null) {
															try {
																MaidianNodeInfo maidianNode = TransformUtil
																		.getEntityFromJson(
																				maidian,
																				MaidianNodeInfo.class);
																if (maidianNode != null) {
																	if (maidianList != null) {
																		maidianList
																				.add(maidianNode);
																	}
																}
															} catch (java.lang.InstantiationException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															} catch (IllegalAccessException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
														}
													}
												}

												if (node.getContent() != null
														&& !node.getContent()
																.equals("null")
														&& !node.getContent()
																.equals("")) {
													long scheduledate = node
															.getScheduledate();
													if (scheduledate - date_now > 0) {
														node.setScheduledate(scheduledate
																- date_now);
													}
												}

												mMutex.lock();
												if (node.getTrade()
														.equals("出售")) {
													mSellNodesListFilter
															.add(node);
												} else {
													mRentNodesListFilter
															.add(node);
												}
												mMutex.unlock();
											}

										} catch (java.lang.InstantiationException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								}

								mRentListAdapter.notifyDataSetChanged();
								mRentListAdapterFilter.notifyDataSetChanged();
								mListView_listview_main_rent.postDelayed(
										new Runnable() {
											@Override
											public void run() {
												mListView_listview_main_rent
														.onRefreshComplete();
											}
										}, 300);

								mSellListAdapter.notifyDataSetChanged();
								mSellListAdapterFilter.notifyDataSetChanged();
								mListView_listview_main_sell.postDelayed(
										new Runnable() {
											@Override
											public void run() {
												mListView_listview_main_sell
														.onRefreshComplete();
											}
										}, 300);

								mTextView_tv_rent_num.setText("出租"
										+ mRentNodesListFilter.size() + "套");
								mTextView_tv_sell_num.setText("出售"
										+ mSellNodesListFilter.size() + "套");

								if (mRentNodesListFilter.size() > 0) {
									mIsRentListOpened = true;
									mImageView_iv_rent_arrow
											.setImageResource(R.drawable.arrow_green);
									mLl_rent.setVisibility(View.VISIBLE);

									mLl_sell.setVisibility(View.GONE);
								} else if (mRentNodesListFilter.size() == 0
										&& mSellNodesListFilter.size() > 0) {
									mIsSellListOpened = true;
									mImageView_iv_sell_arrow
											.setImageResource(R.drawable.arrow_green);
									mListView_listview_main_sell
											.setVisibility(View.VISIBLE);

									mIsRentListOpened = false;
									mImageView_iv_rent_arrow
											.setImageResource(R.drawable.arrow_red);
									mLl_rent.setVisibility(View.GONE);

									mLl_sell.setVisibility(View.VISIBLE);
								} else if (mRentNodesListFilter.size() == 0
										&& mSellNodesListFilter.size() == 0) {
									mIsRentListOpened = false;
									mImageView_iv_rent_arrow
											.setImageResource(R.drawable.arrow_red);
									mLl_rent.setVisibility(View.GONE);

									mIsSellListOpened = false;
									mImageView_iv_sell_arrow
											.setImageResource(R.drawable.arrow_red);
									mListView_listview_main_sell
											.setVisibility(View.GONE);
								}

								mTextView_tv_title.setText("房源列表(部分)");
							} else if (request_url
									.equals(ServerUrl.METHOD_getMyCollectProperty)) {
								mRentNodesListFilter.clear();
								mSellNodesListFilter.clear();

								mIsShowCollectFlag = true;
								mLl_main.setVisibility(View.GONE);
								mSv_filter.setVisibility(View.VISIBLE);

								JSONArray jsonArray_listrent = jsonObject
										.optJSONArray("listrent");
								if (jsonArray_listrent != null) {
									for (int i = 0; i < jsonArray_listrent
											.length(); i++) {
										JSONObject obj = jsonArray_listrent
												.getJSONObject(i);

										HouseSourceNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															obj,
															HouseSourceNodeInfo.class);
											if (node != null) {
												JSONArray array_listEkefeature = obj
														.optJSONArray("listEkefeature");
												if (array_listEkefeature != null) {
													List<MaidianNodeInfo> maidianList = node
															.getEkeMaidian();
													for (int j = 0; j < array_listEkefeature
															.length(); j++) {
														JSONObject maidian = array_listEkefeature
																.getJSONObject(j);
														if (maidian != null) {
															try {
																MaidianNodeInfo maidianNode = TransformUtil
																		.getEntityFromJson(
																				maidian,
																				MaidianNodeInfo.class);
																if (maidianNode != null) {
																	if (maidianList != null) {
																		maidianList
																				.add(maidianNode);
																	}
																}
															} catch (java.lang.InstantiationException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															} catch (IllegalAccessException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
														}
													}
												}

												if (node.getContent() != null
														&& !node.getContent()
																.equals("null")
														&& !node.getContent()
																.equals("")) {
													long scheduledate = node
															.getScheduledate();
													if (scheduledate - date_now > 0) {
														node.setScheduledate(scheduledate
																- date_now);
													}
												}

												mRentNodesListFilter.add(node);
											}

										} catch (java.lang.InstantiationException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								}

								JSONArray jsonArray_listsell = jsonObject
										.optJSONArray("listsell");
								if (jsonArray_listsell != null) {
									for (int i = 0; i < jsonArray_listsell
											.length(); i++) {
										JSONObject obj = jsonArray_listsell
												.getJSONObject(i);

										HouseSourceNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															obj,
															HouseSourceNodeInfo.class);
											if (node != null) {
												JSONArray array_listEkefeature = obj
														.optJSONArray("listEkefeature");
												if (array_listEkefeature != null) {
													List<MaidianNodeInfo> maidianList = node
															.getEkeMaidian();
													for (int j = 0; j < array_listEkefeature
															.length(); j++) {
														JSONObject maidian = array_listEkefeature
																.getJSONObject(j);
														if (maidian != null) {
															try {
																MaidianNodeInfo maidianNode = TransformUtil
																		.getEntityFromJson(
																				maidian,
																				MaidianNodeInfo.class);
																if (maidianNode != null) {
																	if (maidianList != null) {
																		maidianList
																				.add(maidianNode);
																	}
																}
															} catch (java.lang.InstantiationException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															} catch (IllegalAccessException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
														}
													}
												}

												if (node.getContent() != null
														&& !node.getContent()
																.equals("null")
														&& !node.getContent()
																.equals("")) {
													long scheduledate = node
															.getScheduledate();
													if (scheduledate - date_now > 0) {
														node.setScheduledate(scheduledate
																- date_now);
													}
												}

												mSellNodesListFilter.add(node);
											}

										} catch (java.lang.InstantiationException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								}

								mRentListAdapterFilter.notifyDataSetChanged();
								mSellListAdapterFilter.notifyDataSetChanged();
								mRentListAdapter.notifyDataSetChanged();
								mSellListAdapter.notifyDataSetChanged();

								mTextView_tv_rent_num_filter.setText("出租"
										+ mRentNodesListFilter.size() + "套");
								mTextView_tv_sell_num_filter.setText("出售"
										+ mSellNodesListFilter.size() + "套");

								mIsRentListOpenedFilter = true;
								mIsSellListOpenedFilter = true;
								mImageView_iv_rent_arrow_filter
										.setImageResource(R.drawable.arrow_green);
								mImageView_iv_sell_arrow_filter
										.setImageResource(R.drawable.arrow_green);

								mTextView_tv_title.setText("关注房源");
							} else if (request_url
									.equals(ServerUrl.METHOD_getMyFollowProperty)) {
								mRentNodesListFilter.clear();
								mSellNodesListFilter.clear();

								mLl_main.setVisibility(View.GONE);
								mSv_filter.setVisibility(View.VISIBLE);

								JSONArray jsonArray_listrent = jsonObject
										.optJSONArray("listrent");
								if (jsonArray_listrent != null) {
									for (int i = 0; i < jsonArray_listrent
											.length(); i++) {
										JSONObject obj = jsonArray_listrent
												.getJSONObject(i);

										HouseSourceNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															obj,
															HouseSourceNodeInfo.class);
											if (node != null) {
												JSONArray array_listEkefeature = obj
														.optJSONArray("listEkefeature");
												if (array_listEkefeature != null) {
													List<MaidianNodeInfo> maidianList = node
															.getEkeMaidian();
													for (int j = 0; j < array_listEkefeature
															.length(); j++) {
														JSONObject maidian = array_listEkefeature
																.getJSONObject(j);
														if (maidian != null) {
															try {
																MaidianNodeInfo maidianNode = TransformUtil
																		.getEntityFromJson(
																				maidian,
																				MaidianNodeInfo.class);
																if (maidianNode != null) {
																	if (maidianList != null) {
																		maidianList
																				.add(maidianNode);
																	}
																}
															} catch (java.lang.InstantiationException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															} catch (IllegalAccessException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
														}
													}
												}

												if (node.getContent() != null
														&& !node.getContent()
																.equals("null")
														&& !node.getContent()
																.equals("")) {
													long scheduledate = node
															.getScheduledate();
													if (scheduledate - date_now > 0) {
														node.setScheduledate(scheduledate
																- date_now);
													}
												}

												mRentNodesListFilter.add(node);
											}

										} catch (java.lang.InstantiationException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								}

								JSONArray jsonArray_listsell = jsonObject
										.optJSONArray("listsell");
								if (jsonArray_listsell != null) {
									for (int i = 0; i < jsonArray_listsell
											.length(); i++) {
										JSONObject obj = jsonArray_listsell
												.getJSONObject(i);

										HouseSourceNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															obj,
															HouseSourceNodeInfo.class);
											if (node != null) {
												JSONArray array_listEkefeature = obj
														.optJSONArray("listEkefeature");
												if (array_listEkefeature != null) {
													List<MaidianNodeInfo> maidianList = node
															.getEkeMaidian();
													for (int j = 0; j < array_listEkefeature
															.length(); j++) {
														JSONObject maidian = array_listEkefeature
																.getJSONObject(j);
														if (maidian != null) {
															try {
																MaidianNodeInfo maidianNode = TransformUtil
																		.getEntityFromJson(
																				maidian,
																				MaidianNodeInfo.class);
																if (maidianNode != null) {
																	if (maidianList != null) {
																		maidianList
																				.add(maidianNode);
																	}
																}
															} catch (java.lang.InstantiationException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															} catch (IllegalAccessException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
														}
													}
												}

												if (node.getContent() != null
														&& !node.getContent()
																.equals("null")
														&& !node.getContent()
																.equals("")) {
													long scheduledate = node
															.getScheduledate();
													if (scheduledate - date_now > 0) {
														node.setScheduledate(scheduledate
																- date_now);
													}
												}

												mSellNodesListFilter.add(node);
											}

										} catch (java.lang.InstantiationException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								}

								mRentListAdapterFilter.notifyDataSetChanged();
								mSellListAdapterFilter.notifyDataSetChanged();
								mRentListAdapter.notifyDataSetChanged();
								mSellListAdapter.notifyDataSetChanged();

								mTextView_tv_rent_num_filter.setText("出租"
										+ mRentNodesListFilter.size() + "套");
								mTextView_tv_sell_num_filter.setText("出售"
										+ mSellNodesListFilter.size() + "套");

								mIsRentListOpenedFilter = true;
								mIsSellListOpenedFilter = true;
								mImageView_iv_rent_arrow_filter
										.setImageResource(R.drawable.arrow_green);
								mImageView_iv_sell_arrow_filter
										.setImageResource(R.drawable.arrow_green);

								mTextView_tv_title.setText("我的跟房");
							} else if (request_url
									.equals(ServerUrl.METHOD_updateEkeempCollect)) {
								Toast.makeText(
										mContext.getApplicationContext(),
										"设置成功!", Toast.LENGTH_SHORT).show();

								if (mUpdateNode != null) {
									if ((mUpdateNode.getCollectempid() != null && !mUpdateNode
											.getCollectempid().equals(""))
											|| mIsShowCollectFlag) {
										mUpdateNode.setCollectempid(null);

									} else {
										mUpdateNode.setCollectempid(GlobalSPA
												.getInstance(getActivity())
												.getStringValueForKey(
														GlobalSPA.KEY_EMPID));
									}
								}

								if (mUpdateListView == RENT_NODE_CLICKED) {
									mRentListAdapter.notifyDataSetChanged();
									mRentListAdapterFilter
											.notifyDataSetChanged();
								} else {
									mSellListAdapter.notifyDataSetChanged();
									mSellListAdapterFilter
											.notifyDataSetChanged();
								}

								if (mIsShowCollectFlag) {
									getCollections();
								}
							}
						} else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg",
									"出错!");
							Toast.makeText(mContext.getApplicationContext(),
									errorMsg, Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(mContext.getApplicationContext(),
								"请求出错!", Toast.LENGTH_SHORT).show();
					}

					break;

				case Constants.TAG_FAIL:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu_house_source, null);
		initViews(view);
		mContext = view.getContext();

		mMutex = new Mutex();

		return view;
	}

	private void refreshData() {
		mRentNodesList.clear();
		mRentNodesListFilter.clear();

		mSellNodesList.clear();
		mSellNodesListFilter.clear();

		mPageNodeInfo_rent.setCurrentPage(1);
		mPageNodeInfo_sell.setCurrentPage(1);

		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("正在加载");
		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载更多");
		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("释放开始加载");

		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("正在加载");
		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载更多");
		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("释放开始加载");

		JSONObject obj = new JSONObject();
		try {

			obj.put("currentPage", 1);
			obj.put("pageSize", 20);

			ClientHelper clientHelper = new ClientHelper(mContext,
					ServerUrl.METHOD_getListProperty, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取房源数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void refreshData_rent() {
		JSONObject obj = new JSONObject();
		try {

			obj.put("currentPage", mPageNodeInfo_rent.getCurrentPage());
			obj.put("pageSize", mPageNodeInfo_rent.getPageSize());
			obj.put("trade", "出租");

			ClientHelper clientHelper = new ClientHelper(mContext,
					ServerUrl.METHOD_getListPropertyPage, obj.toString(),
					mHandler);
			clientHelper.setShowProgressMessage("正在获取出租房源数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void refreshData_sell() {
		JSONObject obj = new JSONObject();
		try {

			obj.put("currentPage", mPageNodeInfo_sell.getCurrentPage());
			obj.put("pageSize", mPageNodeInfo_sell.getPageSize());
			obj.put("trade", "出售");

			ClientHelper clientHelper = new ClientHelper(mContext,
					ServerUrl.METHOD_getListPropertyPage, obj.toString(),
					mHandler);
			clientHelper.setShowProgressMessage("正在获取出售房源数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initViews(View view) {
		mImageView_iv_setting = (ImageView) view
				.findViewById(R.id.iv_sp_history);
		mTextView_tv_title = (TextView) view.findViewById(R.id.tv_title);
		mImageView_iv_rent_arrow = (ImageView) view
				.findViewById(R.id.iv_rent_arrow);
		mTextView_tv_rent_num = (TextView) view.findViewById(R.id.tv_rent_num);
		mListView_listview_main_rent = (PullToRefreshListView) view
				.findViewById(R.id.listview_main_rent);
		mImageView_iv_sell_arrow = (ImageView) view
				.findViewById(R.id.iv_sell_arrow);
		mTextView_tv_sell_num = (TextView) view.findViewById(R.id.tv_sell_num);
		mListView_listview_main_sell = (PullToRefreshListView) view
				.findViewById(R.id.listview_main_sell);
		mLl_main = (LinearLayout) view.findViewById(R.id.ll_main);
		mLl_rent = (LinearLayout) view.findViewById(R.id.ll_rent);
		mLl_sell = (LinearLayout) view.findViewById(R.id.ll_sell);

		mSv_filter = (ScrollView) view.findViewById(R.id.sv_filter);
		mImageView_iv_rent_arrow_filter = (ImageView) view
				.findViewById(R.id.iv_rent_arrow_filter);
		mTextView_tv_rent_num_filter = (TextView) view
				.findViewById(R.id.tv_rent_num_filter);
		mListView_listview_main_rent_filter = (ListViewForScrollView) view
				.findViewById(R.id.listview_main_rent_filter);
		mImageView_iv_sell_arrow_filter = (ImageView) view
				.findViewById(R.id.iv_sell_arrow_filter);
		mTextView_tv_sell_num_filter = (TextView) view
				.findViewById(R.id.tv_sell_num_filter);
		mListView_listview_main_sell_filter = (ListViewForScrollView) view
				.findViewById(R.id.listview_main_sell_filter);

		mListView_listview_main_rent.setMode(Mode.PULL_FROM_END);
		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("正在加载");
		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载更多");
		mListView_listview_main_rent.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("释放开始加载");

		mListView_listview_main_rent
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						if (mPageNodeInfo_rent.getCurrentPage() < mPageNodeInfo_rent
								.getTotalPages()) {
							int page = mPageNodeInfo_rent.getCurrentPage();
							mPageNodeInfo_rent.setCurrentPage(page + 1);
							refreshData_rent();
						} else {
							mListView_listview_main_rent.getLoadingLayoutProxy(
									false, true).setRefreshingLabel("没有更多数据");
							mListView_listview_main_rent.getLoadingLayoutProxy(
									false, true).setPullLabel("没有更多数据");
							mListView_listview_main_rent.getLoadingLayoutProxy(
									false, true).setReleaseLabel("没有更多数据");

							mListView_listview_main_rent.postDelayed(
									new Runnable() {
										@Override
										public void run() {
											mListView_listview_main_rent
													.onRefreshComplete();
										}
									}, 500);
						}
					}

				});

		mListView_listview_main_sell.setMode(Mode.PULL_FROM_END);
		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("正在加载");
		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载更多");
		mListView_listview_main_sell.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("释放开始加载");

		mListView_listview_main_sell
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						if (mPageNodeInfo_sell.getCurrentPage() < mPageNodeInfo_sell
								.getTotalPages()) {
							int page = mPageNodeInfo_sell.getCurrentPage();
							mPageNodeInfo_sell.setCurrentPage(page + 1);
							refreshData_sell();
						} else {
							mListView_listview_main_sell.getLoadingLayoutProxy(
									false, true).setRefreshingLabel("没有更多数据");
							mListView_listview_main_sell.getLoadingLayoutProxy(
									false, true).setPullLabel("没有更多数据");
							mListView_listview_main_sell.getLoadingLayoutProxy(
									false, true).setReleaseLabel("没有更多数据");

							mListView_listview_main_sell.postDelayed(
									new Runnable() {
										@Override
										public void run() {
											mListView_listview_main_sell
													.onRefreshComplete();
										}
									}, 500);
						}
					}

				});

		mImageView_iv_rent_arrow.setOnClickListener(this);
		mImageView_iv_sell_arrow.setOnClickListener(this);
		mImageView_iv_rent_arrow_filter.setOnClickListener(this);
		mImageView_iv_sell_arrow_filter.setOnClickListener(this);
		mImageView_iv_setting.setOnClickListener(this);

		initRentSellHouses();

		houseCollectFilterFlag = -1;
	}

	private void initRentSellHouses() {
		mRentNodesList.clear();
		mRentNodesListFilter.clear();
		// HouseSourceNodeInfo node = new HouseSourceNodeInfo();//debug
		// mRentNodesListFilter.add(node);

		mRentListAdapter = new HouseSourceListAdapter(getActivity(),
				mRentNodesListFilter, true);
		mListView_listview_main_rent.setAdapter(mRentListAdapter);

		mListView_listview_main_rent
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						showHouseSourceClickedDlg(RENT_NODE_CLICKED,
								position - 1);
					}
				});

		mRentListAdapterFilter = new HouseSourceListAdapter(getActivity(),
				mRentNodesListFilter, true);
		mRentListAdapterFilter.setForCollect();
		mListView_listview_main_rent_filter.setAdapter(mRentListAdapterFilter);

		mListView_listview_main_rent_filter
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						showHouseSourceClickedDlg(RENT_NODE_CLICKED, position);
					}
				});

		mSellNodesList.clear();
		mSellNodesListFilter.clear();
		mSellListAdapter = new HouseSourceListAdapter(getActivity(),
				mSellNodesListFilter, false);
		mListView_listview_main_sell.setAdapter(mSellListAdapter);
		mListView_listview_main_sell
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						showHouseSourceClickedDlg(SELL_NODE_CLICKED,
								position - 1);
					}
				});

		mSellListAdapterFilter = new HouseSourceListAdapter(getActivity(),
				mSellNodesListFilter, false);
		mSellListAdapterFilter.setForCollect();
		mListView_listview_main_sell_filter.setAdapter(mSellListAdapterFilter);
		mListView_listview_main_sell_filter
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						showHouseSourceClickedDlg(SELL_NODE_CLICKED, position);
					}
				});
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("action", "刷新");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "按房源编号查找");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "我客成交");
		list.add(map);

		map = new HashMap<String, Object>();
		if (houseFilterFlag == -1) {
			map.put("action", "条件过滤");
		} else {
			map.put("action", "看所有房源");
		}
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "私盘登记");
		list.add(map);

		map = new HashMap<String, Object>();
		if (houseCollectFilterFlag == -1) {
			map.put("action", "只看关注房源");
		} else {
			map.put("action", "看所有房源");
		}
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "只看我的跟房");

		list.add(map);

		return list;
	}

	private List<Map<String, Object>> getDataClicked() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("action", "申请跟房");
		list.add(map);

		/*
		 * map = new HashMap<String, Object>(); map.put("action", "合同已签");
		 * list.add(map);
		 */

		map = new HashMap<String, Object>();
		map.put("action", "跟进记录");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "房源图上传");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "卖点");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "纠错");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "后台资料");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "钥匙管理");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "反馈");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("action", "添加关注");
		list.add(map);

		return list;
	}

	private boolean mIs_shenqinggenfang_enabled = true;
	private boolean mIs_hetongyiqian_enabled = true;
	private boolean mIs_genjinjilu_enabled = true;
	private boolean mIs_shangchuan_enabled = true;
	private boolean mIs_maidian_enabled = true;
	private boolean mIs_jiucuo_enabled = true;
	private boolean mIs_houtaiziliao_enabled = true;
	private boolean mIs_yaoshiguanli_enabled = true;
	private boolean mIs_fankui_enabled = true;

	private int mUpdateListView = 0;
	// private int mUpdateListViewIndex = 0;
	private HouseSourceNodeInfo mUpdateNode;

	private void showHouseSourceClickedDlg(int rent_or_sell, int position) {
		final HouseSourceNodeInfo node;
		if (rent_or_sell == RENT_NODE_CLICKED) {
			mUpdateListView = RENT_NODE_CLICKED;

			if (mIsShowCollectFlag) {
				node = mRentNodesListFilter.get(position);
				mUpdateNode = mRentNodesListFilter.get(position);
			} else {
				node = mRentNodesList.get(position);
				mUpdateNode = mRentNodesList.get(position);
			}
		} else {
			mUpdateListView = SELL_NODE_CLICKED;
			// mUpdateListViewIndex = position;

			if (mIsShowCollectFlag) {
				node = mSellNodesListFilter.get(position);
				mUpdateNode = mSellNodesListFilter.get(position);
			} else {
				node = mSellNodesList.get(position);
				mUpdateNode = mSellNodesList.get(position);
			}

		}

		final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewContent = inflater.inflate(
				R.layout.dlg_tab_house_source_clicked, null);

		final ListView listview = (ListView) viewContent
				.findViewById(R.id.listview_action);

		SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				getDataClicked(), R.layout.layout_tab_house_action_list_item,
				new String[] { "action" }, new int[] { R.id.tv_action });

		listview.setAdapter(adapter);

		boolean is_someone_genfanging = false;
		if (node.getEmpfollowid() != null
				&& !node.getEmpfollowid().equals("null")) {
			is_someone_genfanging = true;
		}

		if (BuildConfig.DEBUG) {
			MyLog.d(TAG, GlobalSPA.getInstance(getActivity())
					.getStringValueForKey(GlobalSPA.KEY_EMPID)
					+ " <--> "
					+ node.getEmpfollowid());
		}

		if (is_someone_genfanging) {// 有跟房
			boolean is_current_user = false;// 跟房是否为当前用户
			if (node.getEmpfollowid().equals(
					GlobalSPA.getInstance(getActivity()).getStringValueForKey(
							GlobalSPA.KEY_EMPID))) {
				is_current_user = true;
			}

			if (is_current_user) {// 跟房是当前用户
				mIs_shenqinggenfang_enabled = false;
				mIs_hetongyiqian_enabled = true;
				mIs_genjinjilu_enabled = true;
				mIs_shangchuan_enabled = true;
				mIs_maidian_enabled = true;
				mIs_jiucuo_enabled = true;
				mIs_houtaiziliao_enabled = true;
				mIs_yaoshiguanli_enabled = true;
			} else {
				mIs_shenqinggenfang_enabled = false;
				mIs_hetongyiqian_enabled = true;
				mIs_genjinjilu_enabled = true;
				mIs_shangchuan_enabled = true;
				mIs_maidian_enabled = false;
				mIs_jiucuo_enabled = true;
				mIs_houtaiziliao_enabled = true;
				mIs_yaoshiguanli_enabled = true;
			}
		} else {
			mIs_shenqinggenfang_enabled = true;
			mIs_hetongyiqian_enabled = false;
			mIs_genjinjilu_enabled = true;
			mIs_shangchuan_enabled = true;
			mIs_maidian_enabled = false;
			mIs_jiucuo_enabled = true;
			mIs_houtaiziliao_enabled = false;
			mIs_yaoshiguanli_enabled = false;
		}

		if (node.getScheduledate() == 0) {
			mIs_fankui_enabled = false;
		} else {
			mIs_fankui_enabled = true;
		}

		listview.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						listview.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);

						if (!mIs_shenqinggenfang_enabled/*
														 * ||
														 * (node.getIsFollowApply
														 * () != null &&
														 * node.getIsFollowApply
														 * ().equals("1"))
														 */) {
							listview.getChildAt(0).setClickable(false);
							((TextView) listview.getChildAt(0).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						/*
						 * if (!mIs_hetongyiqian_enabled) {
						 * listview.getChildAt(1).setClickable(false);
						 * ((TextView
						 * )listview.getChildAt(1).findViewById(R.id.tv_action
						 * )).setTextColor(0xff8b8b8b); }
						 */

						if (!mIs_genjinjilu_enabled) {
							listview.getChildAt(1).setClickable(false);
							((TextView) listview.getChildAt(1).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_shangchuan_enabled) {
							listview.getChildAt(2).setClickable(false);
							((TextView) listview.getChildAt(2).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_maidian_enabled) {
							listview.getChildAt(3).setClickable(false);
							((TextView) listview.getChildAt(3).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_jiucuo_enabled) {
							listview.getChildAt(4).setClickable(false);
							((TextView) listview.getChildAt(4).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_houtaiziliao_enabled) {
							listview.getChildAt(5).setClickable(false);
							((TextView) listview.getChildAt(5).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_yaoshiguanli_enabled) {
							listview.getChildAt(6).setClickable(false);
							((TextView) listview.getChildAt(6).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if (!mIs_fankui_enabled) {
							listview.getChildAt(7).setClickable(false);
							((TextView) listview.getChildAt(7).findViewById(
									R.id.tv_action)).setTextColor(0xff8b8b8b);
						}

						if ((node.getCollectempid() != null && !node
								.getCollectempid().equals(""))
								|| mIsShowCollectFlag) {
							((TextView) listview.getChildAt(8).findViewById(
									R.id.tv_action)).setText("取消关注");
						} else {
							((TextView) listview.getChildAt(8).findViewById(
									R.id.tv_action)).setText("添加关注");
						}
					}
				});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:// 申请跟房
				{
					if (!mIs_shenqinggenfang_enabled/*
													 * ||
													 * (node.getIsFollowApply()
													 * != null &&
													 * node.getIsFollowApply
													 * ().equals("1"))
													 */) {
						break;
					}

					dlg.dismiss();

					if (node != null) {
						JSONObject obj = new JSONObject();
						try {
							obj.put("propertyid", node.getPropertyid());

							ClientHelper clientHelper = new ClientHelper(
									mContext, ServerUrl.METHOD_applyGenfang,
									obj.toString(), mHandler);
							clientHelper.setShowProgressMessage("正在申请跟房...");
							clientHelper.isShowProgress(true);
							clientHelper.sendPost(true);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
					break;

				case 1:// 跟进记录
				{
					if (!mIs_genjinjilu_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						Intent intent = new Intent(getActivity(),
								HouseTrackActivity.class);
						intent.putExtra("propertyid", node.getPropertyid());
						intent.putExtra("foreignId", node.getPropertyid());
						intent.putExtra("type", "property");
						intent.putExtra("propertyno", node.getPropertyno());
						intent.putExtra("estatename", node.getEstatename());
						intent.putExtra("room",
								node.getBuildno() + node.getRoomno());
						startActivity(intent);
					}

				}
					break;

				case 2:// 房源图上传
				{
					if (!mIs_shangchuan_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						Intent intent = new Intent(getActivity(),
								UploadActivity.class);
						intent.putExtra("propertyid", node.getPropertyid());
						intent.putExtra("estateid", node.getEstateid());
						intent.putExtra(
								"estateinfo",
								"【" + node.getEstatename() + "】"
										+ node.getBuildno() + node.getRoomno());
						intent.putExtra("foreignId", node.getPropertyid());
						intent.putExtra("type", "property");
						startActivity(intent);
					}

				}
					break;

				case 3:// 卖点
				{
					if (!mIs_maidian_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						JSONObject obj = new JSONObject();
						try {
							mCurrentPropertyId = node.getPropertyid();
							obj.put("propertyid", node.getPropertyid());

							ClientHelper clientHelper = new ClientHelper(
									mContext,
									ServerUrl.METHOD_getFeatureByPropertyID,
									obj.toString(), mHandler);
							clientHelper.setShowProgressMessage("正在获取卖点...");
							clientHelper.isShowProgress(true);
							clientHelper.sendPost(true);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
					break;

				case 4:// 纠错
				{
					if (!mIs_jiucuo_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						// Intent intent = new Intent(getActivity(),
						// ErrorReportActivity.class);
						// intent.putExtra("propertyid", node.getPropertyid());
						// startActivity(intent);
						new GlobalErrorReport(getActivity(), getActivity(),
								mHandler, node.getPropertyid(),
								GlobalErrorReport.ERROR_TYPE_PROPERTY)
								.showGlobalErrorReportDlg(GlobalErrorReport.ERROR_TYPE_PROPERTY);
					}

				}
					break;

				case 5:// 后台资料
				{
					if (!mIs_houtaiziliao_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						Intent intent = new Intent(getActivity(),
								BackendInfoActivity.class);
						intent.putExtra("propertyid", node.getPropertyid());
						startActivity(intent);
					}

				}
					break;

				case 6:// 钥匙管理
				{
					if (!mIs_yaoshiguanli_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						Intent intent = new Intent(getActivity(),
								KeyManageActivity.class);
						intent.putExtra("propertyid", node.getPropertyid());
						intent.putExtra("manageid", node.getEmpfollowid());
						intent.putExtra("propertyno", node.getPropertyno());
						intent.putExtra("estatename", node.getEstatename());
						intent.putExtra("room",
								node.getBuildno() + node.getRoomno());
						startActivity(intent);
					}

				}
					break;

				case 7:// 反馈
				{
					if (!mIs_fankui_enabled) {
						break;
					}

					dlg.dismiss();
					if (node != null) {
						Intent intent = new Intent(getActivity(),
								XianshifankuiActivity.class);
						intent.putExtra("propertyid", node.getPropertyid());
						intent.putExtra("house", node.getEstatename() + " "
								+ node.getBuildno() + node.getRoomno());
						startActivity(intent);
					}
				}
					break;

				case 8:// 关注
				{
					dlg.dismiss();

					if (node != null) {
						JSONObject obj = new JSONObject();
						try {
							mCurrentPropertyId = node.getPropertyid();
							obj.put("propertyid", node.getPropertyid());

							ClientHelper clientHelper = new ClientHelper(
									mContext,
									ServerUrl.METHOD_updateEkeempCollect, obj
											.toString(), mHandler);
							if ((node.getCollectempid() != null && !node
									.getCollectempid().equals(""))
									|| mIsShowCollectFlag) {
								clientHelper
										.setShowProgressMessage("正在取消关注...");
							} else {
								clientHelper
										.setShowProgressMessage("正在添加关注...");
							}

							clientHelper.isShowProgress(true);
							clientHelper.sendPost(true);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
					break;

				default:
					break;
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
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		// WindowManager.LayoutParams lp =window.getAttributes();
		// lp.width = dm.widthPixels*2/3;
		// window.setAttributes(lp);

		dlg.show();
	}

	private void showActionDlg() {
		final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewContent = inflater
				.inflate(R.layout.dlg_tab_house_action, null);

		final RelativeLayout rl_page = (RelativeLayout) viewContent
				.findViewById(R.id.rl_page);
		final ListView listview = (ListView) viewContent
				.findViewById(R.id.listview_action);

		rl_page.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(),
				R.layout.layout_tab_house_action_list_item,
				new String[] { "action" }, new int[] { R.id.tv_action });

		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dlg.dismiss();

				switch (position) {
				case 0:// 刷新
				{
					refreshData();
				}
					break;

				case 1:// 房源搜索
				{
					// Intent intent = new Intent(getActivity(),
					// HouseSearchActivity.class);
					// startActivity(intent);
					// dlg.dismiss();

					showHouseSourceSearchDlg();

				}
					break;

				case 2:// 我客成交
				{
					Intent intent = new Intent(getActivity(),
							WoKeChengJiaoActivity.class);
					startActivity(intent);
					dlg.dismiss();
				}
					break;

				case 3:// 过滤
				{
					if (houseFilterFlag == -1) {
						showHouseFilterDlg();
					} else {
						houseFilterFlag = -1;

						mRentNodesListFilter.clear();
						for (int j = 0; j < mRentNodesList.size(); j++) {
							mRentNodesListFilter.add(mRentNodesList.get(j));
						}
						mRentListAdapter.notifyDataSetChanged();
						mRentListAdapterFilter.notifyDataSetChanged();
						mTextView_tv_rent_num.setText("出租"
								+ mRentNodesListFilter.size() + "套");

						mSellNodesListFilter.clear();
						for (int j = 0; j < mSellNodesList.size(); j++) {
							mSellNodesListFilter.add(mSellNodesList.get(j));
						}
						mSellListAdapter.notifyDataSetChanged();
						mSellListAdapterFilter.notifyDataSetChanged();
						mTextView_tv_sell_num.setText("出售"
								+ mSellNodesListFilter.size() + "套");

						mTextView_tv_title.setText("房源列表");
					}

				}
					break;

				case 4:// 私盘登记
				{
					Intent intent = new Intent(getActivity(),
							SiPanDengJiActivity.class);
					startActivity(intent);
					dlg.dismiss();
				}
					break;

				case 5:// 关注房源
				{
					if (houseCollectFilterFlag == -1) {
						houseCollectFilterFlag = 0;
						getCollections();

					} else {
						houseCollectFilterFlag = -1;
						mIsShowCollectFlag = false;
						mLl_main.setVisibility(View.VISIBLE);
						mSv_filter.setVisibility(View.GONE);

						mRentNodesListFilter.clear();
						for (int j = 0; j < mRentNodesList.size(); j++) {
							mRentNodesListFilter.add(mRentNodesList.get(j));
						}
						mRentListAdapter.notifyDataSetChanged();
						mRentListAdapterFilter.notifyDataSetChanged();
						mTextView_tv_rent_num.setText("出租"
								+ mRentNodesListFilter.size() + "套");

						mSellNodesListFilter.clear();
						for (int j = 0; j < mSellNodesList.size(); j++) {
							mSellNodesListFilter.add(mSellNodesList.get(j));
						}
						mSellListAdapter.notifyDataSetChanged();
						mSellListAdapterFilter.notifyDataSetChanged();
						mTextView_tv_sell_num.setText("出售"
								+ mSellNodesListFilter.size() + "套");

						mTextView_tv_title.setText("房源列表");
					}
				}
					break;
				case 6:// 只看我的跟房
						// FIXME 我的跟房,接口地址有问题
					getMyFollowProperty();
					break;

				default:
					break;
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
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.RIGHT | Gravity.TOP);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels;
		lp.height = dm.heightPixels;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);

		dlg.show();
	}

	private void getCollections() {
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(getActivity(),
				ServerUrl.METHOD_getMyCollectProperty, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}

	private void getMyFollowProperty() {
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(getActivity(),
				ServerUrl.METHOD_getMyFollowProperty, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
	}

	private int getMaidianSelectedCount() {
		int count = 0;
		for (int i = 0; i < mMaidianNodeInfos.size(); i++) {
			if (mMaidianNodeInfos.get(i).isSelected()) {
				count++;
			}
		}

		return count;
	}

	private int getMaidianCanSelectedCount() {
		if (mMaidianNodeInfos.size() >= 4) {
			return 4;
		}

		return mMaidianNodeInfos.size();
	}

	private void showMaidianDlg() {
		final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewContent = inflater.inflate(R.layout.dlg_tab_house_maidian,
				null);

		final Button btn_confirm = (Button) viewContent
				.findViewById(R.id.btn_confirm);

		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();

				if (mCurrentPropertyId != null) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("propertyid", mCurrentPropertyId);

						StringBuilder sbBuilder = new StringBuilder();
						for (MaidianNodeInfo node : mMaidianNodeInfos) {
							if (node.isSelected()) {
								sbBuilder.append(node.getFeatureid()).append(
										"/");
							}
						}
						if (sbBuilder.toString().length() > 0) {
							sbBuilder.deleteCharAt(sbBuilder.length() - 1);
						}

						obj.put("ekefeature", sbBuilder.toString());

						ClientHelper clientHelper = new ClientHelper(mContext,
								ServerUrl.METHOD_updateEKEFeature, obj
										.toString(), mHandler);
						clientHelper.setShowProgressMessage("正在提交...");
						clientHelper.isShowProgress(true);
						clientHelper.sendPost(true);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		final GridView gridview = (GridView) viewContent
				.findViewById(R.id.grid_view_maidian);

		final MaidianGridViewAdapter adapter = new MaidianGridViewAdapter(
				getActivity(), mMaidianNodeInfos);
		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MaidianNodeInfo node = mMaidianNodeInfos.get(position);

				if (!node.isSelected()) {
					if (getMaidianSelectedCount() >= 4) {
						return;
					}
				}

				node.setSelected(!node.isSelected());
				adapter.notifyDataSetChanged();
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
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 30);
		lp.height = dm.heightPixels - DensityUtil.dip2px(getActivity(), 100);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}

	private void showHouseSourceSearchDlg() {
		final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewContent = inflater.inflate(
				R.layout.dlg_tab_house_source_search, null);

		final EditText et_house = (EditText) viewContent
				.findViewById(R.id.et_house_id);
		final Button btn_confirm = (Button) viewContent
				.findViewById(R.id.btn_confirm);

		et_house.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = s.toString();
				if (str.length() == 8) {
					btn_confirm.setBackground(getResources().getDrawable(
							R.drawable.bg_house_search_btn));
				} else {
					btn_confirm.setBackground(getResources().getDrawable(
							R.drawable.bg_house_search_btn_disable));
				}
			}
		});

		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = et_house.getText().toString().trim();
				if (str.length() == 8) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("propertyno", str);

						ClientHelper clientHelper = new ClientHelper(
								getActivity(),
								ServerUrl.METHOD_getListPropertyNo, obj
										.toString(), mHandler);
						clientHelper.setShowProgressMessage("正在获取数据...");
						clientHelper.isShowProgress(true);
						clientHelper.sendPost(true);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					dlg.dismiss();
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
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 120);
		lp.height = DensityUtil.dip2px(getActivity(), 120);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);

		dlg.show();
	}

	private List<Map<String, Object>> getUserEstateFilterData() {
		List<Map<String, Object>> listEstate = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < mUserDaiLiEstatesFilter.size(); j++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("estate", mUserDaiLiEstatesFilter.get(j).getEstatename());
			listEstate.add(map);
		}
		return listEstate;
	}

	private List<EstateNodeInfo> mUserDaiLiEstatesFilter = new ArrayList<EstateNodeInfo>();
	private int houseFilterFlag = -1;// 0-all, 1-rent, 2-sell

	private void showHouseFilterDlg() {
		houseFilterFlag = 0;
		final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewContent = inflater
				.inflate(R.layout.dlg_tab_house_filter, null);

		final List<EstateNodeInfo> mUserDaiLiEstates = AppContext.getInstance()
				.getUserDaiLiEstates();
		mUserDaiLiEstatesFilter.clear();
		for (EstateNodeInfo node : mUserDaiLiEstates) {
			node.setSelected(false);
			mUserDaiLiEstatesFilter.add(node);
		}

		final ListView listview = (ListView) viewContent
				.findViewById(R.id.recycleview);
		final HouseFilterEstateListAdapter adapter = new HouseFilterEstateListAdapter(
				getActivity(), mUserDaiLiEstatesFilter);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mUserDaiLiEstatesFilter.get(position).isSelected()) {
					return;
				}

				for (int i = 0; i < mUserDaiLiEstatesFilter.size(); i++) {
					if (i == position) {
						mUserDaiLiEstatesFilter.get(i).setSelected(true);
					} else {
						mUserDaiLiEstatesFilter.get(i).setSelected(false);
					}
				}

				adapter.notifyDataSetChanged();
			}
		});

		EditText et_filter = (EditText) viewContent
				.findViewById(R.id.et_filter);
		et_filter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = s.toString();

				mUserDaiLiEstatesFilter.clear();

				if (str.equals("")) {
					for (EstateNodeInfo node : mUserDaiLiEstates) {
						mUserDaiLiEstatesFilter.add(node);
					}
				} else {
					for (EstateNodeInfo node : mUserDaiLiEstates) {
						if (node.getEstatename().contains(str)) {
							mUserDaiLiEstatesFilter.add(node);
						}
					}
				}

				adapter.notifyDataSetChanged();
			}
		});

		RadioGroup rg_filter = (RadioGroup) viewContent
				.findViewById(R.id.rg_filter);
		rg_filter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.rb_all) {
					houseFilterFlag = 0;
				} else if (checkedId == R.id.rb_rent) {
					houseFilterFlag = 1;
				} else if (checkedId == R.id.rb_sell) {
					houseFilterFlag = 2;
				}
			}
		});

		MyLog.d(TAG, "houseFilterFlag = " + houseFilterFlag);
		Button btn = (Button) viewContent.findViewById(R.id.btn_submit);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String estateid = null;
				for (int i = 0; i < mUserDaiLiEstatesFilter.size(); i++) {
					if (mUserDaiLiEstatesFilter.get(i).isSelected()) {
						estateid = mUserDaiLiEstatesFilter.get(i).getEstateid();
						break;
					}
				}

				if (estateid == null) {
					Toast.makeText(mContext.getApplicationContext(), "请选择楼盘!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String trade = "";
				if (houseFilterFlag == 0) {// all
					trade = "";
				} else if (houseFilterFlag == 1) {// rent
					trade = "出租";

					// mTextView_tv_title.setText("房源列表(部分)");
				} else if (houseFilterFlag == 2) {// sell
					trade = "出售";
				}

				dlg.dismiss();

				menuActionHouseFilter(estateid, trade);
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
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(getActivity(), 80);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);

		dlg.show();
	}

	private void menuActionHouseFilter(String estateid, String trade) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("estateid", estateid);
			obj.put("trade", trade);

			ClientHelper clientHelper = new ClientHelper(getActivity(),
					ServerUrl.METHOD_queryPropertyCondition, obj.toString(),
					mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Runnable runnable_update_time = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mMutex.lock();
			for (HouseSourceNodeInfo node : mRentNodesList) {
				if (node.getContent() != null
						&& !node.getContent().equals("null")
						&& !node.getContent().equals("")) {
					if (node.getScheduledate() - 1000 > 0) {
						node.setScheduledate(node.getScheduledate() - 1000);
					} else {
						node.setScheduledate(0);
					}
				}
			}
			mRentListAdapter.notifyDataSetChanged();
			mRentListAdapterFilter.notifyDataSetChanged();

			for (HouseSourceNodeInfo node : mSellNodesList) {
				if (node.getContent() != null
						&& !node.getContent().equals("null")
						&& !node.getContent().equals("")) {
					if (node.getScheduledate() - 1000 > 0) {
						node.setScheduledate(node.getScheduledate() - 1000);
					} else {
						node.setScheduledate(0);
					}
				}
			}
			mSellListAdapter.notifyDataSetChanged();
			mSellListAdapterFilter.notifyDataSetChanged();
			mMutex.unlock();

			mHandler.postDelayed(this, 1000);
		}
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		mHandler.removeCallbacks(runnable_update_time);
	}

	private boolean mIsNeedUpdate = false;

	public void setNeedUpdaet() {
		mIsNeedUpdate = true;
	}

	private void dataInit() {
		refreshData();
		mHandler.postDelayed(runnable_update_time, 1000);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mIsNeedUpdate) {
			mIsNeedUpdate = false;
			dataInit();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_rent_arrow: {
			if (mIsRentListOpened) {
				mIsRentListOpened = false;
				mImageView_iv_rent_arrow.setImageResource(R.drawable.arrow_red);
				mLl_rent.setVisibility(View.GONE);

				mLl_sell.setVisibility(View.VISIBLE);
			} else {
				mIsRentListOpened = true;
				mImageView_iv_rent_arrow
						.setImageResource(R.drawable.arrow_green);
				mLl_rent.setVisibility(View.VISIBLE);

				mLl_sell.setVisibility(View.GONE);
			}
		}
			break;

		case R.id.iv_sell_arrow: {
			if (mIsSellListOpened) {
				mIsSellListOpened = false;
				mImageView_iv_sell_arrow.setImageResource(R.drawable.arrow_red);
				mListView_listview_main_sell.setVisibility(View.GONE);

			} else {
				mIsSellListOpened = true;
				mImageView_iv_sell_arrow
						.setImageResource(R.drawable.arrow_green);
				mListView_listview_main_sell.setVisibility(View.VISIBLE);

			}
		}
			break;

		case R.id.iv_rent_arrow_filter: {
			if (mIsRentListOpenedFilter) {
				mIsRentListOpenedFilter = false;
				mImageView_iv_rent_arrow_filter
						.setImageResource(R.drawable.arrow_red);
				mListView_listview_main_rent_filter.setVisibility(View.GONE);
			} else {
				mIsRentListOpenedFilter = true;
				mImageView_iv_rent_arrow_filter
						.setImageResource(R.drawable.arrow_green);
				mListView_listview_main_rent_filter.setVisibility(View.VISIBLE);
			}
		}
			break;

		case R.id.iv_sell_arrow_filter: {
			if (mIsSellListOpenedFilter) {
				mIsSellListOpenedFilter = false;
				mImageView_iv_sell_arrow_filter
						.setImageResource(R.drawable.arrow_red);
				mListView_listview_main_sell_filter.setVisibility(View.GONE);

			} else {
				mIsSellListOpenedFilter = true;
				mImageView_iv_sell_arrow_filter
						.setImageResource(R.drawable.arrow_green);
				mListView_listview_main_sell_filter.setVisibility(View.VISIBLE);
			}
		}
			break;

		case R.id.iv_sp_history: {
			showActionDlg();
		}
			break;

		default:
			break;
		}
	}

	public static void setListViewHeightBasedOnChildren(
			PullToRefreshListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		final ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		// + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);

	}

	public void updateView() {
		mRentListAdapter.notifyDataSetChanged();
		mSellListAdapter.notifyDataSetChanged();
		mRentListAdapterFilter.notifyDataSetChanged();
		mSellListAdapterFilter.notifyDataSetChanged();

		AppContext.mHouseSourceNodeSetFengMian = null;
	}
}