package com.eke.cust.utils;

import java.text.SimpleDateFormat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;

import com.eke.cust.R;
import com.eke.cust.exina.android.calendar.CalendarView;
import com.eke.cust.exina.android.calendar.Cell;



public class CalendarUtil {
	public static final int MSG_UPDATE_DATE_FROM = 10000;
	public static final int MSG_UPDATE_DATE_TO = 10001;
	public static final int MSG_UPDATE_DATE_OTHER = 10002;
	private static final String[] MONTHEN = {"Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.",
		"Sep.", "Oct.", "Nov.", "Dec."};
	private Context mContext;
	private Handler mHandler;
	
	private int mFromYear_prior_init = 0;
	private int mFromMonth_prior_init = 0;
	private int mFromDay_prior_init = 0;
	private int mToYear_prior_init = 0;
	private int mToMonth_prior_init = 0;
	private int mToDay_prior_init = 0;
	
	private int mFromYear = 0;
	private int mFromMonth = 0;
	private int mFromDay = 0;
	private int mToYear = 0;
	private int mToMonth = 0;
	private int mToDay = 0;
	
	private String mStrFromDate = null;
	private String mStrToDate = null;
	
	public CalendarUtil(Context context, Handler handler, String startDate, String toDate) {
		this.mContext = context;
		this.mHandler = handler;
		this.mStrFromDate = startDate;
		this.mStrToDate = toDate;
	}
	
