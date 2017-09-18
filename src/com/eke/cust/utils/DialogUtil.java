package com.eke.cust.utils;

import android.content.Context;

import com.eke.cust.widget.dialog.SweetAlertDialog;

public class DialogUtil {
	private Context context;
	private SweetAlertDialog progressdialog;
	private String message;
	private boolean cancelAble = true;
	
	public DialogUtil(Context context) {
		this.context = context;
	}
	
	public void setProgressMessage(String message) {
		this.message = message;
	}

	public void setIsCanCancelAble(boolean cancelable) {
		this.cancelAble = cancelable;
	}
	
	public void showProgressDialog() {
		progressdialog = null;
		progressdialog = new SweetAlertDialog(context,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText(message);
		progressdialog.setCancelable(cancelAble);
		if (progressdialog != null && !progressdialog.isShowing()) {
			progressdialog.setTitleText(message);
			progressdialog.show();
		}
	}

	public void dissmissProgressDialog() {
		if (progressdialog != null && progressdialog.isShowing()) {
			progressdialog.dismiss();
		}
	}
	
	public boolean getIsDlgShowing() {
		if (progressdialog != null) {
			return progressdialog.isShowing();
		}
		return false;
	}
}
