package com.eke.cust.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/1.
 */

public class Headers {
    public static Map<String,String> getHttpHeaders(){
            Map<String,String> headers = new HashMap<String,String>();
//        headers.put("X-Udid","");
            headers.put("X-Os","android");
            headers.put("X-Os-Version","7.0");
            headers.put("X-App-Version","2.0.0");
//        headers.put("X-Http-Version","1.1");
            String str = "ekeaeX-Os"+headers.get("X-Os")+"X-Os-Version"+headers.get("X-Os-Version")+"X-App-Version"+headers.get("X-App-Version");
//        headers.put("X-Authorization", com.eke.cust.utils.MD5.md5(str));
            headers.put("X-Authorization","FEF58BAE6C1BC089FDAFE19657518C98");

//        headers.put("X-Position","11.22,44.66");
            return headers;

    }
}
