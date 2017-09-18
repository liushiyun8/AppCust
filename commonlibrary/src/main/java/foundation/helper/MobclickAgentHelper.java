package foundation.helper;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by wujian on 2015/9/24.
 */
public class MobclickAgentHelper {


    public static void onEvent(Context context, String id) {
        MobclickAgent.onEvent(context, id);
    }


}
