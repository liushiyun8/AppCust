package foundation.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.library.R;

import app.BaseAppContext;


public class ToastManager {
	private Toast toast = null;
	public static ToastManager manager = new ToastManager();

	private void createToast(Context mContext, String msg, int showTime,
			int gravity) {
		createToast(mContext, msg, showTime, gravity, 0, 0);
	}

	private void createToast(Context mContext, int msg, int showTime, int gravity) {
		createToast(mContext, msg, showTime, gravity, 0, 0);
	}

	@SuppressLint("InflateParams") private void createToast(Context mContext, String msg, int showTime,
			int gravity, int xOffset, int yOffset) {
		if (toast == null) {
			toast = Toast.makeText(mContext, msg, showTime);

			toast.setGravity(gravity, xOffset, yOffset);

		}
		LayoutInflater inflate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) inflate.inflate(R.layout.toast_info, null);

		toast.setView(v);
		TextView textView = (TextView) v.findViewById(R.id.txt_hint);
		textView.setText(msg);
		toast.show();

	}

	@SuppressLint("InflateParams") private void createToast(Context mContext, int msg, int showTime,
			int gravity, int xOffset, int yOffset) {
		if (toast == null) {
			toast = Toast.makeText(mContext, msg, showTime);

		} else {
			LayoutInflater inflate = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflate.inflate(R.layout.toast_info, null);
			toast.setView(v);
			TextView textView = (TextView) v.findViewById(R.id.txt_hint);
			textView.setText(msg);
			toast.setGravity(gravity, xOffset, yOffset);
			toast.show();
		}

	}

	public void createTopToast(Context mContext, String msg, int showTime) {
		createToast(mContext, msg, showTime, Gravity.CENTER, 0, -100);
	}

	public void createCenterToast(Context mContext, String msg, int showTime) {
		createToast(mContext, msg, showTime, Gravity.CENTER);
	}

	public void createCenterToast(Context mContext, int msg, int showTime) {
		createToast(mContext, msg, showTime, Gravity.CENTER);
	}

	public void createToast(Context mContext, int msg, int showTime) {
		createToast(mContext, msg, showTime, Gravity.CENTER, 0, 100);
	}

	public void createToast(Context mContext, String msg, int showTime) {
		createToast(mContext, msg, showTime, Gravity.CENTER, 0, 100);
	}

	public void show(Context mContext, String info) {
		createCenterToast(mContext, info, Toast.LENGTH_SHORT);

	}
	public void show(String info) {
		createCenterToast(BaseAppContext.getInstance(), info, Toast.LENGTH_SHORT);

	}


	public void show(Context mContext, int info) {

		createCenterToast(mContext, mContext.getResources().getString(info),
				Toast.LENGTH_SHORT);
	}
	public void show(int info) {

		createCenterToast(BaseAppContext.getInstance(), BaseAppContext.getInstance().getResources().getString(info),
				Toast.LENGTH_SHORT);
	}
}