	private void priorInitDate(final int from_or_to) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String date = sdf.format(new java.util.Date());
    	String []date1 = date.split("-");
    	if (date1.length == 3) {    
    		if (from_or_to == 0) {
    			if (this.mStrFromDate == null) {
    				mFromYear_prior_init = Integer.valueOf(date1[0]);
    				mFromMonth_prior_init = Integer.valueOf(date1[1]);
    				mFromDay_prior_init = Integer.valueOf(date1[2]);
				}
    			else {
    				String []date_parse = this.mStrFromDate.split("-");
    				mFromYear_prior_init = Integer.valueOf(date_parse[0]);
    				mFromMonth_prior_init = Integer.valueOf(date_parse[1]);
    				mFromDay_prior_init = Integer.valueOf(date_parse[2]);
    			}
			}
			else {
				if (this.mStrToDate == null) {
					mToYear_prior_init = Integer.valueOf(date1[0]);
					mToMonth_prior_init = Integer.valueOf(date1[1]);
					mToDay_prior_init = Integer.valueOf(date1[2]);
				}
				else {
					String []date_parse = this.mStrToDate.split("-");
					mToYear_prior_init = Integer.valueOf(date_parse[0]);
					mToMonth_prior_init = Integer.valueOf(date_parse[1]);
					mToDay_prior_init = Integer.valueOf(date_parse[2]);
				}
			}
    		
    	}
    }
	
	public void showDateSelectDlg(final int from_or_to) {
		final Dialog dlg = new Dialog(mContext, R.style.date_select_dialog);
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View viewContent = inflater.inflate(R.layout.calendar_layout, null);
		final CalendarView calendar = (CalendarView)viewContent.findViewById(R.id.calendar_item);
		final Button btnPrior = (Button)viewContent.findViewById(R.id.btnLeft);
		final Button btnCenter = (Button)viewContent.findViewById(R.id.btnCenter);
		final Button btnNext = (Button)viewContent.findViewById(R.id.btnRight);
		
		priorInitDate(from_or_to);
		
		if (from_or_to == 0) {
			calendar.initCalendarDate(mFromYear_prior_init, mFromMonth_prior_init, mFromDay_prior_init);
		}
		else {
			calendar.initCalendarDate(mToYear_prior_init, mToMonth_prior_init, mToDay_prior_init);
		}
		
		btnCenter.setText(calendar.getYear() + "-" + String.format("%02d", (calendar.getMonth() + 1)));
//		btnCenter.setText(MONTHEN[calendar.getMonth()] + " " + calendar.getYear());
		
		btnPrior.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				calendar.previousMonth(); 
				btnCenter.setText(calendar.getYear() + "-" + String.format("%02d", (calendar.getMonth() + 1)));
//				btnCenter.setText(MONTHEN[calendar.getMonth()] + " " + calendar.getYear());
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				calendar.nextMonth(); 
				btnCenter.setText(calendar.getYear() + "-" + String.format("%02d", (calendar.getMonth() + 1)));
//				btnCenter.setText(MONTHEN[calendar.getMonth()] + " " + calendar.getYear());
			}
		});
		
		calendar.setOnCellTouchListener(new CalendarView.OnCellTouchListener() {
			
			@Override
			public void onTouch(Cell cell) {
				// TODO Auto-generated method stub
				if(cell.mPaint.getColor() == Color.GRAY) {
					// 这是上月的
					calendar.previousMonth(); 
					btnCenter.setText(calendar.getYear() + "-" + String.format("%02d", (calendar.getMonth() + 1)));
				} 
				else if(cell.mPaint.getColor() == Color.LTGRAY) {
					// 下月的
					calendar.nextMonth(); 
					btnCenter.setText(calendar.getYear() + "-" + String.format("%02d", (calendar.getMonth() + 1)));
				} 
				else {  //  本月的
//						Intent ret = new Intent();
//						ret.putExtra("year", calendar.getYear());
//						ret.putExtra("month", calendar.getMonth());
//						ret.putExtra("day", cell.getDayOfMonth());	
						
						if (from_or_to == 0) {
							mFromYear_prior_init = calendar.getYear();
							mFromMonth_prior_init  = calendar.getMonth() + 1;
							mFromDay_prior_init  = cell.getDayOfMonth();
						}
						else {
							mToYear_prior_init  = calendar.getYear();
							mToMonth_prior_init  = calendar.getMonth() + 1;
							mToDay_prior_init  = cell.getDayOfMonth();
						}
						// 在此让当前的View 重绘一次
						Rect ecBounds = cell.getBound();
						calendar.getDate();
						calendar.mDecoraClick.setBounds(ecBounds);
						calendar.mDecoration = null;//不显示今天的标识
						calendar.invalidate();					
				}
			}
		});
		
		ViewStub viewstub = (ViewStub)viewContent.findViewById(R.id.viewstub);
		viewstub.inflate();
		Button btn_suishiruzhu = (Button)viewContent.findViewById(R.id.btn_suishiruzhu);
		Button btn_riqidaiding = (Button)viewContent.findViewById(R.id.btn_riqidaiding);
		
		btn_suishiruzhu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_UPDATE_DATE_OTHER;
				Bundle bundle = new Bundle();
				bundle.putString("data", "随时入住");
				msg.setData(bundle);
				mHandler.sendMessage(msg);
				
				dlg.dismiss();	
			}
		});
		
		btn_riqidaiding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_UPDATE_DATE_OTHER;
				Bundle bundle = new Bundle();
				bundle.putString("data", "日期待定");
				msg.setData(bundle);
				mHandler.sendMessage(msg);
				
				dlg.dismiss();	
			}
		});
		
		Button btnConfirm = (Button)viewContent.findViewById(R.id.btn_confirm);
//		Button btnCancel = (Button)viewContent.findViewById(R.id.btn_cancel);
		
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (from_or_to == 0) {
					mFromYear = mFromYear_prior_init;
					mFromMonth = mFromMonth_prior_init;
					mFromDay = mFromDay_prior_init;
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_UPDATE_DATE_FROM;
					Bundle bundle = new Bundle();
					bundle.putInt("year", mFromYear);
					bundle.putInt("month", mFromMonth);
					bundle.putInt("day", mFromDay);
					msg.setData(bundle);
					mHandler.sendMessage(msg);
				}
				else {
					mToYear = mToYear_prior_init;
					mToMonth = mToMonth_prior_init;
					mToDay = mToDay_prior_init;
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_UPDATE_DATE_TO;
					Bundle bundle = new Bundle();
					bundle.putInt("year", mToYear);
					bundle.putInt("month", mToMonth);
					bundle.putInt("day", mToDay);
					msg.setData(bundle);
					mHandler.sendMessage(msg);
				}
				
				dlg.dismiss();					
			}
		});
		
//		btnCancel.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				dlg.dismiss();	
////				Toast.makeText(mContext.getApplicationContext(), "Date not changed.", Toast.LENGTH_SHORT).show();
//			}
//		});
		
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(viewContent);    		
		dlg.show();    	
	}
}
