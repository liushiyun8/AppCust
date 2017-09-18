package com.eke.cust.tabmine.beiyong_activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class OnGoingListActivity extends BaseActivity implements OnClickListener{
	public static final int MSG_NEXT = 0;
	private ListView mListView_listview_deal;
	private List<OnGoingNodeInfo> mNodeInfos = new ArrayList<OnGoingNodeInfo>();
	private OnGoingListAdapter mAdapter;
	
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg != null) {
				
				switch (msg.what) {
				case MSG_NEXT:
				{
					int index = msg.arg1;
					showDlg(index);
				}
					break;
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_mine_ongoing_list);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mListView_listview_deal = (ListView)findViewById(R.id.listview_ongoing);
		
		initData();
	}

	private void initData() {
		mNodeInfos.clear();
		
		for (int i = 0; i < 5; i++) {
			OnGoingNodeInfo node = new OnGoingNodeInfo();
			mNodeInfos.add(node);
		}
		
		mAdapter = new OnGoingListAdapter(OnGoingListActivity.this, mNodeInfos);
		mAdapter.setHandler(mHandler);
		mListView_listview_deal.setAdapter(mAdapter);
	}
	
	private List<Map<String, Object>> getDataClicked() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action", "合同已签");
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("action", "备用");
        list.add(map);
        
        return list;
	}
	
	private void showDlg(int index) {
		final Dialog dlg = new Dialog(OnGoingListActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(OnGoingListActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_house_source_clicked, null);
		
		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		
		SimpleAdapter adapter = new SimpleAdapter(OnGoingListActivity.this, getDataClicked(), R.layout.layout_tab_house_action_list_item,
                new String[]{"action"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
				{
					dlg.dismiss();
				}
					break;
					
				case 1:
				{
					dlg.dismiss();
//					Intent intent = new Intent(OnGoingListActivity.this, HouseTrackActivity.class);
//					startActivity(intent);
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
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		OnGoingListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
//		WindowManager.LayoutParams lp =window.getAttributes();
//		lp.width = dm.widthPixels*2/3;
//		window.setAttributes(lp);
		
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
