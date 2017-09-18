package com.eke.cust.tabmine.deal_activity;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.upload_activity.UploadActivity;
import com.eke.cust.utils.DensityUtil;
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
import java.util.List;
import java.util.Map;

public class DealListActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "DealListActivity";
	private PullToRefreshListView mListView_listview_deal;
	private List<MyDealNodeInfo> mNodeInfos = new ArrayList<MyDealNodeInfo>();
	private MineDealListAdapter mAdapter;
	private int selectDealIndex = 0;
	private List<String> bankList = new ArrayList<String>();
	
	private int currentPage = 1;
	private int pageSize = 20;
	private int totalRecords = 0;
	private String orderColumn = null;
	private boolean orderASC = false;
	private int recordStart = 0;
	private int totalPages = 0;
	
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
							if (request_url.equals(ServerUrl.METHOD_getMyContract)) {
								
								JSONObject obj_data = jsonObject.getJSONObject("data");
								
								currentPage = obj_data.optInt("currentPage");
								pageSize = obj_data.optInt("pageSize");
								totalRecords = obj_data.optInt("totalRecords");
								orderColumn = obj_data.optString("orderColumn");
								orderASC = obj_data.optBoolean("orderASC");
								
								recordStart = obj_data.optInt("recordStart");
								totalPages = obj_data.optInt("totalPages");
								
								JSONArray jsonArray = obj_data.optJSONArray("data");
								if (jsonArray != null) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = jsonArray.getJSONObject(i);
										
										MyDealNodeInfo node;
										try {
											node = TransformUtil.getEntityFromJson(
																			object,
																			MyDealNodeInfo.class);
											if (node != null) {
												mNodeInfos.add(node);
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
								
								mAdapter.notifyDataSetChanged();
								
								mListView_listview_deal.postDelayed(new Runnable() {
						            @Override
						            public void run() {
						            	mListView_listview_deal.onRefreshComplete();
						            }
						        }, 300);
							}
							else if (request_url.equals(ServerUrl.METHOD_updateContractBySK)) {
								Toast.makeText(getApplicationContext(), "提交成功!", Toast.LENGTH_SHORT).show();
							}
							else if (request_url.equals(ServerUrl.METHOD_queryListBank)) {
								bankList.clear();
								JSONArray jsonArray = jsonObject.optJSONArray("data");
								if (jsonArray != null) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = jsonArray.getJSONObject(i);
										bankList.add(object.optString("bankname"));
									}
								}
								showYeZhuShouKuanZhanghaoDlg();
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
		setContentView(R.layout.activity_tab_mine_deal_list);
		
		initActivity();
		
		refreshData();
		//showActionDlg(-1);
	}
	public void houseSeeClick() {

	}
	public void heduiClick() {

	}
	public void uploadHetongClick() {

	}
	
	private void initActivity() {
		mListView_listview_deal = (PullToRefreshListView)findViewById(R.id.listview_deal);
		mListView_listview_deal.setMode(Mode.PULL_FROM_END);
		mListView_listview_deal.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mListView_listview_deal.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mListView_listview_deal.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
				
		mListView_listview_deal.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (currentPage < totalPages) {
					currentPage ++;
					refreshData();
				}
				else {
					mListView_listview_deal.getLoadingLayoutProxy(false, true).setRefreshingLabel("没有更多数据");
					mListView_listview_deal.getLoadingLayoutProxy(false, true).setPullLabel("没有更多数据");
					mListView_listview_deal.getLoadingLayoutProxy(false, true).setReleaseLabel("没有更多数据");
					
					mListView_listview_deal.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	mListView_listview_deal.onRefreshComplete();
			            }
			        }, 500);
				}
			}
			
		});
		
		initData();
	}

	private void refreshData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("currentPage", currentPage);
			obj.put("pageSize", pageSize);
			
			ClientHelper clientHelper = new ClientHelper(DealListActivity.this,
					ServerUrl.METHOD_getMyContract, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initData() {
		mNodeInfos.clear();
		
//		for (int i = 0; i < 5; i++) {
//			MyDealNodeInfo node = new MyDealNodeInfo();
//			mNodeInfos.add(node);
//		}
		
		mAdapter = new MineDealListAdapter(DealListActivity.this, mNodeInfos);
		mListView_listview_deal.setAdapter(mAdapter);
		
		mListView_listview_deal.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectDealIndex = position-1;
				showActionDlg(selectDealIndex);
			}
		});
	}
	
	private void showActionDlg(final int position) {
		final Dialog dlg = new Dialog(DealListActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DealListActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_mine_deal_acion_choose, null);
		
		final Button btn_hetongxiangqing = (Button)viewContent.findViewById(R.id.btn_hetongxiangqing);
		final Button btn_yezhushoukuan = (Button)viewContent.findViewById(R.id.btn_yezhushoukuan);
		final Button btn_uploadhetong = (Button)viewContent.findViewById(R.id.btn_upload_hetong);
		
		btn_hetongxiangqing.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
			}
		});
		
		btn_yezhushoukuan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				
				JSONObject obj = new JSONObject();
				ClientHelper clientHelper = new ClientHelper(DealListActivity.this,
						ServerUrl.METHOD_queryListBank, obj.toString(), mHandler);
				clientHelper.setShowProgressMessage("正在获取数据...");
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);
			}
		});
		btn_uploadhetong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DealListActivity.this, UploadActivity.class);
				if(position != -1) {
					intent.putExtra("foreignId", mNodeInfos.get(position).getContractid());
					intent.putExtra("propertyid", mNodeInfos.get(position).getPropertyno());
					//intent.putExtra("estateid", mNodeInfos.get(position).getEstateid());
					intent.putExtra("estateinfo", "【" + mNodeInfos.get(position).getEstatename() + "】" + mNodeInfos.get(position).getBuildno() + mNodeInfos.get(position).getRoomno());
				}
				intent.putExtra("type", "deallist");
				startActivity(intent);
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
		DealListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels*2/3;
