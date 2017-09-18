package com.eke.cust.tabhouse.error_report_activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class ErrorReportActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "ErrorReportActivity";
	private TextView mTextView_tv_house_source;
	private LinearLayout mLinearLayout_ll_error;
	private GridView mGridView_grid_view_error;
	private EditText mEditText_et_other_content;
	private Button mButton_btn_submit;
	
	private List<ErrorNodeInfo> mErrorNodeInfos = new ArrayList<ErrorNodeInfo>();
	
	private String[] errors = {"面积不对", "入住时间有误", "楼层不对", "业主电话有误", 
			"------", "房型不对", "朝向不对", "挂牌价不对", "虚假房源/业主联系不上", "其他"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_house_error_report);
		
		initActivity();
		
	}
	
	private void initActivity() {
		mTextView_tv_house_source = (TextView)findViewById(R.id.tv_house_source);
		mLinearLayout_ll_error = (LinearLayout)findViewById(R.id.ll_error);
		mGridView_grid_view_error = (GridView)findViewById(R.id.grid_view_error);
		mEditText_et_other_content = (EditText)findViewById(R.id.et_other_content);
		mButton_btn_submit = (Button)findViewById(R.id.btn_submit);

		mButton_btn_submit.setOnClickListener(this);
		
		mErrorNodeInfos.clear();
		for (int i = 0; i < errors.length; i++) {
			ErrorNodeInfo node = new ErrorNodeInfo();
			node.setName(errors[i]);
			
			mErrorNodeInfos.add(node);
		}
		
		final ErrorGridViewAdapter adapter = new ErrorGridViewAdapter(ErrorReportActivity.this, mErrorNodeInfos);
		mGridView_grid_view_error.setAdapter(adapter);
		
		mGridView_grid_view_error.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ErrorNodeInfo node1 = mErrorNodeInfos.get(position);
				Log.d(TAG, "pos: " + position);
				if (node1.isSelected()) {
					return;
				}
				
				for (int i = 0; i < mErrorNodeInfos.size(); i++) {
					ErrorNodeInfo node = mErrorNodeInfos.get(i);
					if (i == position) {
						node.setSelected(true);
					}
					else {
						node.setSelected(false);
					}
				}
				
				adapter.notifyDataSetChanged();
			}
		});
	}

	private int getErrorSelectedCount() {
		int count = 0;
		for (int i = 0; i < mErrorNodeInfos.size(); i++) {
			if (mErrorNodeInfos.get(i).isSelected()) {
				count++;
			}
		}
		
		return count;
	}
	
//	private void initTrackData() {
//		mTrackNodeInfos.clear();
//		for (int i = 0; i < 15; i++) {
//			TrackNodeInfo node = new TrackNodeInfo();
//			node.setDateTime("(8-2" + i + " 12:12)");
//			node.setName("中国 鸟ge");
//			node.setContent("书面合同已签");
//			
//			mTrackNodeInfos.add(node);
//		}
//		
//		mTrackListAdapter = new TrackListAdapter(ErrorReportActivity.this, mTrackNodeInfos);
//		mListView_listview_msg_genjin.setAdapter(mTrackListAdapter);
//	}
//
//	private List<Map<String, Object>> getDataClicked() {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < changyonggenjin.length; i++) {
//        	Map<String, Object> map = new HashMap<String, Object>();
//            map.put("action", changyonggenjin[i]);
//            list.add(map);
//		}
//        
//        return list;
//	}
//	
//	private void showChangyonggenjinDlg() {
//		final Dialog dlg = new Dialog(ErrorReportActivity.this, R.style.dialog);
//		LayoutInflater inflater = LayoutInflater.from(ErrorReportActivity.this);
//		View viewContent = inflater.inflate(R.layout.dlg_tab_house_source_clicked, null);
//		
//		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
//		
//		SimpleAdapter adapter = new SimpleAdapter(ErrorReportActivity.this, getDataClicked(), R.layout.layout_tab_house_action_list_item,
//                new String[]{"action"},
//                new int[]{R.id.tv_action});
//		
//		listview.setAdapter(adapter);
//		
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				switch (position) {
//				case 0:
//				{
//					
//				}
//					break;
//				
//
//				default:
//					break;
//				}
//			}
//		});
//		
//		dlg.setContentView(viewContent);
//		dlg.setCanceledOnTouchOutside(true);
//		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
//			
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(keyCode == KeyEvent.KEYCODE_BACK){ 
//					
//				}
//				return false;
//			}
//		});
//		
//		DisplayMetrics  dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		
//		Window window = dlg.getWindow();
//		window.setGravity(Gravity.CENTER);
////		WindowManager.LayoutParams lp =window.getAttributes();
////		lp.width = dm.widthPixels*2/3;
////		window.setAttributes(lp);
//		
//		dlg.show();
//	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_changyonggenjin:
		{
		}
			break;
			
		case R.id.btn_submit:
		{
			
		}
			break;
			
		default:
			break;
		}
	}
}
