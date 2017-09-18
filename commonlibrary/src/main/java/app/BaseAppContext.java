package app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


/**
 * Created by wu on 16/4/7.
 */
public class BaseAppContext extends MultiDexApplication {
    private static BaseAppContext appContext;
    protected final static int DEFAULT_MEMMORY = 32;

    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
//        MultiDex.install(this);
    }
    public static BaseAppContext getInstance() {
        return appContext;
    }
}
