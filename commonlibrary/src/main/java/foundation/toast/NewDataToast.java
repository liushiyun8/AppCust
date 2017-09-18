package foundation.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.common.library.R;


/**
 * 新数据Toast提示控件
 */
public class NewDataToast extends Toast {

	public NewDataToast(Context context) {
		this(context, false);
	}

	public NewDataToast(Context context, boolean isSound) {
		super(context);

	}

	@Override
	public void show() {
		super.show();

	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param resId
	 *            文本资源oid

	 * @return
	 */
	public static NewDataToast makeText(Context context, int resId) {
		return makeText(context, context.getString(resId));
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param text
	 *            提示消息

	 * @return
	 */
 public static NewDataToast makeText(Context context, String text) {
		NewDataToast result = new NewDataToast(context);

		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		DisplayMetrics dm = context.getResources().getDisplayMetrics();

		View v = inflate.inflate(R.layout.new_data_toast, null);
		v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度

		TextView tv = (TextView) v.findViewById(R.id.new_data_toast_message);
		tv.setText(text);

		result.setView(v);
		result.setDuration(Toast.LENGTH_SHORT);
		// 显示最顶部
		result.setGravity(Gravity.TOP, 0, 0);

		return result;
	}
	@SuppressLint("InflateParams") public static NewDataToast makeBottomText(Context context, String text) {
		NewDataToast result = new NewDataToast(context);
		
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		
		View v = inflate.inflate(R.layout.new_data_toast, null);
		v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度
		
		TextView tv = (TextView) v.findViewById(R.id.new_data_toast_message);
		tv.setText(text);
		
		result.setView(v);
		result.setDuration(Toast.LENGTH_SHORT);
		// 显示最顶部
		result.setGravity(Gravity.BOTTOM, 0, 0);
		
		return result;
	}

}