//		lp.height = dm.heightPixels;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < bankList.size(); i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("bank", bankList.get(i));
            list.add(map);
		}
        
        return list;
	}
	
	private PopupWindow pw;
	private int selectedBankIndex = -1;
	private void showYeZhuShouKuanZhanghaoDlg() {
		selectedBankIndex = -1;
		
		final Dialog dlg = new Dialog(DealListActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(DealListActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_mine_deal_yezhushoukuan_zhanghao, null);
		
		final Button btn_submit = (Button)viewContent.findViewById(R.id.btn_submit);
		final TextView tv_bank;
		final EditText mEditText_et_shoukuanzhanghao;
		final EditText mEditText_et_shoukuanren_name;
		final EditText mEditText_et_yezhu_tel;
		
		tv_bank = (TextView)viewContent.findViewById(R.id.tv_bank);
		mEditText_et_shoukuanzhanghao = (EditText)viewContent.findViewById(R.id.et_shoukuanzhanghao);
		mEditText_et_shoukuanren_name = (EditText)viewContent.findViewById(R.id.et_shoukuanren_name);
		mEditText_et_yezhu_tel = (EditText)viewContent.findViewById(R.id.et_yezhu_tel);
		
		tv_bank.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_bank.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.increment_up), null);
				
				DisplayMetrics  dm = new DisplayMetrics();
				DealListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
				
				View myView = getLayoutInflater().inflate(R.layout.pop_bank_list, null);
                pw = new PopupWindow(myView, tv_bank.getMeasuredWidth(), LayoutParams.WRAP_CONTENT, true);
                pw.setFocusable(true);
                //将window视图显示在tv_bank下面
                pw.showAsDropDown(tv_bank);
                ListView lv = (ListView) myView.findViewById(R.id.lv_pop_bank);
                SimpleAdapter adapter = new SimpleAdapter(DealListActivity.this, getData(), R.layout.layout_spinner_bank_list_item,
		                new String[]{"bank"},
		                new int[]{R.id.tv_bank_name});
				
				lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                    	selectedBankIndex = position;
                    	tv_bank.setText(bankList.get(position));
                    	tv_bank.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.decrement_down), null);
                        pw.dismiss();
                    }
                });
			}
		});
		
		btn_submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectedBankIndex == -1) {
					Toast.makeText(getApplicationContext(), "请选择一个银行!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String account = mEditText_et_shoukuanzhanghao.getText().toString().trim();
				if (account.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入收款账号!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String name = mEditText_et_shoukuanren_name.getText().toString().trim();
				if (name.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入收款人姓名!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String tel = mEditText_et_yezhu_tel.getText().toString().trim();
				if (tel.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入业主登记手机号码!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				dlg.dismiss();
				
				JSONObject obj = new JSONObject();
				try {
					obj.put("contractid", mNodeInfos.get(selectDealIndex).getContractid());
					obj.put("bank", bankList.get(selectedBankIndex));
					obj.put("account", account);
					obj.put("username", name);
					obj.put("tel", tel);
					
					ClientHelper clientHelper = new ClientHelper(DealListActivity.this,
							ServerUrl.METHOD_updateContractBySK, obj.toString(), mHandler);
					clientHelper.setShowProgressMessage("正在获取数据...");
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
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		DealListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(getApplicationContext(), 60);
//		lp.height = dm.heightPixels;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		default:
			break;
		}
	}
}
