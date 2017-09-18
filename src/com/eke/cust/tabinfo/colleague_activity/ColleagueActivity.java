package com.eke.cust.tabinfo.colleague_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabmine.EstateNodeInfo;
import com.eke.cust.utils.MyLog;
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

public class ColleagueActivity extends BaseActivity {
	private static final String TAG = "ColleagueActivity";
	private PullToRefreshListView mListView;
	private List<ColleagueNodeInfo> mColleagueDetailNodeInfosList = new ArrayList<ColleagueNodeInfo>();
	private ColleagueListAdapter mColleagueListAdapter;
	private List<EstateNodeInfo> mUserDaiLiEstates;
	private int selectedEstateIndex = 0;

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
									.equals(ServerUrl.METHOD_selectEstateDL)) {
								mColleagueDetailNodeInfosList.clear();

								JSONArray jsonArray = jsonObject
										.optJSONArray("data");
								if (jsonArray != null) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = jsonArray
												.getJSONObject(i);

										ColleagueNodeInfo node;
										try {
											node = TransformUtil
													.getEntityFromJson(
															object,
															ColleagueNodeInfo.class);
											if (node != null) {
												mColleagueDetailNodeInfosList
														.add(node);
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

								showColleagueDetailDlg();
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
		setContentView(R.layout.activity_tab_info_colleague);

		initActivity();

	}

	private void initActivity() {
		mListView = (PullToRefreshListView) findViewById(R.id.listview_colleague);

		mUserDaiLiEstates =AppContext.getInstance().getUserDaiLiEstates();

		mListView.setMode(Mode.DISABLED);
		mListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		mListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mListView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");

		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

			}
		});

		if (mUserDaiLiEstates != null) {
			mColleagueListAdapter = new ColleagueListAdapter(
					ColleagueActivity.this, mUserDaiLiEstates, true);
			mListView.setAdapter(mColleagueListAdapter);
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < 1 || position > mUserDaiLiEstates.size()) {
					return;
				}

				MyLog.d(TAG, "pos = " + position);

				// TODO Auto-generated method stub
				JSONObject obj = new JSONObject();
				try {
					selectedEstateIndex = position - 1;

					obj.put("estateid", mUserDaiLiEstates.get(position - 1)
							.getEstateid());

					ClientHelper clientHelper = new ClientHelper(
							ColleagueActivity.this,
							ServerUrl.METHOD_selectEstateDL, obj.toString(),
							mHandler);
					clientHelper.setShowProgressMessage("正在获取数据...");
					clientHelper.isShowProgress(true);
					clientHelper.sendPost(true);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void getList() {
		JSONObject obj = new JSONObject();

		ClientHelper clientHelper = new ClientHelper(ColleagueActivity.this,
				ServerUrl.METHOD_selectEstateDL, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);

	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mColleagueDetailNodeInfosList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			ColleagueNodeInfo node = mColleagueDetailNodeInfosList.get(i);
			map.put("empname", node.getEmpname());
			map.put("empno", node.getEmpno());
			map.put("tel", node.getTel());
			map.put("guide", "带看：" + node.getGuide());
			map.put("contract", "签约：" + node.getContract());
			map.put("property", "跟房：" + node.getProperty());
			map.put("img", "发图：" + node.getImg());
			list.add(map);
		}

		return list;
	}

	private void showColleagueDetailDlg() {

		final Dialog dlg = new Dialog(ColleagueActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(ColleagueActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_info_colleage,
				null);

		final RelativeLayout rl_bg = (RelativeLayout) viewContent
				.findViewById(R.id.rl_bg);
		rl_bg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
			}
		});

		TextView tv_title = (TextView) viewContent.findViewById(R.id.tv_title);
		tv_title.setText(mUserDaiLiEstates.get(selectedEstateIndex)
				.getEstatename());

		final ListView listview = (ListView) viewContent
				.findViewById(R.id.recycleview);

		SimpleAdapter adapter = new SimpleAdapter(ColleagueActivity.this,
				getData(), R.layout.layout_colleague_detail_list_item,
				new String[] { "empname", "empno", "tel", "guide", "contract",
						"property", "img" }, new int[] { R.id.tv_name,
						R.id.tv_id, R.id.tv_tel, R.id.tv_gen, R.id.tv_sign,
						R.id.tv_daikan, R.id.tv_img });

		listview.setAdapter(adapter);

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
		ColleagueActivity.this.getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = dm.widthPixels;
		lp.height = dm.heightPixels;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
}
