package foundation.toast;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import app.BaseAppContext;


/**
 * 自定义Toast工具类    
 * 
 * @author blueam 
 *     
 */   
public class ToastUtil {  

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;
    private static Object synObj = new Object();
	
	/**
	 * 优化Toast的显示方式，可以立即弹出下一条Toast而不是等上一条toast消失后再显示
	 * 不用考虑是否在主线程，直接使用
	 * @param msg 提示的文字
	 */
    public static void showToast(final String msg) {
        new Thread(new Runnable() {
            public void run() {  
                handler.post(new Runnable() {
                    @Override
                    public void run() {  
                        synchronized (synObj) {  
                            if (toast != null) {  
                                toast.cancel();  
                            } 
                            toast = Toast.makeText(BaseAppContext.getInstance(), msg, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();  
                        }  
                    }  
                });  
            }  
        }).start();  
    }

    public static void showToast(final int msg) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(BaseAppContext.getInstance(), BaseAppContext.getInstance().getResources().getString(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }
}  
