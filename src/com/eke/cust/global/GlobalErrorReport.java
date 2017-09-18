package com.eke.cust.global;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.error_report_activity.ErrorGridViewAdapter;
import com.eke.cust.tabhouse.error_report_activity.ErrorNodeInfo;
import com.eke.cust.utils.DensityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GlobalErrorReport {
	public static final int MSG_CLOSE_ERROR_REPORT_DLG = 21200;
	public static final int ERROR_TYPE_PROPERTY = 0;	//房源纠错
	public static final int ERROR_TYPE_PROPERTYPIC = 1;	//房源图纠错
	public static final int ERROR_TYPE_ESTATEPIC = 2;	//楼盘图纠错
	private int mErrorType = 0;
	private String mId = "";
	private String mDesc = "";
	private Activity mActivity;
	private Context mContext;
	private Dialog dlg;
	private Handler mHandler;
	
	private List<ErrorNodeInfo> mErrorNodeInfos = new ArrayList<ErrorNodeInfo>();
	private String[] errors_fangyuan = {"房型/朝向/面积有误", "楼层不对", "挂牌价不对", "业主电话有误",
			"虚假房源/业主联系不上", "入住时间有误", "其它"};
	
	private String[] errors_kantu = {"取景太差，没法看", "图片打不开或模糊不清", "打开速度慢", "重复了", 
			"张冠李戴", "其它"};
	
	private Handler mHandler1 = new Handler() {
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
							if (request_url.equals(ServerUrl.METHOD_insertErrorCorrection)) {
								dlg.dismiss();
								Toast.makeText(mContext, "纠错成功!", Toast.LENGTH_SHORT).show();	
							}
						}
						else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg", "出错!");
							Toast.makeText(mContext.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(mContext.getApplicationContext(), "出错!", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case Constants.TAG_FAIL:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}
	};
	
	public GlobalErrorReport(Activity activity, Context context, Handler handler, String id, int errortype) {
		this.mActivity = activity;
		this.mContext = context;
		this.mHandler = handler;
		this.mId = id;
		this.mErrorType = errortype;
	}
	
	public Dialog showGlobalErrorReportDlg(int mErrorType) {
		dlg = new Dialog(this.mActivity, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(this.mActivity);
		View viewContent = inflater.inflate(R.layout.dlg_error_report, null);
		
		LinearLayout mLinearLayout_ll_error;
		ListView mListView_view_error;
		final EditText mEditText_et_other_content;
		Button mButton_btn_submit;
		
		mLinearLayout_ll_error = (LinearLayout)viewContent.findViewById(R.id.ll_error);
		mListView_view_error = (ListView)viewContent.findViewById(R.id.lv_error);
		mEditText_et_other_content = (EditText)viewContent.findViewById(R.id.et_other_content);
		mButton_btn_submit = (Button)viewContent.findViewById(R.id.btn_submit);
		
		String[] errors = null;
		switch (mErrorType) {
		case ERROR_TYPE_PROPERTY:
		{
			errors = errors_fangyuan;
		}
			break;
			
		case ERROR_TYPE_PROPERTYPIC:
		{
			errors = errors_kantu;
		}
			break;
			
		case ERROR_TYPE_ESTATEPIC:
		{
			errors = errors_kantu;
		}
			break;

		default:
			break;
		}
		mErrorNodeInfos.clear();
		for (int i = 0; i < errors.length; i++) {
			ErrorNodeInfo node = new ErrorNodeInfo();
			node.setName(errors[i]);
			
			mErrorNodeInfos.add(node);
		}
		
		final ErrorGridViewAdapter adapter = new ErrorGridViewAdapter(mActivity, mErrorNodeInfos);
		mListView_view_error.setAdapter(adapter);
		
		mListView_view_error.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ErrorNodeInfo node1 = mErrorNodeInfos.get(position);
				if (node1.isSelected()) {
					mDesc = node1.getName();
					return;
				}
				
				for (int i = 0; i < mErrorNodeInfos.size(); i++) {
					ErrorNodeInfo node = mErrorNodeInfos.get(i);
					if (i == position) {
						node.setSelected(true);
						mDesc = node.getName();
					}
					else {
						node.setSelected(false);
					}
				}
				
				adapter.notifyDataSetChanged();
			}
		});
		
		mButton_btn_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = mEditText_et_other_content.getText().toString().trim();
				if(content.length()>=6){
					uploadError(content);
				}else{
					ToastUtils.show(AppContext.getInstance(),"不能少于6个字");
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
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(mContext, 40);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
		
		return dlg;
	}
	
	private void uploadError(String more) {
		JSONObject obj = new JSONObject();
		try {
			String type = "";
			switch (mErrorType) {
			case ERROR_TYPE_PROPERTY:
			{
				type = "Property";
			}
				break;
				
			case ERROR_TYPE_PROPERTYPIC:
			{
				type = "PropertyPic";
			}
				break;
				
			case ERROR_TYPE_ESTATEPIC:
			{
				type = "EstatePic";
			}
				break;

			default:
				break;
				
				
			}
			
			obj.put("type", type);
			obj.put("targetid", mId);
			obj.put("detail", mDesc);
			obj.put("more", more);

			ClientHelper clientHelper = new ClientHelper(mContext,
					ServerUrl.METHOD_insertErrorCorrection, obj.toString(), mHandler1);
			clientHelper.setShowProgressMessage("正在处理...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
