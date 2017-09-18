package foundation.util;


import app.BaseAppContext;

/**
 * 常用单位转换的辅助类
 *
 * @author blueam
 */
public class DpUtil {



	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 *
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue) {
		float scale = BaseAppContext.getInstance().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}



}
