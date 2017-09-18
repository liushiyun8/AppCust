package foundation.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class UIHelper {
	/**
	 * 简单的Activity 跳转
	 * @param context
	 * @param clazz  需要跳转的Activity
	 */
	public static void startActivity(Context context, Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		context.startActivity(intent);
	}


	public static void startActivity(Context context, Class<?> clazz, Serializable data) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		context.startActivity(intent);
	}



	/**
	 *
	 * @param context
	 * @param clazz
	 * @param
	 * @param data
	 */
	public static void startActivity(Context context, Class<?> clazz,String data ,String data1) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		intent.putExtra("data1", data1);
		context.startActivity(intent);
	}

	/**
	 *
	 * @param context
	 * @param clazz
	 * @param
	 * @param data
	 */
	public static void startActivity(Context context, Class<?> clazz,String data ,String data1,String data2) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		intent.putExtra("data1", data1);
		intent.putExtra("data2", data2);
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> clazz, Serializable data, Serializable data1) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		intent.putExtra("data1", data1);
		context.startActivity(intent);
	}
	public static void startActivity(Activity context,int requestCode, Class<?> clazz, Serializable data) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		context.startActivityForResult(intent,requestCode);
	}
	public static void startActivity(Activity context,int requestCode, Class<?> clazz, Serializable data, Serializable data1) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("data", data);
		intent.putExtra("data1", data1);
		context.startActivityForResult(intent,requestCode);
	}

	/**
	 * 简单的Activity 跳转  待标题
	 * @param context
	 * @param clazz  需要跳转的Activity
	 */
	public static void startLoginActivity(Context context, Class<?> clazz,String from) {
		Intent intent = new Intent(context, clazz);
		intent.putExtra("from",from);
		context.startActivity(intent);
	}

}
