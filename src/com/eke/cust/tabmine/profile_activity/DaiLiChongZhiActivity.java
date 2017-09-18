package com.eke.cust.tabmine.profile_activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.nodeinfo.PageNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.TransformUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DaiLiChongZhiActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "DaiLiChongZhiActivity";
	private RelativeLayout mRelativeLayout_rl_header;
	private ImageView mImageView_iv_back;
	private TextView mTextView_tv_title;
	private Button mButton_btn_submit;
	private RelativeLayout mRelativeLayout_rl_daili_top;
	private TextView mTextView_tv_daili_num;
	private TextView mTextView_tv_daili_can_not_find;
	private TextView mTextView_tv_daili_select_district;
	private EditText mEditText_et_content_find;
	private PullToRefreshListView mListView_listview_loupan;
	private PageNodeInfo mEstatePageNodeInfo = new PageNodeInfo();
	private DaiLiLouPanSelectEstateListAdapter mDaiLiLouPanEstateListAdapter;

//	public List<EstateNodeInfo> mFilteredEstates = new ArrayList<EstateNodeInfo>();
	
	private ImageView mImageView_iv_setting;
	private PullToRefreshListView mListView_lv_daililoupan;	
	
	private List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
	private List<EstateNodeInfo> mAllEstates = new ArrayList<EstateNodeInfo>();
	private List<EstateNodeInfo> mDailiEstates = new ArrayList<EstateNodeInfo>();
	private List<EstateNodeInfo> mSelectedEstates = new ArrayList<EstateNodeInfo>();
	private List<Boolean> mSelectedmarked= new ArrayList<Boolean>();
	private String mCityName = "深圳";
	private String mDistrictname = "";
	private TextView mTextView_quxian,mTextView_pianqu,mTextView_loupan;
	
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
						if(BuildConfig.DEBUG) {
							Log.d(TAG, "result: " + resp);
						}
						if (result.equals(Constants.RESULT_SUCCESS)) {
							if (request_url.equals(ServerUrl.METHOD_initAgent)) {
								mListDistrict.clear();
								
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								JSONArray array_listDistrict = obj_data.optJSONArray("listDistrict");
								if (array_listDistrict != null) {
									for (int i = 0; i < array_listDistrict.length(); i++) {
										JSONObject object = array_listDistrict.optJSONObject(i);
										if (object != null) {
											try {
												DistrictNodeInfo districtNodeInfo = TransformUtil
																				.getEntityFromJson(
																					object,
																					DistrictNodeInfo.class);
												if (districtNodeInfo != null) {
													mListDistrict.add(districtNodeInfo);
												}
												
											} catch (InstantiationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
								
								mAllEstates.clear();
								JSONObject object_allEstatePage = obj_data.optJSONObject("allEstatePage");
								if (object_allEstatePage != null) {
									try {
										mEstatePageNodeInfo = TransformUtil
												.getEntityFromJson(
														object_allEstatePage,
														PageNodeInfo.class);
										
										JSONArray array_data_allEstatePage = object_allEstatePage.optJSONArray("data");
										if (array_data_allEstatePage != null) {
											for (int i = 0; i < array_data_allEstatePage.length(); i++) {
												JSONObject object = array_data_allEstatePage.getJSONObject(i);
												if (object != null) {
													EstateNodeInfo node = TransformUtil
															.getEntityFromJson(
																	object,
																	EstateNodeInfo.class);
													if (node != null) {
														mAllEstates.add(node);
													}
												}
											}
										}
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
								mSelectedEstates.clear();
								mSelectedmarked.clear();
								JSONArray array_listSelectEstate = obj_data.optJSONArray("listSelectEstate");
								if (array_listSelectEstate != null) {
									for (int i = 0; i < array_listSelectEstate.length(); i++) {
										JSONObject object = array_listSelectEstate.getJSONObject(i);
										if (object != null) {
											try {
												EstateNodeInfo node = TransformUtil
																				.getEntityFromJson(
																					object,
																					EstateNodeInfo.class);
												if (node != null) {
													for (int j = 0; j < mAllEstates.size(); j++) {
														if (mAllEstates.get(j).getEstateid().equals(node.getEstateid())) {
															mAllEstates.get(j).setSelected(true);
															break;
														}
													}
													mDailiEstates.add(node);
													mSelectedEstates.add(node);
													mSelectedmarked.add(true);
												}
												
											} catch (InstantiationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
								
//								mFilteredEstates.clear();
//								for (int i = 0; i < mAllEstates.size(); i++) {
//									mFilteredEstates.add(mAllEstates.get(i));
//								}
								
								mDaiLiLouPanEstateListAdapter.notifyDataSetChanged();
								
								mTextView_tv_daili_num.setText(getSelectedNode());
								
								mListView_listview_loupan.postDelayed(new Runnable() {
									@Override
									public void run() {
										mListView_listview_loupan.onRefreshComplete();
									}
								}, 300);
								updateLoupanTextView();
							}
							else if (request_url.equals(ServerUrl.METHOD_queryListPage)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								try {
									mEstatePageNodeInfo = TransformUtil
											.getEntityFromJson(
													obj_data,
													PageNodeInfo.class);
									
									JSONArray array_data_allEstatePage = obj_data.optJSONArray("data");
									if (array_data_allEstatePage != null) {
										for (int i = 0; i < array_data_allEstatePage.length(); i++) {
											JSONObject object = array_data_allEstatePage.getJSONObject(i);
											if (object != null) {
												EstateNodeInfo node = TransformUtil
														.getEntityFromJson(
																object,
																EstateNodeInfo.class);
												if (node != null) {
													for (int j = 0; j < mSelectedEstates.size(); j++) {
														if (node.getEstateid().equals(mSelectedEstates.get(j).getEstateid())) {
															node.setSelected(true);
															break;
														}
													}
													mAllEstates.add(node);
												}
											}
										}
										
										mTextView_tv_daili_num.setText(getSelectedNode());
										mDaiLiLouPanEstateListAdapter.notifyDataSetChanged();
									}
								} catch (InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								mListView_listview_loupan.postDelayed(new Runnable() {
						            @Override
						            public void run() {
						            	mListView_listview_loupan.onRefreshComplete();
						            }
						        }, 300);
								
							}
							else if (request_url.equals(ServerUrl.METHOD_insertEkeempestatemodi)) {
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
	private ImageView mSearchBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_mine_profile_daili_setting);
		
		mCityName = GlobalSPA.getInstance(getApplicationContext()).getStringValueForKey(GlobalSPA.KEY_CITYID);
		
		initActivity();
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("val", mCityName);
			
			ClientHelper clientHelper = new ClientHelper(DaiLiChongZhiActivity.this,
					ServerUrl.METHOD_initAgent, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initActivity() {
		mRelativeLayout_rl_header = (RelativeLayout)findViewById(R.id.rl_header);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mTextView_tv_title = (TextView)findViewById(R.id.tv_title);
		mButton_btn_submit = (Button)findViewById(R.id.btn_submit);
		mRelativeLayout_rl_daili_top = (RelativeLayout)findViewById(R.id.rl_daili_top);
		mTextView_tv_daili_num = (TextView)findViewById(R.id.tv_daili_num);
		mTextView_tv_daili_can_not_find = (TextView)findViewById(R.id.tv_daili_can_not_find);
	/*	mLinearLayout_ll_daili_filter = (LinearLayout)findViewById(R.id.ll_daili_filter);
		mRelativeLayout_rl_daili_filter = (RelativeLayout)findViewById(R.id.rl_daili_filter);
		mImageView_iv_arrow_down = (ImageView)findViewById(R.id.iv_arrow_down);*/

		mEditText_et_content_find = (EditText)findViewById(R.id.et_content_find);
		mListView_listview_loupan = (PullToRefreshListView)findViewById(R.id.listview_loupan);
		
		mTextView_tv_daili_can_not_find.setOnClickListener(this);
		mButton_btn_submit.setOnClickListener(this);

		mTextView_quxian = (TextView)findViewById(R.id.chongzhi_quxian);
		mTextView_pianqu = (TextView)findViewById(R.id.chongzhi_pianqu);
		mTextView_loupan = (TextView)findViewById(R.id.chongzhi_loupan);

		mSearchBtn = (ImageView)findViewById(R.id.chongzhi_search);
		mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mAllEstates.clear();
				mEstatePageNodeInfo.setCurrentPage(1);

				refreshData(mEditText_et_content_find.getText().toString());
			}
		});

		mTextView_loupan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                    showRemoveDialog();
			}
		});
		
//		mImageView_iv_setting = (ImageView)findViewById(R.id.iv_setting);
//		mListView_lv_daililoupan = (PullToRefreshListView)findViewById(R.id.lv_daililoupan);
//		mListView_lv_daililoupan.setMode(Mode.PULL_FROM_END);
//		mListView_lv_daililoupan.setOnRefreshListener(new OnRefreshListener<ListView>() {
//
//			@Override
//			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
		/*mEditText_et_content_find.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
				String strSearch = s.toString();
				
				mAllEstates.clear();
				mEstatePageNodeInfo.setCurrentPage(1);
				
				refreshData(strSearch);
				
//				if (strSearch.equals("")) {
////					mFilteredEstates.clear();
////					if (mAllEstates != null) {
////						for (int i = 0; i < mAllEstates.size(); i++) {
////							EstateNodeInfo node = mAllEstates.get(i);
////							mFilteredEstates.add(node);
////						}	
////					}
//					mTextView_tv_daili_num.setText(getSelectedNode());
//					mDaiLiLouPanEstateListAdapter.notifyDataSetChanged();
//				}
//				else {
//					refreshData(strSearch);
//				}
			}
		});*/
		
		//mRelativeLayout_rl_daili_filter.setOnClickListener(this);
		
		mAllEstates.clear();
		
//		if (mAllEstates != null) {
//			for (int i = 0; i < mAllEstates.size(); i++) {
//				EstateNodeInfo node = mAllEstates.get(i);
//				
//				if (mSelectedEstates != null) {
//					for (int j = 0; j < mSelectedEstates.size(); j++) {
//						if (node.getEstateid().equals(mSelectedEstates.get(j).getEstateid())) {
//							node.setSelected(true);
//							break;
//						}
//					}
//				}
//				
//				mFilteredEstates.add(node);				
//			}			
//			
//			
//		}
		
		mDaiLiLouPanEstateListAdapter = new DaiLiLouPanSelectEstateListAdapter(DaiLiChongZhiActivity.this, 
				mAllEstates);
		mListView_listview_loupan.setAdapter(mDaiLiLouPanEstateListAdapter);
		
		mListView_listview_loupan.setMode(Mode.PULL_FROM_END);
		mListView_listview_loupan.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mListView_listview_loupan.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mListView_listview_loupan.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
				
		mListView_listview_loupan.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (mEstatePageNodeInfo.getCurrentPage() < mEstatePageNodeInfo.getTotalPages()) {
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
					
					int page = mEstatePageNodeInfo.getCurrentPage();
					mEstatePageNodeInfo.setCurrentPage(page+1);
					refreshData("");
				}
				else {
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setRefreshingLabel("没有更多数据");
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setPullLabel("没有更多数据");
					mListView_listview_loupan.getLoadingLayoutProxy(false, true).setReleaseLabel("没有更多数据");
					
					mListView_listview_loupan.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	mListView_listview_loupan.onRefreshComplete();
			            }
			        }, 500);
				}
			}
			
		});
		
		mListView_listview_loupan.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LinkedList<Integer>indexed = new LinkedList<Integer>();
				if (mAllEstates.get(position-1).isSelected()) {
					for (int i = 0; i < mSelectedEstates.size(); i++) {
						if (mSelectedEstates.get(i).getEstateid().equals(mAllEstates.get(position-1).getEstateid())) {
							indexed.add(i);
							mSelectedmarked.remove(i);
							break;
						}
					}
					for(int i=indexed.get(indexed.size()-1);i>=0;i--) {
						mSelectedmarked.remove(indexed.get(i).intValue());
					}
				}
				else {
					EstateNodeInfo node = new EstateNodeInfo();
					node.setEstateid(mAllEstates.get(position - 1).getEstateid());
					node.setEstatename(mAllEstates.get(position - 1).getEstatename());
					mSelectedEstates.add(node);
					mSelectedmarked.add(true);
				}
				updateLoupanTextView();
				mTextView_quxian.setText(mAllEstates.get(position-1).getDistrictname());
				mTextView_pianqu.setText(mAllEstates.get(position-1).getAreaname());
				mAllEstates.get(position-1).setSelected(!mAllEstates.get(position-1).isSelected());
				mEditText_et_content_find.setText(mAllEstates.get(position-1).getEstatename());

				mDaiLiLouPanEstateListAdapter.notifyDataSetChanged();		
				
				mTextView_tv_daili_num.setText(getSelectedNode());
			}
		});
		
	}
	private void updateLoupanTextView() {
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<mSelectedEstates.size();i++) {
			builder.append(mSelectedEstates.get(i).getEstatename());
			if(i < mSelectedEstates.size() - 1) {
				builder.append("/");
			}
		}
		mTextView_loupan.setText(builder.toString());
	}
	
	private void refreshData(String filter) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("txt", mCityName);
			obj.put("districtname", mDistrictname);
			obj.put("currentPage", mEstatePageNodeInfo.getCurrentPage());
			obj.put("pageSize", mEstatePageNodeInfo.getPageSize());
			obj.put("estatename", filter);
			
			ClientHelper clientHelper = new ClientHelper(DaiLiChongZhiActivity.this,
					ServerUrl.METHOD_queryListPage, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getSelectedNode() {
//		int count = 0;
//		for (int i = 0; i < mAllEstates.size(); i++) {
//			if (mAllEstates.get(i).isSelected()) {
//				count++;
//			}
//		}
		return "代理楼盘：" + mSelectedEstates.size() + "/" + mEstatePageNodeInfo.getTotalRecords();
	}

//	private void filterAction(String filter) {
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("districtname", mCityName);
//			obj.put("pageSize", 100);
//			obj.put("estatename", filter);
//			
//			ClientHelper clientHelper = new ClientHelper(DaiLiChongZhiActivity.this,
//					ServerUrl.METHOD_queryListPage, obj.toString(), mHandler);
//			clientHelper.setShowProgressMessage("正在获取数据...");
//			clientHelper.isShowProgress(true);
//			clientHelper.sendPost(true);
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private void submit() {
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < mSelectedEstates.size(); i++) {
			data.append(mSelectedEstates.get(i).getEstateid());
			if (i != mSelectedEstates.size()-1) {
				data.append("/");
			}
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("estateapply", data.toString());
			
			ClientHelper clientHelper = new ClientHelper(DaiLiChongZhiActivity.this,
					ServerUrl.METHOD_insertEkeempestatemodi, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在提交数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<Map<String, Object>> getDataClicked() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action", "全部");
        list.add(map);
        
        if (mListDistrict != null) {
			for (int i = 0; i < mListDistrict.size(); i++) {
				DistrictNodeInfo node = mListDistrict.get(i);
				 map = new HashMap<String, Object>();
			     map.put("action", node.getDistrictname());
			     list.add(map);
			}
		}
        
        return list;
	}
	private void showSubmitCommitDialog() {
		final Dialog dlg = new Dialog(DaiLiChongZhiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DaiLiChongZhiActivity.this);
		View viewContent = inflater.inflate(R.layout.chongzhi_commit_dialog, null);
		TextView commitContent = (TextView)viewContent.findViewById(R.id.commit_content);
		commitContent.setText(getResources().getString(R.string.chongzhi_content));
		TextView commitOk = (TextView)viewContent.findViewById(R.id.commit_ok);
		TextView commitCancel = (TextView)viewContent.findViewById(R.id.commit_cancel);
		commitOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
				dlg.dismiss();
			}
		});
		commitCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
	private void showRemoveDialog() {
		final Dialog dlg = new Dialog(DaiLiChongZhiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DaiLiChongZhiActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_daili_remove, null);

		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		removeAdapter adapter = new removeAdapter(this,mSelectedEstates,mSelectedmarked);
		listview.setAdapter(adapter);
		Button removeBtn = (Button)viewContent.findViewById(R.id.remove_btn);
		removeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*for(int i=0;i<mSelectedmarked.size();i++) {
					if(!mSelectedmarked.get(i)) {
						mSelectedEstates.remove(i);
					}
				}
				mSelectedmarked.clear();
				for(int i=0;i<mSelectedEstates.size();i++) {
					mSelectedmarked.add(true);
				}*/
				mSelectedEstates.clear();
				mSelectedmarked.clear();
				updateLoupanTextView();
				dlg.dismiss();
			}
		});
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

				}
				return false;
			}
		});

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
	class removeAdapter extends BaseAdapter {
		private Context context;
		private List<EstateNodeInfo> selectedEstates;
		private List<Boolean>selectedMarked;

		public removeAdapter(Context context, List<EstateNodeInfo> selectedEstates, List<Boolean> selectedMarked) {
			this.context = context;
			this.selectedEstates = selectedEstates;
			this.selectedMarked = selectedMarked;
		}

		@Override
		public int getCount() {
			return selectedEstates.size();
		}

		@Override
		public Object getItem(int position) {
			return selectedEstates.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final int index = position;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.remove_daili_item, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			boolean isSet = false;
			for (int j = 0; j < mDailiEstates.size(); j++) {
					if(mDailiEstates.get(j).getEstateid().equals(selectedEstates.get(position).getEstateid())) {
						isSet = true;
						break;
					}
			}
			if(isSet) {
				viewHolder.getTv_selected_mark.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.getTv_selected_mark.setVisibility(View.VISIBLE);
			}
            viewHolder.tv_selected_content.setText(selectedEstates.get(position).getEstatename());
			final TextView markedView = viewHolder.getTv_selected_mark;
		/*	viewHolder.tv_selected_content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectedMarked.get(position)) {
						selectedMarked.set(position, false);
						markedView.setVisibility(View.INVISIBLE);
					} else {
						markedView.setVisibility(View.VISIBLE);
						selectedMarked.set(position, true);
					}
				}
			});*/
			return convertView;
		}
	}
	public class ViewHolder {
		public TextView tv_selected_content;
		public TextView getTv_selected_mark;
		ViewHolder(View v) {
			tv_selected_content = (TextView)v.findViewById(R.id.daili_loupan_selected);
			getTv_selected_mark = (TextView)v.findViewById(R.id.loupan_select_mark);
		}
	}
	private void showDistrictSelectDlg() {
		final Dialog dlg = new Dialog(DaiLiChongZhiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DaiLiChongZhiActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_house_source_clicked, null);
		
		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		
		SimpleAdapter adapter = new SimpleAdapter(DaiLiChongZhiActivity.this, getDataClicked(), R.layout.layout_dailichongzhi_list_item,
                new String[]{"action"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mAllEstates.clear();
				mEstatePageNodeInfo.setCurrentPage(1);
				
				if (position == 0) {
					mTextView_tv_daili_select_district.setText("全部");
					mDistrictname = "";
//					if (mAllEstates != null) {
//						for (int i = 0; i < mAllEstates.size(); i++) {
//							EstateNodeInfo node = mAllEstates.get(i);
//							mFilteredEstates.add(node);
//						}	
//					}
//					mTextView_tv_daili_num.setText(getSelectedNode());
				}
				else {
					if (mListDistrict != null) {
						mTextView_tv_daili_select_district.setText(mListDistrict.get(position-1).getDistrictname());
						mDistrictname = mListDistrict.get(position-1).getDistrictname();					
					}
				}
				
				refreshData(mEditText_et_content_find.getText().toString().trim());
				
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
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
//		WindowManager.LayoutParams lp =window.getAttributes();
//		lp.width = dm.widthPixels*2/3;
//		window.setAttributes(lp);
		
		dlg.show();
	}

	private List<Map<String, Object>> getTePanLiShiData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        if (mListDistrict != null) {
			for (int i = 0; i < mListDistrict.size(); i++) {
				DistrictNodeInfo node = mListDistrict.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
			    map.put("item", node.getDistrictname());
			    list.add(map);
			}
		}
        
        return list;
	}
	
	private void showLouPanAddDlg() {
		final Dialog dlg = new Dialog(DaiLiChongZhiActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DaiLiChongZhiActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_mine_loupan_add, null);
		
		final ListView listview = (ListView)viewContent.findViewById(R.id.recycleview);
		
		SimpleAdapter adapter = new SimpleAdapter(DaiLiChongZhiActivity.this, getTePanLiShiData(), R.layout.layout_mine_loupan_add_list_item,
                new String[]{"item"},
                new int[]{R.id.tv_item});
		
		listview.setAdapter(adapter);
		
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
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(DaiLiChongZhiActivity.this, 60);
//		lp.height = dm.heightPixels = DensityUtil.dip2px(DaiLiChongZhiActivity.this, 100);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	/*	case R.id.rl_daili_filter:
		{
			showDistrictSelectDlg();
		}
			
			break;*/
			
		case R.id.tv_daili_can_not_find:
		{
			//showLouPanAddDlg();
			Intent intent = new Intent();
			intent.setClass(this,ChongzhiNotFindActivity.class);
			startActivityForResult(intent,0);
		}
			break;
			
		case R.id.btn_submit:
		{
			showSubmitCommitDialog();
		}
			break;
			
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			mAllEstates.clear();
			mEstatePageNodeInfo.setCurrentPage(1);
			refreshData("");
		} else {

		}
	}
}
