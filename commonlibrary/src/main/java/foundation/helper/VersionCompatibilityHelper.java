package foundation.helper;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.webkit.WebView;
import android.widget.ImageView;

public class VersionCompatibilityHelper {

	// 剪切板问题

	@SuppressWarnings("deprecation")
	public static void copy(Context ctx, String content) {
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) ctx
					.getSystemService(Context.CLIPBOARD_SERVICE);

			clipboardManager.setText(content);

		} else {
			ClipboardManager cmb = (ClipboardManager) ctx
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content);

		}
	}

	@SuppressWarnings("deprecation")
	public static void setBackground(ImageView imageView, Drawable drawable) {
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			imageView.setBackground(drawable);
		} else {
			imageView.setBackgroundDrawable(drawable);
		}
	}
	public static void setInitialScale(WebView appView) {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			appView.setInitialScale(101);
		} else {
			appView.setInitialScale(1);
		}
	}
}
